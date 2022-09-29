package net.frootloop.qa.metrics;

import net.frootloop.qa.metrics.parser.JavaSourceFileParser;
import net.frootloop.qa.metrics.parser.SourceFileData;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        // Test!
        SourceFileData f = JavaSourceFileParser.getParsedClassDataOf("/C:/Users/Alex/Desktop/IFT3913 - Qualité Logiciel/TP1/TP1 Metriques de qualite de logiciel/src/main/java/net/frootloop/qa/metrics/Main.java");
        System.out.println("Number of empty lines : " + f.numLinesEmpty);
        System.out.println("Number of single-lined comments : " + f.numLinesComments);
        System.out.println("Number of other lines (code, etc.) : " + (f.numLines - f.numLinesEmpty - f.numLinesComments));
        System.out.println("In total, there are " + f.numLines + " lines in this file.");
    }
}
