package net.frootloop.qa.parser.util.stats.comparators;

import net.frootloop.qa.parser.result.ParsedClass;

import java.util.Comparator;

public interface ParsedClassComparator extends Comparator<ParsedClass> {
    enum CompareClassesBy {
        NUMBER_OF_COMMITS,
        NUMBER_LINES_OF_CODES,
        DENSITY_OF_COMMENTS
    }
}
