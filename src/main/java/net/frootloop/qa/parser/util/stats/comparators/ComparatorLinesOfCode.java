package net.frootloop.qa.parser.util.stats.comparators;

import net.frootloop.qa.parser.result.ParsedClass;

public class ComparatorLinesOfCode implements ParsedClassComparator {

    /**
     * Serves as a Comparator class in order to sort a list of ParsedClass objects
     * by their number of lines of code.
     *
     * @param x the first ParsedClass object to be compared.
     * @param y the second ParsedClass object to be compared.
     * @return -1 if x < y, 0 if x == y, or 1 if x > y
     */
    @Override
    public int compare(ParsedClass x, ParsedClass y) {
        int numLinesCodeX = x.getNumLinesCode();
        int numLinesCodeY = y.getNumLinesCode();

        // Returns -1 if (x < y):
        if(numLinesCodeX < numLinesCodeY) return -1;

        // Returns 0 if (x == y):
        if(numLinesCodeX == numLinesCodeY) return 0;

        // Returns 1 if (x > y):
        return 1;
    }
}
