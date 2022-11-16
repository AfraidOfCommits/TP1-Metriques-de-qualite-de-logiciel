package net.frootloop.qa.parser.util.stats;

import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.util.stats.comparators.ComparatorCommentDensity;
import net.frootloop.qa.parser.util.stats.comparators.ComparatorCommitsPerClass;
import net.frootloop.qa.parser.util.stats.comparators.ComparatorLinesOfCode;
import net.frootloop.qa.parser.util.stats.comparators.ParsedClassComparator;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoxPlotData {
    public final double UPPER_WHISKER_VALUE;
    public final double LOWER_WHISKER_VALUE;
    public final double UPPER_QUARTILE_VALUE;
    public final double LOWER_QUARTILE_VALUE;
    public final double MEDIAN_VALUE;
    public final ArrayList<Double> EXTREME_DATA_POINTS;

    public enum ClassComparatorEnum {
        NUMBER_OF_COMMITS,
        NUMBER_LINES_OF_CODES,
        DENSITY_OF_COMMENTS
    }


    /***
     *
     * @param listOfClasses
     * @param sortedBy
     */
    public BoxPlotData(List<ParsedClass> listOfClasses, ClassComparatorEnum sortedBy) {

        // Sort the list:
        ParsedClassComparator comparator = null;
        if(sortedBy == ClassComparatorEnum.NUMBER_OF_COMMITS) comparator = new ComparatorCommitsPerClass();
        if(sortedBy == ClassComparatorEnum.NUMBER_LINES_OF_CODES) comparator = new ComparatorLinesOfCode();
        if(sortedBy == ClassComparatorEnum.DENSITY_OF_COMMENTS) comparator = new ComparatorCommentDensity();
        List<ParsedClass> sortedListOfClasses = listOfClasses.stream().sorted(comparator).collect(Collectors.toList());

        // Find the values:
        double median = BoxPlotData.GetMedianOf(sortedListOfClasses, sortedBy);
        double upperWhisker = 0.0d; // TODO
        double upperQuartile = 0.0d; // TODO
        double lowerWhisker = 0.0d; // TODO
        double lowerQuartile = 0.0d; // TODO

        // Check the values. Are they well distributed?
        BoxPlotData.EnsureValidBoxPlotOrder(upperWhisker,upperQuartile,median,lowerQuartile,lowerWhisker);

        // Set our attributes
        UPPER_WHISKER_VALUE = upperWhisker;
        UPPER_QUARTILE_VALUE = upperQuartile;
        MEDIAN_VALUE = median;
        LOWER_QUARTILE_VALUE = lowerQuartile;
        LOWER_WHISKER_VALUE = lowerWhisker;
        EXTREME_DATA_POINTS = new ArrayList<>();
    }


    /**
     *
     * @param upperWhisker
     * @param upperQuartile
     * @param median
     * @param lowerQuartile
     * @param lowerWhisker
     */
    private static void EnsureValidBoxPlotOrder(double upperWhisker, double upperQuartile, double median, double lowerQuartile, double lowerWhisker) {
        boolean isOrderValid = (upperWhisker >= upperQuartile) && (upperQuartile >= median) && (median >= lowerQuartile) && (lowerQuartile >= lowerWhisker);
        if(!isOrderValid) {
            System.out.println("\n[ FATAL CONSTRUCTOR ERROR ]\nInput data to 'BoxPlotData' class constructor is invalid.\nInput values should respect order: 'upperWhisker' >= 'upperQuartile' >= 'median' >= 'lowerQuartile' >= 'lowerWhisker'.");
            if(!(upperWhisker >= upperQuartile)) System.out.println("Box plots require the upper whisker to be higher or equal to the upper quartile, yet inputted values were " + upperWhisker + " and " + upperQuartile + " respectively.");
            if(!(upperQuartile >= median)) System.out.println("Box plots require the upper quartile to be higher or equal to the median, yet inputted values were " + upperQuartile + " and " + median + " respectively.");
            if(!(upperWhisker >= upperQuartile)) System.out.println("Box plots require the median to be higher or equal to the lower quartile, yet inputted values were " + median + " and " + lowerQuartile + " respectively.");
            if(!(upperWhisker >= upperQuartile)) System.out.println("Box plots require the lower quartile to be higher or equal to the lower whisker, yet inputted values were " + upperWhisker + " and " + upperQuartile + " respectively.");
            System.out.println("\nPlease ensure that the input to the constructor was properly sorted.\n");
            throw new InvalidParameterException();
        }
    }



    /***
     * Returns the median value of a list of sorted Parsed Classes, based off of the Comparator they were sorted by.
     * @param sortedListOfClasses
     * @param sortedBy
     * @return (double) Median of the dataset.
     */
    private static double GetMedianOf(List<ParsedClass> sortedListOfClasses, ClassComparatorEnum sortedBy) {

        // If there's only one class in the list, return its value:
        if (sortedListOfClasses.size() == 1) {
            ParsedClass medianClass = sortedListOfClasses.get(0);
            if (sortedBy == ClassComparatorEnum.NUMBER_OF_COMMITS) return medianClass.getNumCommits();
            if (sortedBy == ClassComparatorEnum.NUMBER_LINES_OF_CODES) return medianClass.getNumLinesCode();
            if (sortedBy == ClassComparatorEnum.DENSITY_OF_COMMENTS) return medianClass.getCommentDensity();
        }

        // If the number of elements is odd, return the middle element's value:
        else if (sortedListOfClasses.size() % 2 == 1) {
            ParsedClass medianClass = sortedListOfClasses.get(sortedListOfClasses.size() / 2);
            if (sortedBy == ClassComparatorEnum.NUMBER_OF_COMMITS) return medianClass.getNumCommits();
            if (sortedBy == ClassComparatorEnum.NUMBER_LINES_OF_CODES) return medianClass.getNumLinesCode();
            if (sortedBy == ClassComparatorEnum.DENSITY_OF_COMMENTS) return medianClass.getCommentDensity();
        }

        // If the number of elements is even, return the average between the two middle elements' values:
        else {
            ParsedClass medianClassBtm = sortedListOfClasses.get(sortedListOfClasses.size() >> 1);
            ParsedClass medianClassTop = sortedListOfClasses.get(sortedListOfClasses.size() >> 1);
            if (sortedBy == ClassComparatorEnum.NUMBER_OF_COMMITS)
                return (double) (medianClassBtm.getNumCommits() + medianClassTop.getNumCommits()) / 2.0d;
            if (sortedBy == ClassComparatorEnum.NUMBER_LINES_OF_CODES)
                return (double) (medianClassBtm.getNumLinesCode() + medianClassTop.getNumLinesCode()) / 2.0d;
            if (sortedBy == ClassComparatorEnum.DENSITY_OF_COMMENTS)
                return (double) (medianClassBtm.getCommentDensity() + medianClassTop.getCommentDensity()) / 2.0d;
        }
        return 0.0d;
    }
}
