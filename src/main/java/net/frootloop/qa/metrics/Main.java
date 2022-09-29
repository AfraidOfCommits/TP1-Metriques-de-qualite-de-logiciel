package net.frootloop.qa.metrics;

import net.frootloop.qa.metrics.parser.JavaSourceFileParser;
import net.frootloop.qa.metrics.parser.SourceFileData;

import java.io.FileNotFoundException;

class Blob{

}

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        // Test!
        SourceFileData f = JavaSourceFileParser.parse("/C:/Users/Alex/Desktop/IFT3913 - Qualité Logiciel/TP1/TP1 Metriques de qualite de logiciel/src/main/java/net/frootloop/qa/metrics/Main.java");


        System.out.println("Number of empty lines : " + f.numLinesEmpty);
        System.out.println("Number of single-lined comments : " + f.numLinesComments);
        System.out.println("Number of other lines (code, docstring, etc.) : " + (f.numLines - f.numLinesEmpty - f.numLinesComments));
        System.out.println("In total, there are " + f.numLines + " lines in this file.");

        System.out.println();
        System.out.println("Package : " + f.packageName);
        System.out.println("Main class name : " + f.mainClassName); // TODO: Fix the regex to capture only a word, and debug why 'Main' wasn't selected for main class
        System.out.println("Import statements : " + String.join("; ", f.importStatements)); // TODO: Fix the className regex to NOT match on print statements, only on declarations*/
    }
}
