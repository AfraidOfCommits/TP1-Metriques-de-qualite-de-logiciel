package net.frootloop.qa.parser.util.stats.commits;

import net.frootloop.qa.parser.result.ParsedClass;

import java.util.Comparator;

public class ParsedClassCommitComparator implements Comparator<ParsedClass> {

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
