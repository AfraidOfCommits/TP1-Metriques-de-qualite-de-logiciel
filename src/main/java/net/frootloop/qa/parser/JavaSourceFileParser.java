package net.frootloop.qa.parser;

import net.frootloop.qa.inputhandling.InputHandler;
import net.frootloop.qa.parser.result.ParsedSourceFile;

import java.nio.file.Path;

public class JavaSourceFileParser implements InputHandler, StringParser {

    private static int numFilesParsed = 0;


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

