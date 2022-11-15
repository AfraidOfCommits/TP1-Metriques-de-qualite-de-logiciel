package net.frootloop.qa.parser.util.stats;

import java.security.InvalidParameterException;

public class BoxPlotDataContainer {
    public final float UPPER_WHISKER_VALUE;
    public final float LOWER_WHISKER_VALUE;
    public final float UPPER_QUARTILE_VALUE;
    public final float LOWER_QUARTILE_VALUE;
    public final float MEDIAN_VALUE;

    public BoxPlotDataContainer(float upperWhisker, float upperQuartile, float median, float lowerQuartile, float lowerWhisker) {

        boolean isOrderValid = (upperWhisker >= upperQuartile) && (upperQuartile >= median) && (median >= lowerQuartile) && (lowerQuartile >= lowerWhisker);
        if(!isOrderValid) {
            System.out.println("\n[ FATAL CONSTRUCTOR ERROR ]\nInput data to 'BoxPlotDataContainer' class constructor is invalid.\nInput values should respect order: 'upperWhisker' >= 'upperQuartile' >= 'median' >= 'lowerQuartile' >= 'lowerWhisker'.");
            if(!(upperWhisker >= upperQuartile)) System.out.println("Box plots require the upper whisker to be higher or equal to the upper quartile, yet inputted values were " + upperWhisker + " and " + upperQuartile + " respectively.");
            if(!(upperQuartile >= median)) System.out.println("Box plots require the upper quartile to be higher or equal to the median, yet inputted values were " + upperQuartile + " and " + median + " respectively.");
            if(!(upperWhisker >= upperQuartile)) System.out.println("Box plots require the median to be higher or equal to the lower quartile, yet inputted values were " + median + " and " + lowerQuartile + " respectively.");
            if(!(upperWhisker >= upperQuartile)) System.out.println("Box plots require the lower quartile to be higher or equal to the lower whisker, yet inputted values were " + upperWhisker + " and " + upperQuartile + " respectively.");
            throw new InvalidParameterException();
        }

        UPPER_WHISKER_VALUE = upperWhisker;
        UPPER_QUARTILE_VALUE = upperQuartile;
        MEDIAN_VALUE = median;
        LOWER_QUARTILE_VALUE = lowerQuartile;
        LOWER_WHISKER_VALUE = lowerWhisker;
    }
}
