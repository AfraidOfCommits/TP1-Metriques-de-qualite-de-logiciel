package net.frootloop.qa.parser.util.stats.charts_scatterplot;

import net.frootloop.qa.parser.JavaRepositoryParser;
import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.result.ParsedRepository;
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
import java.nio.file.Path;

public class DrawnScatterPlot extends JFrame {

    public DrawnScatterPlot(ParsedClass[] parsedClasses, CompareClassesBy a, CompareClassesBy b) {
        super("DrawnScatterPlot");

        // Create dataset
        XYDataset dataset = createDataset(parsedClasses, a, b);

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

    public static void main(String[] args) {
        ParsedRepository repo = JavaRepositoryParser.analyseRepositoryAt(Path.of("C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\jfreechart"));
        //ParsedRepository repo = JavaRepositoryParser.analyseRepositoryAt(Path.of("C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel"));
        new DrawnScatterPlot(repo.getClasses(), CompareClassesBy.DENSITY_OF_COMMENTS, CompareClassesBy.NUMBER_LINES_OF_CODES);
        new DrawnScatterPlot(repo.getClasses(), CompareClassesBy.NUMBER_OF_COMMITS, CompareClassesBy.NUMBER_LINES_OF_CODES);
        new DrawnScatterPlot(repo.getClasses(), CompareClassesBy.DENSITY_OF_COMMENTS, CompareClassesBy.NUMBER_OF_COMMITS);
    }
}
