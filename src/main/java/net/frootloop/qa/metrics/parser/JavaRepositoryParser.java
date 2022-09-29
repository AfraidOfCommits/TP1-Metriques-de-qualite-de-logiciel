package net.frootloop.qa.metrics.parser;

import net.frootloop.qa.metrics.parser.result.ParsedRepository;

public class JavaRepositoryParser {

    private static JavaSourceFileParser fileParser = new JavaSourceFileParser();

    public static ParsedRepository parse(String filePath){
        ParsedRepository repo = new ParsedRepository(filePath);
        JavaRepositoryParser.visitFolder(filePath, repo);
        return repo;
    }

    private static void visitFolder(String folderPath, ParsedRepository repo){
        // See what's inside the folder:
        // For each .java file in the folder:
            // Parse the file using JavaSourceFileParser:
            // Add each ParsedClass to repo:
        // Recursively call on every subfolder:
    }

}
