package net.frootloop.qa.parser.util.stats.charts_boxplot;

import net.frootloop.qa.parser.result.ParsedClass;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.util.ArrayList;

public class DrawnBoxPlotNLOC extends AbstractDrawnBoxPlot {

    public DrawnBoxPlotNLOC(ParsedClass[] parsedClasses) {
        super(parsedClasses, "Number of Lines Per Classes", "NLOC");
    }

    @Override
    protected DefaultBoxAndWhiskerCategoryDataset createSampleDataset(ParsedClass[] parsedClasses) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        ArrayList<Double> graphData = new ArrayList<>();
        for(ParsedClass c : parsedClasses) graphData.add((double) c.getNumLinesCode());
        dataset.add(graphData, "1", "1");

        return dataset;
    }
}
