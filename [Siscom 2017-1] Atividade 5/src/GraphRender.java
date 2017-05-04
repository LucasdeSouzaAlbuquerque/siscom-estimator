import java.util.ArrayList;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class GraphRender {

	public String xAxis = "Number Of Tags";
	public String yAxis;
	public String graphTitle;

	public int inTag;
	public int inc;
	public int enTag;

	private ArrayList<Graph> graphs;

	public GraphRender(double[][][] results, String type, int inTag, int inc, int enTag, String[] est) {

		this.inTag = inTag;
		this.inc = inc;
		this.enTag = enTag;
		this.graphTitle = "GraphRender";

		graphs = new ArrayList<Graph>();

		xAxis = "Number Of Tags";
		yAxis = type;

		double xValues[] = new double[((enTag - inTag) / inc) + 1];
		for (int i = 0; i <= ((enTag - inTag) / inc); i++) {
			xValues[i] = (inTag + (i * inc));
		}

		switch (type) {
		case "Número de Slots":
			for (int i = 0; i < results.length; i++) {
				double[] yValues = vals(results[i], 3);
				Graph graph = new Graph(est[i], xValues, yValues);
				graphs.add(graph);
			}
			break;
		case "Número de Slots em Colisão":
			for (int i = 0; i < results.length; i++) {
				double[] yValues = vals(results[i], 1);
				Graph graph = new Graph(est[i], xValues, yValues);
				graphs.add(graph);
			}
			break;
		case "Número de Slots Vazios":
			for (int i = 0; i < results.length; i++) {
				double[] yValues = vals(results[i], 2);
				Graph graph = new Graph(est[i], xValues, yValues);
				graphs.add(graph);
			}
			break;
		case "Tempo de Simulação":
			for (int i = 0; i < results.length; i++) {
				double[] yValues = vals(results[i], 4);
				Graph graph = new Graph(est[i], xValues, yValues);
				graphs.add(graph);
			}
			break;
		}
	}

	public double[] vals(double[][] sims, int pos) {
		double[] yValues = new double[sims.length];
		for (int j = 0; j < sims.length; j++) {
			yValues[j] = sims[j][pos];
		}
		return yValues;
	}

	public void render() {
		XYChart graph = new XYChartBuilder().width(800).height(600).build();
		graph.setTitle(this.graphTitle);
		graph.setYAxisTitle(yAxis);
		graph.setXAxisTitle("Number of Tags");

		for (int i = 0; i < graphs.size(); i++) {
			graph.addSeries(graphs.get(i).getTitle(), graphs.get(i).getxValues(), graphs.get(i).getyValues());
		}

		new SwingWrapper(graph).displayChart();
	}

}
