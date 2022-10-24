package net.frootloop.qa.parser;

import net.frootloop.qa.parser.inputhandling.InputHandler;
import net.frootloop.qa.parser.result.ParsedSourceFile;

import java.nio.file.Path;

public class JavaSourceFileParser extends InputHandler implements StringParser {

    /***
     * (Vocally) Reads a .java given .java file and parses its code to extract information about
     * its classes, statements, number of lines, etc,
     *
     * @param filePathString : (String) Path of the .java file that was found.
     * @return ParsedSourceFile instance with data relating to the .java file's code.
     */
    public static ParsedSourceFile parse(String filePathString) {
        Path path = Path.of(filePathString.replace('/', '\\').replace("\"", ""));

        System.out.println("[ Parsing Source File ] \nLocation given: " + path.toFile().getAbsolutePath() + "\n");
        if(!path.toString().endsWith(".java")) System.out.println("[ ERROR ] \nFile at given location does not end with \'.java\'!");

        return parse(path);
    }

    /***
     * Reads a .java given .java file and parses its code to extract information about
     * its classes, statements, number of lines, etc,
     *
     * @param filePath : (Path) Path of the .java file that was found.
     * @return ParsedSourceFile instance with data relating to the .java file's code.
     */
    public static ParsedSourceFile parse(Path filePath) {

        // Read the file and extract the source code's list of statements;
        if(!filePath.toString().endsWith(".java")) return null;
        ParsedSourceFile parsedFile = new ParsedSourceFile(filePath);
        return parsedFile;
    }
}

