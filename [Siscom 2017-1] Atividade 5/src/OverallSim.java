import java.util.Scanner;

public class OverallSim {

	public static double[][][] totArr;
	public static double[][] arr;
	public static String[] names;
	public static String[] listCheck = { "Número de Slots", "Número de Slots em Colisão", "Número de Slots Vazios",
			"Tempo de Simulação" };
	//public static String[] estimators = { "q" };

	public static void main(String[] args) {

		int count = 0;
		Scanner in = new Scanner(System.in);
		System.out.println("SIMULADOR!!!!!!!!!!!");
		System.out.println("Digite a quantidade de tags iniciais (Recomendado: 100)");
		int inTag = in.nextInt();
		System.out.println("Digite o incremento da quantidade de etiquetas (Recomendado: 100)");
		int inc = in.nextInt();
		System.out.println("Digite a quantidade de tags máxima (Recomenado: 1000)");
		int enTag = in.nextInt();
		System.out.println("Digite a quantidade de simulações por quantidade (Recomendado: 2000)");
		int epochs = in.nextInt();
		System.out.println("Digite o tamanho do quadro inicial (Recomendado: 64)");
		int frame = in.nextInt();
		in.nextLine();
		if(inc == 0){ inc = 1; }
		totArr = new double[1][((enTag - inTag) / inc) + 1][5];
		names = new String[1];
		while (true) {
			System.out.println("Digite o protocolo (lb/eom/q, qualquer outra coisa encerra o programa)");

			String nP = in.nextLine().toLowerCase();
			//String nP = estimators[count];
			
			if (!nP.equals("lb") && !nP.equals("eom") && !nP.equals("q")) {
				break;
			}
			count += 1;

			arr = new double[((enTag - inTag) / inc) + 1][5];
			for (int i = 0; i <= ((enTag - inTag) / inc); i++) {

				@SuppressWarnings("unused")
				Simulator simulator = new Simulator(nP, (inTag + (i * inc)), frame, epochs);
				double[] values = Simulator.simulate();
				arr[i] = values;
				System.out.println("Para " + (inTag + (i * inc)) + " tags!");
				System.out.println(values[0] + " - Tags Sent");
				System.out.println(values[1] + " - Collision Slots");
				System.out.println(values[2] + " - Empty Slots");
				System.out.println(values[3] + " - Total Slots");
				System.out.println(values[4] + " - Time for Simulation");
				System.out.println();
				arr[i][0] = (inTag + (i * inc));
			}

			double[][][] tempArr = totArr;
			String[] tempNames = names;
			totArr = new double[count][((enTag - inTag) / inc) + 1][5];
			names = new String[count];
			for (int i = 0; i < tempArr.length; i++) {
				totArr[i] = tempArr[i];
				names[i] = tempNames[i];
			}
			totArr[(count - 1)] = arr;
			names[(count - 1)] = nP.toUpperCase();

			/* TagsSent -> CollSlots -> EmptSlots -> TotSlots -> SimTime */
		}

		// totArr (cada sim tem uma array de sims pra numero de tags com uma
		// array de valores)
		// String[] names = {"FAST-Q"};
		for (int i = 0; i < 4; i++) {
			new GraphRender(totArr, listCheck[i], inTag, inc, enTag, names).render();
		}
		in.close();
	}

}
