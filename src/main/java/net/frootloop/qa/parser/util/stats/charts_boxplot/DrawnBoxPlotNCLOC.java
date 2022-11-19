package net.frootloop.qa.parser.util.stats.charts_boxplot;

import net.frootloop.qa.parser.result.ParsedClass;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.util.ArrayList;

public class DrawnBoxPlotNCLOC extends AbstractDrawnBoxPlot {

    public DrawnBoxPlotNCLOC(ParsedClass[] parsedClasses) {
        super(getValuesOf(parsedClasses), "Number of Lines Per Classes", "NCLOC");
    }

    public DrawnBoxPlotNCLOC(double[] values) {
        super(values, "Number of Lines Per Classes", "NCLOC");
    }

    private static double[] getValuesOf(ParsedClass[] parsedClasses) {
        double[] values = new double[parsedClasses.length];
        for(int i = 0; i < parsedClasses.length; i++)
            values[i] = parsedClasses[i].getNumLinesCode();
        return values;
    }

    protected DefaultBoxAndWhiskerCategoryDataset createSampleDataset(ParsedClass[] parsedClasses) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        ArrayList<Double> graphData = new ArrayList<>();
        for(ParsedClass c : parsedClasses) graphData.add((double) c.getNumLinesCode());
        dataset.add(graphData, "1", "1");

        return dataset;
    }

    @Override
    protected DefaultBoxAndWhiskerCategoryDataset createSampleDataset(double[] numLinesCode) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        ArrayList<Double> graphData = new ArrayList<>();
        for(int i = 0; i < numLinesCode.length; i++) graphData.add((double)numLinesCode[i]);
        dataset.add(graphData, "", "");

        return dataset;
    }
}
