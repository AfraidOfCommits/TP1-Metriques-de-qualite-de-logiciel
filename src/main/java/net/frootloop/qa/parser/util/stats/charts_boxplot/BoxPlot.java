package net.frootloop.qa.parser.util.stats.charts_boxplot;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.awt.*;
import java.util.ArrayList;

public class BoxPlot extends ApplicationFrame {

    final static String CHART_TITLE = "Basic Box-and-Whisker Chart";
    final static String Y_AXIS_LABEL = "Value";
    final static String X_AXIS_LABEL = "Parsed Classes";

    public BoxPlot() {
        super(CHART_TITLE);

        final BoxAndWhiskerCategoryDataset dataset = this.createSampleDataset();

        // Set the ranges of data;
        final CategoryAxis xAxis = new CategoryAxis(X_AXIS_LABEL);
        final NumberAxis yAxis = new NumberAxis(Y_AXIS_LABEL);
        yAxis.setAutoRangeIncludesZero(false);

        // Set JFreeChart's renderer for the BoxAndWhisker chart:
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart(CHART_TITLE, new Font("Helvetica", Font.BOLD, 16), plot, true);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(450, 270));
        setContentPane(chartPanel);
        setContentPane(chartPanel);

        // Show the Box Plot:
        this.pack();
        this.setVisible(true);
    }


    private BoxAndWhiskerCategoryDataset createSampleDataset() {

        final int seriesCount = 3;
        final int categoryCount = 4;
        final int entityCount = 22;

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        for (int i = 0; i < seriesCount; i++) {
            for (int j = 0; j < categoryCount; j++) {
                final ArrayList<Double> list = new ArrayList();
                // add some values...
                for (int k = 0; k < entityCount; k++) {
                    final double value1 = 10.0 + Math.random() * 3;
                    list.add(value1);
                    final double value2 = 11.25 + Math.random();
                    list.add(value2);
                }
                dataset.add(list, "Series " + i, " Type " + j);
            }
        }

        return dataset;
    }

    public static void main(final String[] args) {

        //Log.getInstance().addTarget(new PrintStreamLogTarget(System.out));
        final BoxPlot demo = new BoxPlot();

    }

}
