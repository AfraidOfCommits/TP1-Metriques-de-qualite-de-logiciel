package net.frootloop.qa.parser.util.stats;

import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.util.stats.comparators.ComparatorCommentDensity;
import net.frootloop.qa.parser.util.stats.comparators.ComparatorCommitsPerClass;
import net.frootloop.qa.parser.util.stats.comparators.ComparatorLinesOfCode;
import net.frootloop.qa.parser.util.stats.comparators.ParsedClassComparator;
import net.frootloop.qa.parser.util.stats.comparators.ParsedClassComparator.ClassComparatorEnum;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoxPlotData {
    public final double UPPER_LIMIT_VALUE;
    public final double LOWER_LIMIT_VALUE;
    public final double UPPER_QUARTILE_VALUE;
    public final double LOWER_QUARTILE_VALUE;
    public final double MEDIAN_VALUE;
    public final double LENGTH;
    public final ArrayList<Double> EXTREME_DATA_POINTS;


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
        double upperQuartile = BoxPlotData.GetUpperQuartileOf(sortedListOfClasses, sortedBy);
        double lowerQuartile = BoxPlotData.GetLowerQuartileOf(sortedListOfClasses, sortedBy);
        double length = upperQuartile - lowerQuartile;
        double upperLimit = upperQuartile + 1.5d * length;
        double lowerLimit = lowerQuartile - 1.5d * length;

        // Check the values. Are they well distributed?
        BoxPlotData.EnsureValidBoxPlotOrder(upperLimit,upperQuartile,median,lowerQuartile,lowerLimit);

        // Set our attributes:
        UPPER_LIMIT_VALUE = upperLimit;
        UPPER_QUARTILE_VALUE = upperQuartile;
        MEDIAN_VALUE = median;
        LOWER_QUARTILE_VALUE = lowerQuartile;
        LOWER_LIMIT_VALUE = lowerLimit;
        LENGTH = length;

        // Get a list of extreme data points, i.e. classes with values exceeding either limit:
        ArrayList<Double> extremePoints = new ArrayList<>();
        for(ParsedClass c : sortedListOfClasses) {
            double value = median;
            if(sortedBy == ClassComparatorEnum.NUMBER_OF_COMMITS) value = c.getNumCommits();
            if(sortedBy == ClassComparatorEnum.NUMBER_LINES_OF_CODES) value = c.getNumLinesCode();
            if(sortedBy == ClassComparatorEnum.DENSITY_OF_COMMENTS) value = c.getCommentDensity();
            if(value < lowerLimit || value > upperLimit) extremePoints.add(value);
        }
        EXTREME_DATA_POINTS = extremePoints;
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
            ParsedClass medianClass = sortedListOfClasses.get((sortedListOfClasses.size() - 1) / 2);
            if (sortedBy == ClassComparatorEnum.NUMBER_OF_COMMITS) return medianClass.getNumCommits();
            if (sortedBy == ClassComparatorEnum.NUMBER_LINES_OF_CODES) return medianClass.getNumLinesCode();
            if (sortedBy == ClassComparatorEnum.DENSITY_OF_COMMENTS) return medianClass.getCommentDensity();
        }

        // If the number of elements is even, return the average between the two middle elements' values:
        else {
            ParsedClass medianClassBtm = sortedListOfClasses.get(sortedListOfClasses.size() >> 1);
            ParsedClass medianClassTop = sortedListOfClasses.get((sortedListOfClasses.size() >> 1) + 1);
            if (sortedBy == ClassComparatorEnum.NUMBER_OF_COMMITS)
                return (double) (medianClassBtm.getNumCommits() + medianClassTop.getNumCommits()) / 2.0d;
            if (sortedBy == ClassComparatorEnum.NUMBER_LINES_OF_CODES)
                return (double) (medianClassBtm.getNumLinesCode() + medianClassTop.getNumLinesCode()) / 2.0d;
            if (sortedBy == ClassComparatorEnum.DENSITY_OF_COMMENTS)
                return (double) (medianClassBtm.getCommentDensity() + medianClassTop.getCommentDensity()) / 2.0d;
        }
        return 0.0d;
    }

    /***
     * Returns the median value sorted Parsed Classes with values above the dataset's median.
     * @param sortedListOfClasses
     * @param sortedBy
     * @return (double) Upper quartile of the dataset.
     */
    private static double GetUpperQuartileOf(List<ParsedClass> sortedListOfClasses, ClassComparatorEnum sortedBy) {

        // If there's only one class in the list, return its value:
        if (sortedListOfClasses.size() == 1) {
            ParsedClass upperQuartileClass = sortedListOfClasses.get(0);
            if (sortedBy == ClassComparatorEnum.NUMBER_OF_COMMITS) return upperQuartileClass.getNumCommits();
            if (sortedBy == ClassComparatorEnum.NUMBER_LINES_OF_CODES) return upperQuartileClass.getNumLinesCode();
            if (sortedBy == ClassComparatorEnum.DENSITY_OF_COMMENTS) return upperQuartileClass.getCommentDensity();
        }

        int indexOfMedian;
        if (sortedListOfClasses.size() % 2 == 1) indexOfMedian = ((sortedListOfClasses.size() - 1) / 2);
        else indexOfMedian = sortedListOfClasses.size() >> 1;

        List<ParsedClass> upperHalf = sortedListOfClasses.subList(indexOfMedian, sortedListOfClasses.size());
        return GetMedianOf(upperHalf, sortedBy);
    }

    /***
     * Returns the median value sorted Parsed Classes with values below the dataset's median.
     * @param sortedListOfClasses
     * @param sortedBy
     * @return (double) Lower quartile of the dataset.
     */
    private static double GetLowerQuartileOf(List<ParsedClass> sortedListOfClasses, ClassComparatorEnum sortedBy) {

        // If there's only one class in the list, return its value:
        if (sortedListOfClasses.size() == 1) {
            ParsedClass lowerQuartileClass = sortedListOfClasses.get(0);
            if (sortedBy == ClassComparatorEnum.NUMBER_OF_COMMITS) return lowerQuartileClass.getNumCommits();
            if (sortedBy == ClassComparatorEnum.NUMBER_LINES_OF_CODES) return lowerQuartileClass.getNumLinesCode();
            if (sortedBy == ClassComparatorEnum.DENSITY_OF_COMMENTS) return lowerQuartileClass.getCommentDensity();
        }

        int indexOfMedian;
        if (sortedListOfClasses.size() % 2 == 1) indexOfMedian = ((sortedListOfClasses.size() - 1) / 2);
        else indexOfMedian = (sortedListOfClasses.size() >> 1) + 1;

        List<ParsedClass> lowerHalf = sortedListOfClasses.subList(0, indexOfMedian);
        return GetMedianOf(lowerHalf, sortedBy);
    }
}
