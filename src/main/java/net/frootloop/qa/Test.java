package net.frootloop.qa;

import net.frootloop.qa.inputhandling.FilePathHandler;
import net.frootloop.qa.inputhandling.GitGudder;
import net.frootloop.qa.inputhandling.InputHandler;
import net.frootloop.qa.parser.JavaSourceFileParser;
import net.frootloop.qa.parser.StringParser;
import net.frootloop.qa.parser.result.ParsedRepository;

import java.io.IOException;

// test;

public class Test extends JavaSourceFileParser implements StringParser, GitGudder, FilePathHandler, InputHandler {

    private String test1;
    private String test2;

    private class Pouet {
        private class PouetSquared {

        }
    }

    public static void main(String[] args) throws IOException {

        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\parser\\StringParser.java";
        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\Test.java";
        String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\parser\\result\\internal\\CodeTree.java";

        //ParsedSourceFile parsedFile = JavaSourceFileParser.parse(pathString);
        //parsedFile.print();

        //Path root = new File("C:\\").toPath();
        //System.out.println("Path: "  + root.toString());

        //ArrayList<Path> occurrencesOf = FilePathHandler.getPathsToFile("picture");

        //System.out.println(FilePathHandler.getWorkingDirectoryRoot());

        //GitGudder.getLocalGitRepositories();

        InputHandler.promptWelcome();
        InputHandler.promptForRepositoryPath();

        //Path testPath = Path.of("C:\\Users\\Alex\\Documents\\GitHub\\TP1-Metriques-de-qualite-de-logiciel");


        if(1 != 2) return;
        ParsedRepository repo; // = JavaRepositoryParser.parse("C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel");
        System.out.println("\n[ STATISTICS OF REPOSITORY ]");
        System.out.println("Total Cyclomatic Complexity of the Project: " + repo.getTotalCyclomaticComplexity());
        System.out.println("Number of classes: " + repo.getClasses().length);
        System.out.println("Number of tests: " + repo.getNumAssertStatements());
        System.out.println("Number of lines:\n    " + repo.getTotalLinesCode() + " are code, " + repo.getTotalLinesComments() + " are comments, " + repo.getTotalLinesEmpty() + " are empty.\n    " + repo.getTotalLines() + " in total.");

        System.out.println("\n\n[ CLASSES ]");
        System.out.println("Most Complex: " + repo.getMostComplexClass().getSignature() + " with a cyclomatic complexity of " + repo.getMostComplexClass().getCyclomaticComplexity() + ".");
        System.out.println("Most Referenced (Total): " + repo.getMostReferencedClass().getSignature() + ", referenced by " + repo.getNumTimesReferenced(repo.getMostReferencedClass()) + " other classes.");
        System.out.println("Most Referenced (Directly): " + repo.getMostDirectlyReferencedClass().getSignature() + ", referenced by " + repo.getNumTimesReferenced(repo.getMostDirectlyReferencedClass()) + " other classes.");
        System.out.println("Most Referenced (Indirectly): " + repo.getMostIndirectlyReferencedClass().getSignature() + ", referenced by " + repo.getNumTimesReferenced(repo.getMostIndirectlyReferencedClass()) + " other classes.");

        System.out.println(GitGudder.getCommitCountTo(repo.getFilePath()));
    }
}