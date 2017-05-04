import java.util.Random;
import java.lang.Math;

public class Simulator {

	public static String type;
	public static int numTags = 1000;
	public static int initFrame = 64;
	public static int iter = 2000;
	public final static double THRESHOLD = 0.001;

	public static int avgCollSlots = 0;
	public static int avgEmptSlots = 0;
	public static int avgSentTags = 0;
	public static int avgTotSlots = 0;
	public static double avgTime = 0;

	public static double y = 2;
	public static double b = Double.POSITIVE_INFINITY;

	static Random rand = new Random();

	public Simulator(String type, int numTags, int initFrame, int iter) {
		Simulator.type = type;
		Simulator.numTags = numTags;
		Simulator.initFrame = initFrame;
		Simulator.iter = iter;
	}

	public static double[] qSim() {

		for (int e = 0; e < iter; e++) {

			double init = System.currentTimeMillis();
			int sentTags = 0, collSlots = 0, emptSlots = 0, totSlots = 0;
			int q = 4, pq = 4;
			double qfp = 4;

			double Ccoll = 0.21183, Cidle = 0.15;

			int currFrame = (int) Math.pow(2, q);
			Boolean[] tags = genTags(numTags);
			int[] timeslot = genTimeSlot(numTags);

			while (sentTags != tags.length) {
				q = (int) Math.round(qfp);
				// System.out.println("TotSlots: " + totSlots + ", q: " + q + ",
				// pq: " + pq + ", qfp: "+qfp+", Tags Sent: "+sentTags);
				if (totSlots == 0 || q != pq) {
					currFrame = (int) Math.pow(2, q);
					timeslot = updateTimeSlot(tags, timeslot, currFrame);
				} else {
					timeslot = decrementTimeSlot(tags, timeslot);
				}
				pq = q;
				int count = 0;
				int pos = 0;
				totSlots += 1;
				for (int i = 0; i < tags.length; i++) {
					if (tags[i] == false && timeslot[i] == 0) {
						count += 1;
						pos = i;
						if (count == 2) {
							collSlots += 1;
							qfp = Math.min(15, (qfp + Ccoll));
							i = tags.length;
						}
					}
				}

				if (count == 1) {
					sentTags += 1;
					tags[pos] = true;
				} else if (count == 0) {
					qfp = Math.max(0, (qfp - Cidle));
					emptSlots += 1;
				}

			}

			avgCollSlots += collSlots;
			avgEmptSlots += emptSlots;
			avgTotSlots += totSlots;
			avgSentTags += sentTags;
			avgTime += (System.currentTimeMillis() - init);
		}

		double[] returnee = { (avgSentTags / iter), (avgCollSlots / iter), (avgEmptSlots / iter), (avgTotSlots / iter),
				(avgTime / iter) };
		return returnee;
	}

	public static double[] simulate() {

		avgCollSlots = 0;
		avgEmptSlots = 0;
		avgSentTags = 0;
		avgTotSlots = 0;
		avgTime = 0;

		if (type.equals("q")) {
			return (qSim());
		} else {
			for (int e = 0; e < iter; e++) {

				double init = System.currentTimeMillis();

				if (type.equals("eom")) {
					y = 2;
					b = Double.POSITIVE_INFINITY;
				}

				int sentTags = 0, collSlots = 0, emptSlots = 0, totSlots = 0;
				int currFrame = initFrame;
				Boolean[] tags = genTags(numTags);
				int[] timeslot = genTimeSlot(numTags);

				while (sentTags != tags.length) {

					int currSentTags = 0;
					totSlots += currFrame;
					timeslot = updateTimeSlot(tags, timeslot, currFrame);

					int currCt = 0;
					for (int t = 0; t < currFrame; t++) {
						int count = 0;
						int pos = 0;
						for (int i = 0; i < tags.length; i++) {
							if (tags[i] == false && timeslot[i] == t) {
								count += 1;
								pos = i;
								if (count == 2) {
									currCt += 1;
									i = tags.length;
								}
							}
						}

						if (count == 1) {
							currSentTags += 1;
							tags[pos] = true;
						} else if (count == 0) {
							emptSlots += 1;
						}

					}
					collSlots += currCt;
					sentTags += currSentTags;

					// estimator
					if (type.equals("lb")) {
						currFrame = lowerBound(currCt);
					}
					if (type.equals("eom")) {
						currFrame = (int) Math.ceil(eomlee(currFrame, currCt, currSentTags));
					}

				}
				avgCollSlots += collSlots;
				avgEmptSlots += emptSlots;
				avgTotSlots += totSlots;
				avgSentTags += sentTags;
				avgTime += (System.currentTimeMillis() - init);
			}
			double[] returnee = { (avgSentTags / iter), (avgCollSlots / iter), (avgEmptSlots / iter),
					(avgTotSlots / iter), (avgTime / iter) };
			return returnee;
		}

	}

	public static double eomlee(int currFrame, int collSlots, int currSentTags) {
		for (int i = 0;; i++) {
			b = currFrame / ((y * collSlots) + currSentTags);
			double reuse = 1 / b;
			double prevy = y;
			y = (1 - Math.exp(-reuse)) / (b * (1 - ((1 + reuse) * Math.exp(-reuse))));
			if (Math.abs(prevy - y) < THRESHOLD) {
				break;
			}
		}
		return (y * collSlots);
	}

	public static Boolean[] genTags(int numTags) {
		Boolean[] tags = new Boolean[numTags];
		for (int i = 0; i < tags.length; i++) {
			tags[i] = false;
		}
		return tags;
	}

	public static int[] genTimeSlot(int numTags) {
		int[] timeslot = new int[numTags];
		for (int i = 0; i < timeslot.length; i++) {
			timeslot[i] = -1;
		}
		return timeslot;
	}

	public static int[] updateTimeSlot(Boolean[] tags, int[] timeslot, int currFrame) {
		for (int i = 0; i < tags.length; i++) {
			if (tags[i] == false) {
				timeslot[i] = rand.nextInt(currFrame);
			}
		}
		return timeslot;
	}

	public static int[] decrementTimeSlot(Boolean[] tags, int[] timeslot) {
		for (int i = 0; i < tags.length; i++) {
			if (tags[i] == false) {
				timeslot[i] = timeslot[i] - 1;
			}
		}
		return timeslot;
	}

	public static int lowerBound(int collisionSlots) {
		return (collisionSlots * 2);
	}

}
