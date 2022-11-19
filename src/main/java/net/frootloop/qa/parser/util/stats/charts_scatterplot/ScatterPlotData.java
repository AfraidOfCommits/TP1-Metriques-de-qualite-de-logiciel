package net.frootloop.qa.parser.util.stats.charts_scatterplot;

import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.util.stats.comparators.ParsedClassComparator.CompareClassesBy;

public class ScatterPlotData {

    public double[] commentDensities;
    public int[] numLinesCode;
    public int[] numCommits;

    private final int NUM_CLASSES;
    public final double COMMENT_DENSITY_AVERAGE, NUM_LINES_CODE_AVERAGE, NUM_COMMITS_AVERAGE;
    public final double COMMENT_DENSITY_STANDARD_DEVIATION, NUM_LINES_CODE_STANDARD_DEVIATION, NUM_COMMITS_STANDARD_DEVIATION;

    public ScatterPlotData(ParsedClass[] parsedClasses) {

        // Initialize arrays:
        NUM_CLASSES = parsedClasses.length;
        commentDensities = new double[NUM_CLASSES];
        numLinesCode = new int[NUM_CLASSES];
        numCommits = new int[NUM_CLASSES];

        // Get a list for each value, in order of each class. In other words, each index i represents a (X,Y,Z) class datapoint:
        for(int i = 0; i < NUM_CLASSES; i++) {
            ParsedClass c = parsedClasses[i];
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

    public void print() {

        System.out.println("\n[ STATISTICS OF CLASS SIZES & MATURITY ]");

        System.out.println( "Average commits per class (NCH): " + String.format("%.2f", NUM_COMMITS_AVERAGE) + "\n" +
                "Std Deviation of NCH: " + String.format("%.2f", NUM_COMMITS_STANDARD_DEVIATION) + "\n");

        System.out.println("Average lines of code (NLOC) : " + String.format("%.2f", NUM_LINES_CODE_AVERAGE)+ "\n" +
                "Std Deviation of NLOC: " + String.format("%.2f", NUM_LINES_CODE_STANDARD_DEVIATION) + "\n");


        System.out.println("Average CD: " + String.format("%.2f", COMMENT_DENSITY_AVERAGE) + "\n" +
                "Std Deviation of CD: " + String.format("%.2f", COMMENT_DENSITY_STANDARD_DEVIATION) + "\n");

        System.out.println("[ CORRELATIONS ]");
        System.out.println("Correlation between NLOC and CD: " + String.format("%.3f", this.getCorrelationBetween(CompareClassesBy.NUMBER_LINES_OF_CODES, CompareClassesBy.DENSITY_OF_COMMENTS)));
        System.out.println("Correlation between NLOC and NoComm: " + String.format("%.3f", this.getCorrelationBetween(CompareClassesBy.NUMBER_LINES_OF_CODES, CompareClassesBy.NUMBER_OF_COMMITS)));
        System.out.println("Correlation between NoComm and CD: " + String.format("%.3f", this.getCorrelationBetween(CompareClassesBy.NUMBER_OF_COMMITS, CompareClassesBy.DENSITY_OF_COMMENTS)));;

    }

    private double getValueOfClassAt(int index, CompareClassesBy sortBy) {
        if(sortBy.equals(CompareClassesBy.NUMBER_LINES_OF_CODES)) return numLinesCode[index];
        else if(sortBy.equals(CompareClassesBy.NUMBER_OF_COMMITS)) return numCommits[index];
        return commentDensities[index];
    }

    private double getAverageOf(CompareClassesBy sortBy) {
        if(sortBy.equals(CompareClassesBy.NUMBER_LINES_OF_CODES)) return NUM_LINES_CODE_AVERAGE;
        else if(sortBy.equals(CompareClassesBy.NUMBER_OF_COMMITS)) return NUM_COMMITS_AVERAGE;
        return COMMENT_DENSITY_AVERAGE;
    }

    private double getCorrelationBetween(CompareClassesBy a, CompareClassesBy b) {
        if(a.equals(b)) return 1.0d;

        double covarianceSum = 0.0d;
        for(int i = 0; i < NUM_CLASSES; i++)
            covarianceSum += (this.getValueOfClassAt(i, a) - this.getAverageOf(a)) * (this.getValueOfClassAt(i, b) - this.getAverageOf(b));

        double deviationsA = 0.0d;
        for(int i = 0; i < NUM_CLASSES; i++)
            deviationsA += Math.pow(this.getValueOfClassAt(i, a) - this.getAverageOf(a), 2);

        double deviationsB = 0.0d;
        for(int i = 0; i < NUM_CLASSES; i++)
            deviationsB += Math.pow(this.getValueOfClassAt(i, b) - this.getAverageOf(b), 2);

        return covarianceSum / (Math.sqrt(deviationsA) * Math.sqrt(deviationsB));
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
        double variance = 0;
        for(int i : integers) variance += Math.pow((double)i - average, 2);
        variance = variance/integers.length;
        return Math.sqrt(variance);
    }

    private static double getStandardDeviationOfDoublesList(double[] doubles, double average) {
        double variance = 0.0d;
        for(double d : doubles) variance += Math.pow(d - average, 2);
        variance = variance/doubles.length;
        return Math.sqrt(variance);
    }
}
