package net.frootloop.qa.metrics.parser;

import net.frootloop.qa.metrics.parser.result.ParsedRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class JavaRepositoryParser {

    private static JavaSourceFileParser fileParser = new JavaSourceFileParser();

    public static ParsedRepository parse(Path filePath){
        ParsedRepository repo = new ParsedRepository(filePath);
        JavaRepositoryParser.walk(filePath, new ParsedRepository(filePath));
        return repo;
    }

    /***
     * Called by the parse() function. Checks what's inside a given folder; for each
     * file with a .java extension it finds, it parses the source file and adds its data to
     * the ParsedRepository instance.
     *
     * In the end, the ParsedRepository will contain all relevant data relating to the Java
     * source files contained in a given folder and in all of its subfolders.
     *
     * @param folderPath
     * @param repo
     */
    public static void walk(Path folderPath, ParsedRepository repo) {
        try {
            Files.walk(folderPath)
                    .filter(Files::isRegularFile)
                    .filter(Files::isReadable)
                    .forEach(JavaSourceFileParser::parse);
        }
        catch (IOException e) {
            System.out.println("ERROR: Something went horribly wrong in function visitFolder when trying to list the files of directory :\n" + folderPath);
            e.printStackTrace();
        }
    }
}
