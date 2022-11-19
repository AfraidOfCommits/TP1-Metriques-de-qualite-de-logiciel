package net.frootloop.qa.parser.util.stats.charts_boxplot;

import net.frootloop.qa.parser.result.ParsedClass;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.util.ArrayList;

public class DrawnBoxPlotCD extends AbstractDrawnBoxPlot {

    public DrawnBoxPlotCD(ParsedClass[] parsedClasses) {
        super(parsedClasses, "Comment Densities of Classes", "CD");
    }

    @Override
    protected DefaultBoxAndWhiskerCategoryDataset createSampleDataset(ParsedClass[] parsedClasses) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        ArrayList<Double> graphData = new ArrayList<>();
        for(ParsedClass c : parsedClasses) graphData.add((double) c.getCommentDensity());
        dataset.add(graphData, "1", "1");

        return dataset;
    }
}
