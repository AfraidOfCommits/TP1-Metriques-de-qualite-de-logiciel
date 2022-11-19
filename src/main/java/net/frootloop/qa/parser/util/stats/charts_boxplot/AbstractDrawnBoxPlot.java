package net.frootloop.qa.parser.util.stats.charts_boxplot;

import net.frootloop.qa.parser.result.ParsedClass;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.Range;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public abstract class AbstractDrawnBoxPlot extends ApplicationFrame {

    final String X_AXIS_LABEL = "Parsed Classes";

    public AbstractDrawnBoxPlot(ParsedClass[] parsedClasses, String title, String yAxisName) {
        super(title);

        final BoxAndWhiskerCategoryDataset dataset = this.createSampleDataset(parsedClasses);

        // Set the ranges of data;
        final CategoryAxis xAxis = new CategoryAxis(X_AXIS_LABEL);
        final NumberAxis yAxis = new NumberAxis(yAxisName);
        yAxis.setAutoRangeIncludesZero(false);

        // Set JFreeChart's renderer for the BoxAndWhisker chart:
        final CustomBoxAndWhiskerRenderer renderer = new CustomBoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        renderer.setMaximumBarWidth(0.2);
        renderer.setDataBoundsIncludesVisibleSeriesOnly(false);

        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart(title, new Font("SansSerif", Font.BOLD, 14), plot, true);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(300, 600));

        setContentPane(chartPanel);
        setContentPane(chartPanel);

        // Show the Box Plot:
        this.pack();
        this.setVisible(true);
    }

    protected abstract DefaultBoxAndWhiskerCategoryDataset createSampleDataset(ParsedClass[] parsedClasses);

    private class CustomBoxAndWhiskerRenderer extends BoxAndWhiskerRenderer {
        public CustomBoxAndWhiskerRenderer() {
            super();
        }

        private void drawEllipse(Point2D point, double oRadius, Graphics2D g2) {
            Ellipse2D dot = new Ellipse2D.Double(point.getX() + oRadius / 4.0, point.getY(), oRadius / 2.0, oRadius / 2.0);
            g2.draw(dot);
        }

        public Range findRangeBounds(DefaultBoxAndWhiskerCategoryDataset dataset) {
            //return dataset.getRangeBounds(true);
            return new Range(0, 400);
        }
    }
}
