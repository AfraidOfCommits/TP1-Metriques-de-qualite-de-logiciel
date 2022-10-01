package net.frootloop.qa.metrics.parser;

import net.frootloop.qa.metrics.parser.result.ParsedRepository;
import net.frootloop.qa.metrics.parser.result.ParsedSourceFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class JavaRepositoryParser {

    /***
     * Builds and returns a data container for information relating to the given repository, including
     * class references, number of lines, etc.
     *
     * @param filePath : root from which .java files will be analyzed and parsed.
     * @return ParsedRepository instance
     */
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
        if(repo == null) return;
        try {
            Files.walk(folderPath)
                    .filter(Files::isRegularFile)
                    .filter(Files::isReadable)
                    .forEach((filePath) -> {
                        ParsedSourceFile sourceFile = JavaSourceFileParser.parse(filePath);
                        if (sourceFile != null) repo.addParsedFile(sourceFile);
                    });
        }
        catch (IOException e) {
            System.out.println("ERROR: Something went horribly wrong in function visitFolder when trying to list the files of directory :\n" + folderPath);
            e.printStackTrace();
        }
    }

    private static void addFileToRepository(Path filePath, ParsedRepository repo) {
        ParsedSourceFile sourceFile = JavaSourceFileParser.parse(filePath);
        if(sourceFile != null) repo.addParsedFile(sourceFile);
    }
}
