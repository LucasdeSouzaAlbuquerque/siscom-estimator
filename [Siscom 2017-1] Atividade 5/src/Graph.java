public class Graph {
	private String title;
	private double[] xValues;
	private double[] yValues;

	public Graph(String estimator, double[] numTags, double[] result) {
		this.title = estimator;
		this.xValues = numTags;
		this.yValues = result;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double[] getxValues() {
		return xValues;
	}

	public void setxValues(double[] xValues) {
		this.xValues = xValues;
	}

	public double[] getyValues() {
		return yValues;
	}

	public void setyValues(double[] yValues) {
		this.yValues = yValues;
	}
}
