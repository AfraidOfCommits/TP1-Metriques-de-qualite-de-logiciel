package net.frootloop.qa.parser.util.stats.charts_boxplot;

import net.frootloop.qa.parser.result.ParsedClass;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.util.ArrayList;

public class DrawnBoxPlotCD extends AbstractDrawnBoxPlot {

    public DrawnBoxPlotCD(ParsedClass[] parsedClasses) {
        super(getValuesOf(parsedClasses), "Comment Densities of Classes", "CD");
    }

    public DrawnBoxPlotCD(double[] values) {
        super(values, "Comment Densities of Classes", "CD");
    }

    private static double[] getValuesOf(ParsedClass[] parsedClasses) {
        double[] values = new double[parsedClasses.length];
        for(int i = 0; i < parsedClasses.length; i++)
            values[i] = parsedClasses[i].getCommentDensity();
        return values;
    }

    protected DefaultBoxAndWhiskerCategoryDataset createSampleDataset(ParsedClass[] parsedClasses) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        ArrayList<Double> graphData = new ArrayList<>();
        for(ParsedClass c : parsedClasses) graphData.add((double) c.getCommentDensity());
        dataset.add(graphData, "", "");

        return dataset;
    }

    @Override
    protected DefaultBoxAndWhiskerCategoryDataset createSampleDataset(double[] densities) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        ArrayList<Double> graphData = new ArrayList<>();
        for(int i = 0; i < densities.length; i++) graphData.add(densities[i]);
        dataset.add(graphData, "", "");

        return dataset;
    }
}
