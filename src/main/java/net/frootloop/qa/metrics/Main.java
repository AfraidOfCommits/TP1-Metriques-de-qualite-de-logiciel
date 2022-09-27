package net.frootloop.qa.metrics;
/**   */
import net.frootloop.qa.metrics.parser.JavaSourceFileParser;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        JavaSourceFileParser.test();
    }
}
