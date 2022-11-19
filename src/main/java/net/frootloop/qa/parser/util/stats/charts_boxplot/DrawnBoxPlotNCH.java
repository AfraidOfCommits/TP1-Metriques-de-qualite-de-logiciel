package net.frootloop.qa.parser.util.stats.charts_boxplot;

import net.frootloop.qa.parser.result.ParsedClass;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.util.ArrayList;

public class DrawnBoxPlotNCH extends AbstractDrawnBoxPlot {

    public DrawnBoxPlotNCH(ParsedClass[] parsedClasses) {
        super(getValuesOf(parsedClasses), "Number of Commits Per Class", "NCH");
    }

    public DrawnBoxPlotNCH(double[] values) {
        super(values, "Number of Commits Per Class", "NCH");
    }

    private static double[] getValuesOf(ParsedClass[] parsedClasses) {
        double[] values = new double[parsedClasses.length];
        for(int i = 0; i < parsedClasses.length; i++)
            values[i] = parsedClasses[i].getNumCommits();
        return values;
    }


    protected DefaultBoxAndWhiskerCategoryDataset createSampleDataset(ParsedClass[] parsedClasses) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        ArrayList<Double> graphData = new ArrayList<>();
        for(ParsedClass c : parsedClasses) graphData.add((double) c.getNumCommits());
        dataset.add(graphData, "1", "1");

        return dataset;
    }

    @Override
    protected DefaultBoxAndWhiskerCategoryDataset createSampleDataset(double[] numCommits) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        ArrayList<Double> graphData = new ArrayList<>();
        for(int i = 0; i < numCommits.length; i++) graphData.add((double)numCommits[i]);
        dataset.add(graphData, "", "");

        return dataset;
    }
}
