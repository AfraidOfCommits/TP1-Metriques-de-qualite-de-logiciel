package net.frootloop.qa.parser;

import net.frootloop.qa.parser.util.InputHandler;
import net.frootloop.qa.parser.result.ParsedSourceFile;
import net.frootloop.qa.parser.util.strings.CodeParser;

import java.nio.file.Path;

public class JavaSourceFileParser implements InputHandler, CodeParser {

    private static int numFilesParsed = 0;


    public static void analyseFileAt(Path sourceFilePath) {
        if(sourceFilePath == null) return;

        System.out.println("\n[ PARSING SOURCE FILE ]\nParsing the source files of repository at: \'" + sourceFilePath + "\'...");
        ParsedSourceFile sourceFile = JavaSourceFileParser.parse(sourceFilePath);
        System.out.println("...Done!");

        System.out.println("\n============================\n          ANALYSIS          \n============================");

        System.out.println("\n[ STATISTICS OF FILE ]");
        System.out.println("Consists of " + sourceFile.getClasses().length + " Classes, " + sourceFile.getNumMethods() + " Methods, " + sourceFile.getNumAssertStatements() + " Unit Tests.");
        System.out.println("Number of lines: " + sourceFile.getNumLinesCode() + " are code, " + sourceFile.getNumLinesComments() + " are comments, " + sourceFile.getNumLinesEmpty() + " are empty.\nIn total: " + sourceFile.getNumLines() + " lines.");

        System.out.println("\n[ COMPLEXITY ]");
        System.out.println("Percentage of code dedicated to documentation (CD): " + String.format("%.1f", 100 * sourceFile.getCommentDensity()) + "%" );
        System.out.println("Weighted Methods per Class (WMC): " + String.format("%.1f", sourceFile.getAverageWeightedMethods()));

        System.out.println("\n[ RELIABILITY ]");
        System.out.println("Number of @Test methods : " + sourceFile.getTestMethods().size());
        System.out.println("Methods referenced in test code : " + String.join(",", sourceFile.getTestedMethods()));
    }

    /***
     * Reads a .java given .java file and parses its code to extract information about
     * its classes, statements, number of lines, etc,
     *
     * @param filePath : (Path) Path of the .java file that was found.
     * @return ParsedSourceFile instance with data relating to the .java file's code.
     */
    public static ParsedSourceFile parse(Path filePath) {
        if(filePath == null) return null;

        // Read the file and extract the source code's list of statements;
        if(!filePath.toString().endsWith(".java")) return null;
        ParsedSourceFile parsedFile = new ParsedSourceFile(filePath);
        return parsedFile;
    }

    public static void printCodeOf(Path filePath) {
        if(filePath == null) return;

        ParsedSourceFile parsedFile = JavaSourceFileParser.parse(filePath);
        if(parsedFile != null) parsedFile.print();
    }

    public static void announceParsedFile() {
        System.out.print("Parsed " + ++numFilesParsed + " files.\r");
    }
}

