package net.frootloop.qa.metrics;

import net.frootloop.qa.metrics.parser.JavaRepositoryParser;
import net.frootloop.qa.metrics.parser.JavaSourceFileParser;
import net.frootloop.qa.metrics.parser.result.ParsedClass;
import net.frootloop.qa.metrics.parser.result.ParsedSourceFile;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends JavaSourceFileParser {

    public class Maine {
        // Nested class
    }

    public static void main(String[] args) throws FileNotFoundException {

        whoNeedsUnitTests();
    }

    private static void whoNeedsUnitTests(){

        // Test the repo parser:
        JavaRepositoryParser.walk(Path.of(""), null);

        // Test the file parser:
        Path path = Path.of("/C:/Users/Alex/Desktop/IFT3913 - Qualité Logiciel/TP1/TP1 Metriques de qualite de logiciel/src/main/java/net/frootloop/qa/metrics/Main.java");
        ParsedSourceFile f = JavaSourceFileParser.parse(path);
        System.out.println("\n");

        System.out.println("Number of empty lines : " + f.numLinesEmpty);
        System.out.println("Number of single-lined comments : " + f.numLinesComments);
        System.out.println("Number of other lines (code, docstring, etc.) : " + (f.numLines - f.numLinesEmpty - f.numLinesComments));
        System.out.println("In total, there are " + f.numLines + " lines in this file.");

        System.out.println();
        System.out.println("Package : " + f.packageName);
        System.out.println("Main class name : " + f.mainClass.getClassName());
        System.out.println("Import statements : " + String.join("; ", f.importStatements));

        System.out.println();
        for(int i = 0; i < f.classes.size(); i++) {
            ParsedClass c = f.classes.get(i);
            System.out.println("The class " + c.getClassName() + " ("+ c.getSignature() + ") inherits from " + String.join(", ", c.getParentSignatures()));
        }
    }
}
