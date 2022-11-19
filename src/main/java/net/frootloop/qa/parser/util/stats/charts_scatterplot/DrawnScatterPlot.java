package net.frootloop.qa.parser.util.stats.charts_scatterplot;

import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.util.stats.comparators.ParsedClassComparator.CompareClassesBy;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class DrawnScatterPlot extends JFrame {

    private static double[] getDensitiesFrom(ParsedClass[] parsedClasses) {
        double[] values = new double[parsedClasses.length];
        for(int i = 0; i < parsedClasses.length; i++)
            values[i] = (double)parsedClasses[i].getCommentDensity();
        return values;
    }

    private static double[] getNumLinesFrom(ParsedClass[] parsedClasses) {
        double[] values = new double[parsedClasses.length];
        for(int i = 0; i < parsedClasses.length; i++)
            values[i] = (double)parsedClasses[i].getNumLinesCode();
        return values;
    }

    private static double[] getNumCommitsFrom(ParsedClass[] parsedClasses) {
        double[] values = new double[parsedClasses.length];
        for(int i = 0; i < parsedClasses.length; i++)
            values[i] = (double)parsedClasses[i].getNumCommits();
        return values;
    }

    public DrawnScatterPlot(ParsedClass[] parsedClasses, CompareClassesBy a, CompareClassesBy b) {
        this(getDensitiesFrom(parsedClasses), getNumLinesFrom(parsedClasses), getNumCommitsFrom(parsedClasses), a, b);
    }

    public DrawnScatterPlot(double[] densities, double[] numLinesCode, double[] numCommits, CompareClassesBy a, CompareClassesBy b) {
        super("DrawnScatterPlot");

        // Create dataset
        XYDataset dataset = createDataset(densities, numLinesCode, numCommits, a, b);

        // Create chart
        String xAxisTitle = a.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? "Comment Density" : a.equals(CompareClassesBy.NUMBER_OF_COMMITS) ? "Number of Commits" : "Number of Lines of Code";
        String yAxisTitle = b.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? "Comment Density" : b.equals(CompareClassesBy.NUMBER_OF_COMMITS) ? "Number of Commits" : "Number of Lines of Code";
        String title = xAxisTitle + " per class in relation to " + yAxisTitle ;
        JFreeChart chart = ChartFactory.createScatterPlot(title, xAxisTitle, yAxisTitle, dataset);

        //Changes background color
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(new Color(255,228,196));

        // Create Panel
        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);

        this.setSize(800, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private XYDataset createDataset(double[] densities, double[] numLinesCode, double[] numCommits, CompareClassesBy a, CompareClassesBy b) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Classes");

        for(int i = 0; i < densities.length; i++) {
            double xValue = a.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? densities[i] : a.equals(CompareClassesBy.NUMBER_OF_COMMITS) ? numCommits[i] : numLinesCode[i];
            double yValue = b.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? densities[i] : b.equals(CompareClassesBy.NUMBER_OF_COMMITS) ? numCommits[i] : numLinesCode[i];
            series.add(xValue, yValue);
        }

        dataset.addSeries(series);
        return dataset;
    }

    private XYDataset createDataset(ParsedClass[] parsedClasses, CompareClassesBy a, CompareClassesBy b) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Classes");

        for(ParsedClass c : parsedClasses) {
            double xValue = a.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? c.getCommentDensity() : a.equals(CompareClassesBy.NUMBER_OF_COMMITS) ? c.getNumCommits() : c.getNumLinesCode();
            double yValue = b.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? c.getCommentDensity() : b.equals(CompareClassesBy.NUMBER_OF_COMMITS) ? c.getNumCommits() : c.getNumLinesCode();
            series.add(xValue, yValue);
        }

        dataset.addSeries(series);
        return dataset;
    }
}
