package net.frootloop.qa.parser;

import net.frootloop.qa.parser.result.ParsedRepository;
import net.frootloop.qa.parser.result.ParsedSourceFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaRepositoryParser {

    public void analyseRepositoryAt(Path directory) {
        ParsedRepository repo = JavaRepositoryParser.parse(directory);

    }

    /***
     * Builds and returns a data container for information relating to the given repository, including
     * class references, number of lines, etc.
     *
     * @param directory : (Path) directory from which .java files will be analyzed and parsed.
     * @return ParsedRepository instance
     */
    private static ParsedRepository parse(Path directory){
        ParsedRepository repo = new ParsedRepository(directory);
        JavaRepositoryParser.walk(directory, repo);
        repo.buildReferences();
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
     * @param path
     * @param repo
     */
    private static void walk(Path path, ParsedRepository repo) {
        if(repo == null) return;
        try {
            Files.walk(path)
                    .filter(Files::isRegularFile)
                    .filter(Files::isReadable)
                    .forEach((filePath) -> {
                        ParsedSourceFile sourceFile = JavaSourceFileParser.parse(filePath);

                        // Add to the repository:
                        if (sourceFile != null) repo.addParsedFile(sourceFile);

                    });
        }
        catch (IOException e) {
            System.out.println("ERROR: Something went horribly wrong in function visitFolder when trying to list the files of directory :\n" + path);
            e.printStackTrace();
        }
    }

    private static void addFileToRepository(Path filePath, ParsedRepository repo) {
        ParsedSourceFile sourceFile = JavaSourceFileParser.parse(filePath);
        if(sourceFile != null) repo.addParsedFile(sourceFile);
    }
}
