package net.frootloop.qa.parser.util.stats;

import net.frootloop.qa.parser.result.ParsedClass;

import java.util.ArrayList;
import java.util.List;

public class ScatterPlotData {

    private List<Double> commentDensities = new ArrayList<>();
    private List<Integer> numLinesCode = new ArrayList<>();
    private List<Integer> numCommits = new ArrayList<>();

    public final double COMMENT_DENSITY_AVERAGE, NUM_LINES_CODE_AVERAGE, NUM_COMMITS_AVERAGE;
    public final double COMMENT_DENSITY_VARIANCE, NUM_LINES_CODE_VARIANCE, NUM_COMMITS_VARIANCE;
    public final double COMMENT_DENSITY_STANDARD_DEVIATION, NUM_LINES_CODE_STANDARD_DEVIATION, NUM_COMMITS_STANDARD_DEVIATION;

    public ScatterPlotData(List<ParsedClass> parsedClasses) {

        // Get a list for each value, in order of each class. In other words, each index i represents a (X,Y,Z) class datapoint:
        for(ParsedClass c: parsedClasses) {
            commentDensities.add((double)c.getCommentDensity());
            numLinesCode.add(c.getNumLinesCode());
            numCommits.add(c.getNumCommits());
        }

        // Get the means of each list of values:
        COMMENT_DENSITY_AVERAGE = getAverageOfDoublesList(commentDensities);
        NUM_LINES_CODE_AVERAGE = getAverageOfIntegerList(numLinesCode);
        NUM_COMMITS_AVERAGE = getAverageOfIntegerList(numCommits);

        // Get the variance of list of values:
        COMMENT_DENSITY_VARIANCE = getVarianceOfDoublesList(commentDensities, COMMENT_DENSITY_AVERAGE);
        NUM_LINES_CODE_VARIANCE = getVarianceOfIntegerList(numLinesCode, NUM_LINES_CODE_AVERAGE);
        NUM_COMMITS_VARIANCE = getVarianceOfIntegerList(numCommits, NUM_COMMITS_AVERAGE);

        // Get the standard deviations of the list of values:
        COMMENT_DENSITY_STANDARD_DEVIATION = getStandardDeviationOfDoublesList(commentDensities, COMMENT_DENSITY_AVERAGE);
        NUM_LINES_CODE_STANDARD_DEVIATION = getStandardDeviationOfIntegerList(numLinesCode, NUM_LINES_CODE_AVERAGE);
        NUM_COMMITS_STANDARD_DEVIATION = getStandardDeviationOfIntegerList(numCommits, NUM_COMMITS_AVERAGE);
    }

    private static double getAverageOfIntegerList(List<Integer> integers) {
        double average = 0.0d;
        for(int i : integers) average += i;
        return average / integers.size();
    }

    private static double getAverageOfDoublesList(List<Double> doubles) {
        double average = 0.0d;
        for(double d : doubles) average += d;
        return average / doubles.size();
    }

    private static double getVarianceOfIntegerList(List<Integer> integers, double average) {
        double variance = 0.0d;
        for(int i : integers) variance += (double)i - average;
        return variance;
    }

    private static double getVarianceOfDoublesList(List<Double> doubles, double average) {
        double variance = 0.0d;
        for(double d : doubles) variance += d - average;
        return variance;
    }

    private static double getStandardDeviationOfIntegerList(List<Integer> integers, double average) {
        double stdDeviation = 0.0d;
        for(int i : integers) stdDeviation += Math.pow((double)i - average, 2);
        return stdDeviation;
    }

    private static double getStandardDeviationOfDoublesList(List<Double> doubles, double average) {
        double stdDeviation = 0.0d;
        for(double d : doubles) stdDeviation += Math.pow(d - average, 2);
        return stdDeviation;
    }
}
