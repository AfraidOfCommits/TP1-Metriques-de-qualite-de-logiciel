package net.frootloop.qa.metrics;

import net.frootloop.qa.metrics.jls.JLS;
import net.frootloop.qa.metrics.parser.JavaRepositoryParser;
import net.frootloop.qa.metrics.parser.JavaSourceFileParser;
import net.frootloop.qa.metrics.parser.result.ParsedClass;
import net.frootloop.qa.metrics.parser.result.ParsedRepository;
import net.frootloop.qa.metrics.parser.result.ParsedSourceFile;

import java.io.FileNotFoundException;
import java.nio.file.Path;

public class Main extends JavaSourceFileParser {

    public class Maine {
        // Nested class
    }

    public static void main(String[] args) throws FileNotFoundException {
        JLS.print("");
        //whoNeedsUnitTests();
    }

    private static void whoNeedsUnitTests(){

        // Test the repo parser:
        //JavaRepositoryParser.walk(Path.of(""), new ParsedRepository(Path.of("")));

        // Test the file parser:
        Path path = Path.of("./src/main/java/net/frootloop/qa/metrics/Main.java");
        ParsedSourceFile f = JavaSourceFileParser.parse(path);
        System.out.println("\n");

        System.out.println("Number of empty lines : " + f.numLinesEmpty);
        System.out.println("Number of single-lined comments : " + f.numLinesComments);
        System.out.println("Number of other lines (code, docstring, etc.) : " + (f.numLines - f.numLinesEmpty - f.numLinesComments));
        System.out.println("In total, there are " + f.numLines + " lines in this file.");

        System.out.println();
        System.out.println("Package : " + f.packageName);
        System.out.println("Number of classes : " + f.classes.size());
        System.out.print("\nClasses : ");
        for (ParsedClass cow:f.classes) {
            System.out.print(cow.getClassName() + ", ");
        }
        System.out.println("\nMain class name : " + f.mainClass.getClassName());
        System.out.println("Import statements : " + String.join("; ", f.importStatements));

        System.out.println();
        for(int i = 0; i < f.classes.size(); i++) {
            ParsedClass c = f.classes.get(i);
            System.out.println("The class " + c.getClassName() + " ("+ c.getSignature() + ") inherits from " + String.join(", ", c.getParentSignatures()));
        }
    }
}