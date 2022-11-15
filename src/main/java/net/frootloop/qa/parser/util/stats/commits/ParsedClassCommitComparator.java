package net.frootloop.qa.parser.util.stats.commits;

import net.frootloop.qa.parser.result.ParsedClass;

import java.util.Comparator;

public class ParsedClassCommitComparator implements Comparator<ParsedClass> {

    /**
     * Serves as a Comparator class in order to sort a list of ParsedClass objects
     * by their number of commits.
     *
     * @param x the first ParsedClass object to be compared.
     * @param y the second ParsedClass object to be compared.
     * @return -1 if x < y, 0 if x == y, or 1 if x > y
     */
    @Override
    public int compare(ParsedClass x, ParsedClass y) {
        int commitsX = x.getNumCommits();
        int commitsY = y.getNumCommits();

        // Returns -1 if (x < y):
        if(commitsX < commitsY) return -1;

        // Returns 0 if (x == y):
        if(commitsX == commitsY) return 0;

        // Returns 1 if (x > y):
        return 1;
    }
}
