package net.frootloop.qa;

import net.frootloop.qa.parser.JavaRepositoryParser;
import net.frootloop.qa.parser.JavaSourceFileParser;
import net.frootloop.qa.parser.StringParser;
import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.result.ParsedRepository;
import net.frootloop.qa.parser.result.ParsedSourceFile;
import net.frootloop.qa.parser.result.internal.CodeTree;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

// test;

public class Main extends JavaSourceFileParser implements StringParser {

    private String test1;
    private String test2;

    private class Pouet {
        private class PouetSquared {

        }
    }

    public static void main(String[] args) throws IOException {

        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\parser\\StringParser.java";
        //String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\Main.java";
        String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\parser\\result\\internal\\CodeTree.java";

        ParsedSourceFile parsedFile = JavaSourceFileParser.parse(pathString);
        parsedFile.print();

        ParsedRepository repo = JavaRepositoryParser.parse("C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\");
        System.out.println("\n[ STATISTICS OF REPOSITORY ]");
        System.out.println("Total Cyclomatic Complexity of the Project: " + repo.getTotalCyclomaticComplexity());
        System.out.println("Number of classes: " + repo.getClasses().length);
        System.out.println("Number of tests: " + repo.getNumAssertStatements());
        System.out.println("Number of lines:\n    " + repo.getTotalLinesCode() + " are code, " + repo.getTotalLinesComments() + " are comments, " + repo.getTotalLinesEmpty() + " are empty.\n    " + repo.getTotalLines() + " in total.");
        System.out.println("\n[ CLASSES ]");
        System.out.println("Most Complex: " + repo.getMostComplexClass().getSignature() + " with a cyclomatic complexity of " + repo.getMostComplexClass().getCyclomaticComplexity() + ".");
        System.out.println("Most Referenced (Total): " + repo.getMostReferencedClass().getSignature() + ", referenced by " + repo.getNumTimesReferenced(repo.getMostReferencedClass()) + " other classes.");
        System.out.println("Most Referenced (Directly): " + repo.getMostDirectlyReferencedClass().getSignature() + ", referenced by " + repo.getNumTimesReferenced(repo.getMostDirectlyReferencedClass()) + " other classes.");
        System.out.println("Most Referenced (Indirectly): " + repo.getMostIndirectlyReferencedClass().getSignature() + ", referenced by " + repo.getNumTimesReferenced(repo.getMostIndirectlyReferencedClass()) + " other classes.");
    }
}