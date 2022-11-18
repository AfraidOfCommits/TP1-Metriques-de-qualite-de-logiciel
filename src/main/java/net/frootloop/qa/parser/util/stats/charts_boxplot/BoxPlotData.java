package net.frootloop.qa.parser.util.stats.charts_boxplot;

import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.util.stats.comparators.ComparatorCommentDensity;
import net.frootloop.qa.parser.util.stats.comparators.ComparatorCommitsPerClass;
import net.frootloop.qa.parser.util.stats.comparators.ComparatorLinesOfCode;
import net.frootloop.qa.parser.util.stats.comparators.ParsedClassComparator;
import net.frootloop.qa.parser.util.stats.comparators.ParsedClassComparator.CompareClassesBy;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoxPlotData {

    private final CompareClassesBy VALUE_TYPE;
    public final double UPPER_LIMIT_VALUE;
    public final double LOWER_LIMIT_VALUE;
    public final double UPPER_QUARTILE_VALUE;
    public final double LOWER_QUARTILE_VALUE;
    public final double MEDIAN_VALUE;
    public final double LENGTH;
    public final ArrayList<ParsedClass> EXTREME_CLASSES;

    /***
     *
     * @param listOfClasses
     * @param sortedBy
     */
    public BoxPlotData(List<ParsedClass> listOfClasses, CompareClassesBy sortedBy) {

        // Sort the list:
        ParsedClassComparator comparator = null;
        if(sortedBy == CompareClassesBy.NUMBER_OF_COMMITS) comparator = new ComparatorCommitsPerClass();
        if(sortedBy == CompareClassesBy.NUMBER_LINES_OF_CODES) comparator = new ComparatorLinesOfCode();
        if(sortedBy == CompareClassesBy.DENSITY_OF_COMMENTS) comparator = new ComparatorCommentDensity();
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
        VALUE_TYPE = sortedBy;
        UPPER_LIMIT_VALUE = upperLimit;
        UPPER_QUARTILE_VALUE = upperQuartile;
        MEDIAN_VALUE = median;
        LOWER_QUARTILE_VALUE = lowerQuartile;
        LOWER_LIMIT_VALUE = lowerLimit;
        LENGTH = length;

        // Get a list of extreme data points, i.e. classes with values exceeding either limit:
        ArrayList<ParsedClass> extremePoints = new ArrayList<>();
        for(ParsedClass c : sortedListOfClasses) {
            double value = median;
            if(sortedBy == CompareClassesBy.NUMBER_OF_COMMITS) value = c.getNumCommits();
            if(sortedBy == CompareClassesBy.NUMBER_LINES_OF_CODES) value = c.getNumLinesCode();
            if(sortedBy == CompareClassesBy.DENSITY_OF_COMMENTS) value = c.getCommentDensity();
            if(value < lowerLimit || value > upperLimit) extremePoints.add(c);
        }
        EXTREME_CLASSES = extremePoints;
    }

    public void print() {
        String dataNameUppercase = "", dataNameShorthand = "";
        if(VALUE_TYPE == CompareClassesBy.DENSITY_OF_COMMENTS) {
            dataNameUppercase = "COMMENTS DENSITY";
            dataNameShorthand = "CD";
        }
        else if(VALUE_TYPE == CompareClassesBy.NUMBER_LINES_OF_CODES) {
            dataNameUppercase = "NUM. LINES OF CODE";
            dataNameShorthand = "NLOC";
        }
        else if(VALUE_TYPE == CompareClassesBy.NUMBER_OF_COMMITS) {
            dataNameUppercase = "NUM. OF COMMITS";
            dataNameShorthand = "number of commits";
        }

        // Print a crude version of the box plot:
        System.out.println("\n[ DATA DISTRIBUTION OF " + dataNameUppercase + " ]" +
                "\nThe median for the " + dataNameShorthand + " of all classes in the repository is " + String.format("%.2f", MEDIAN_VALUE) + " and length is " + String.format("%.2f", LENGTH) +
                "\nCrude rendition of box plot:" +
                "\n  _____   Upper limit: " + String.format("%.2f", UPPER_LIMIT_VALUE) +
                "\n    |   " +
                "\n  __|__   Upper quartile: " + String.format("%.2f", UPPER_QUARTILE_VALUE) +
                "\n  |   | " +
                "\n  |___|   Median: " + String.format("%.2f", MEDIAN_VALUE) +
                "\n  |   | " +
                "\n  |___|   Lower quartile: " + String.format("%.2f", LOWER_QUARTILE_VALUE) +
                "\n    |   " +
                "\n  __|__   Lower limit: " + String.format("%.2f", LOWER_LIMIT_VALUE));

        // Show which classes are extremes:
        if(EXTREME_CLASSES.size() > 0) {
            if(EXTREME_CLASSES.size() == 1) System.out.println("There is a single extreme case in this dataset;");
            else System.out.println("There are " + EXTREME_CLASSES.size() + " single extreme case in this dataset;");

            int counter = 1;
            for(ParsedClass c : EXTREME_CLASSES) {

                if(VALUE_TYPE == CompareClassesBy.DENSITY_OF_COMMENTS) System.out.println("   - Comment density: " + String.format("%.2f", c.getCommentDensity()) + ", Class: '" + c.getSignature() + "'");
                if(VALUE_TYPE == CompareClassesBy.NUMBER_LINES_OF_CODES) System.out.println("   - NLOC: " + c.getNumLinesCode() + ", Class: '" + c.getSignature() + "'");
                if(VALUE_TYPE == CompareClassesBy.DENSITY_OF_COMMENTS) System.out.println("   - Number of commits: " + c.getNumLinesCode() + ", Class: '" + c.getSignature() + "'");


                counter++;
                if(EXTREME_CLASSES.size() > 3 && counter == 4) {
                    System.out.println("... " + (EXTREME_CLASSES.size() - 3) + " more!");
                    break;
                }
            }
        }
        else {
            System.out.println("There are no extreme cases in this dataset.");
        }

        // Whether the distribution has some symmetry:
        if(this.isDistributionSymmetrical(0.05d))
            System.out.println("\nNOTE: The distribution is symmetrical to a margin of error of 5%, which suggests that that the data points are normally distributed.");
        else if(this.isDistributionSymmetrical(0.1d))
            System.out.println("\nNOTE: The distribution is partially (> 90%) symmetrical, which might hint to a normal distribution.");
        else
            System.out.println("\nNOTE: We can reject with the hypothesis that the dataset is normally distributed, as the brackets are less than 90% symmetric.");
    }

    private boolean isDistributionSymmetrical(double percentileMarginOfError) {

        double quartileAlignment = (UPPER_QUARTILE_VALUE - MEDIAN_VALUE) / (MEDIAN_VALUE - LOWER_QUARTILE_VALUE);
        boolean areQuartilesSymmetrical = (quartileAlignment > (1d - percentileMarginOfError/100d)) && (quartileAlignment < (1 + percentileMarginOfError));

        double whiskersAlignment = (UPPER_LIMIT_VALUE - MEDIAN_VALUE) / (MEDIAN_VALUE - LOWER_LIMIT_VALUE);
        boolean areWhiskersSymmetrical = (whiskersAlignment > (1d - percentileMarginOfError/100d)) && (whiskersAlignment < (1 + percentileMarginOfError));

        return areQuartilesSymmetrical && areWhiskersSymmetrical;
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
    private static double GetMedianOf(List<ParsedClass> sortedListOfClasses, CompareClassesBy sortedBy) {

        // If there's only one class in the list, return its value:
        if (sortedListOfClasses.size() == 1) {
            ParsedClass medianClass = sortedListOfClasses.get(0);
            if (sortedBy == CompareClassesBy.NUMBER_OF_COMMITS) return medianClass.getNumCommits();
            if (sortedBy == CompareClassesBy.NUMBER_LINES_OF_CODES) return medianClass.getNumLinesCode();
            if (sortedBy == CompareClassesBy.DENSITY_OF_COMMENTS) return medianClass.getCommentDensity();
        }

        // If the number of elements is odd, return the middle element's value:
        else if (sortedListOfClasses.size() % 2 == 1) {
            ParsedClass medianClass = sortedListOfClasses.get((sortedListOfClasses.size() - 1) / 2);
            if (sortedBy == CompareClassesBy.NUMBER_OF_COMMITS) return medianClass.getNumCommits();
            if (sortedBy == CompareClassesBy.NUMBER_LINES_OF_CODES) return medianClass.getNumLinesCode();
            if (sortedBy == CompareClassesBy.DENSITY_OF_COMMENTS) return medianClass.getCommentDensity();
        }

        // If the number of elements is even, return the average between the two middle elements' values:
        else {
            ParsedClass medianClassBtm = sortedListOfClasses.get(sortedListOfClasses.size() >> 1);
            ParsedClass medianClassTop = sortedListOfClasses.get((sortedListOfClasses.size() >> 1) + 1);
            if (sortedBy == CompareClassesBy.NUMBER_OF_COMMITS)
                return (double) (medianClassBtm.getNumCommits() + medianClassTop.getNumCommits()) / 2.0d;
            if (sortedBy == CompareClassesBy.NUMBER_LINES_OF_CODES)
                return (double) (medianClassBtm.getNumLinesCode() + medianClassTop.getNumLinesCode()) / 2.0d;
            if (sortedBy == CompareClassesBy.DENSITY_OF_COMMENTS)
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
    private static double GetUpperQuartileOf(List<ParsedClass> sortedListOfClasses, CompareClassesBy sortedBy) {

        // If there's only one class in the list, return its value:
        if (sortedListOfClasses.size() == 1) {
            ParsedClass upperQuartileClass = sortedListOfClasses.get(0);
            if (sortedBy == CompareClassesBy.NUMBER_OF_COMMITS) return upperQuartileClass.getNumCommits();
            if (sortedBy == CompareClassesBy.NUMBER_LINES_OF_CODES) return upperQuartileClass.getNumLinesCode();
            if (sortedBy == CompareClassesBy.DENSITY_OF_COMMENTS) return upperQuartileClass.getCommentDensity();
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
    private static double GetLowerQuartileOf(List<ParsedClass> sortedListOfClasses, CompareClassesBy sortedBy) {

        // If there's only one class in the list, return its value:
        if (sortedListOfClasses.size() == 1) {
            ParsedClass lowerQuartileClass = sortedListOfClasses.get(0);
            if (sortedBy == CompareClassesBy.NUMBER_OF_COMMITS) return lowerQuartileClass.getNumCommits();
            if (sortedBy == CompareClassesBy.NUMBER_LINES_OF_CODES) return lowerQuartileClass.getNumLinesCode();
            if (sortedBy == CompareClassesBy.DENSITY_OF_COMMENTS) return lowerQuartileClass.getCommentDensity();
        }

        int indexOfMedian;
        if (sortedListOfClasses.size() % 2 == 1) indexOfMedian = ((sortedListOfClasses.size() - 1) / 2);
        else indexOfMedian = (sortedListOfClasses.size() >> 1) + 1;

        List<ParsedClass> lowerHalf = sortedListOfClasses.subList(0, indexOfMedian);
        return GetMedianOf(lowerHalf, sortedBy);
    }
}
