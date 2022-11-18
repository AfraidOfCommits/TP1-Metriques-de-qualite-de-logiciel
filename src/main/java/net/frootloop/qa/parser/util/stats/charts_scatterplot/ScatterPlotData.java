package net.frootloop.qa.parser.util.stats.charts_scatterplot;

import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.util.stats.comparators.ParsedClassComparator.CompareClassesBy;

import java.util.List;

public class ScatterPlotData {

    public double[] commentDensities;
    public int[] numLinesCode;
    public int[] numCommits;

    public final double COMMENT_DENSITY_AVERAGE, NUM_LINES_CODE_AVERAGE, NUM_COMMITS_AVERAGE;
    public final double COMMENT_DENSITY_STANDARD_DEVIATION, NUM_LINES_CODE_STANDARD_DEVIATION, NUM_COMMITS_STANDARD_DEVIATION;

    public ScatterPlotData(List<ParsedClass> parsedClasses) {

        // Initialize arrays:
        commentDensities = new double[parsedClasses.size()];
        numLinesCode = new int[parsedClasses.size()];
        numCommits = new int[parsedClasses.size()];

        // Get a list for each value, in order of each class. In other words, each index i represents a (X,Y,Z) class datapoint:
        for(int i = 0; i < parsedClasses.size(); i++) {
            ParsedClass c = parsedClasses.get(i);
            commentDensities[i] = c.getCommentDensity();
            numLinesCode[i] = c.getNumLinesCode();
            numCommits[i] = c.getNumCommits();
        }

        // Get the means of each list of values:
        COMMENT_DENSITY_AVERAGE = getAverageOfDoublesList(commentDensities);
        NUM_LINES_CODE_AVERAGE = getAverageOfIntegerList(numLinesCode);
        NUM_COMMITS_AVERAGE = getAverageOfIntegerList(numCommits);

        // Get the standard deviations of the list of values:
        COMMENT_DENSITY_STANDARD_DEVIATION = getStandardDeviationOfDoublesList(commentDensities, COMMENT_DENSITY_AVERAGE);
        NUM_LINES_CODE_STANDARD_DEVIATION = getStandardDeviationOfIntegerList(numLinesCode, NUM_LINES_CODE_AVERAGE);
        NUM_COMMITS_STANDARD_DEVIATION = getStandardDeviationOfIntegerList(numCommits, NUM_COMMITS_AVERAGE);
    }

    public double getPearsonCorrelationBetween(CompareClassesBy a, CompareClassesBy b) {
        if(a.equals(b)) return 1.0d;
        return this.getCovariance(a,b) / this.getStdDeviationProduct(a,b);
    }

    private double getStdDeviationProduct(CompareClassesBy a, CompareClassesBy b) {

        double stdDeviationA = a.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? COMMENT_DENSITY_STANDARD_DEVIATION :
                a.equals(CompareClassesBy.NUMBER_LINES_OF_CODES) ? NUM_LINES_CODE_STANDARD_DEVIATION : NUM_COMMITS_STANDARD_DEVIATION;
        double stdDeviationB = b.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? COMMENT_DENSITY_STANDARD_DEVIATION :
                b.equals(CompareClassesBy.NUMBER_LINES_OF_CODES) ? NUM_LINES_CODE_STANDARD_DEVIATION : NUM_COMMITS_STANDARD_DEVIATION;

        return stdDeviationA * stdDeviationB;
    }

    private double getCovariance(CompareClassesBy a, CompareClassesBy b) {

        double averageA = a.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? COMMENT_DENSITY_AVERAGE :
                a.equals(CompareClassesBy.NUMBER_LINES_OF_CODES) ? NUM_LINES_CODE_AVERAGE : NUM_COMMITS_AVERAGE;
        double averageB = b.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? COMMENT_DENSITY_AVERAGE :
                b.equals(CompareClassesBy.NUMBER_LINES_OF_CODES) ? NUM_LINES_CODE_AVERAGE : NUM_COMMITS_AVERAGE;

        double sum = 0.0d;
        for(int i = 0; i < numLinesCode.length; i++) {
            double valA = a.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? commentDensities[i] :
                    a.equals(CompareClassesBy.NUMBER_LINES_OF_CODES) ? numLinesCode[i] : numCommits[i];
            double valB = b.equals(CompareClassesBy.DENSITY_OF_COMMENTS) ? commentDensities[i] :
                    b.equals(CompareClassesBy.NUMBER_LINES_OF_CODES) ? numLinesCode[i] : numCommits[i];
            sum += (valA - averageA) * (valB - averageB);
        }
        return sum;
    }


    private static double getAverageOfIntegerList(int[] integers) {
        double average = 0.0d;
        for(int i : integers) average += i;
        return average / integers.length;
    }

    private static double getAverageOfDoublesList(double[] doubles) {
        double average = 0.0d;
        for(double d : doubles) average += d;
        return average / doubles.length;
    }

    private static double getStandardDeviationOfIntegerList(int[] integers, double average) {
        double stdDeviation = 0.0d;
        for(int i : integers) stdDeviation += Math.pow((double)i - average, 2);
        return stdDeviation;
    }

    private static double getStandardDeviationOfDoublesList(double[] doubles, double average) {
        double stdDeviation = 0.0d;
        for(double d : doubles) stdDeviation += Math.pow(d - average, 2);
        return stdDeviation;
    }
}
