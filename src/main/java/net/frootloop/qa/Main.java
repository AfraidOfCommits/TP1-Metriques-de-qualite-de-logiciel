package net.frootloop.qa;

import net.frootloop.qa.parser.JavaSourceFileParser;
import net.frootloop.qa.parser.StringParser;
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
        String pathString = "C:\\Users\\Alex\\Desktop\\IFT3913 - Qualité Logiciel\\TP1\\TP1 Metriques de qualite de logiciel\\src\\main\\java\\net\\frootloop\\qa\\Main.java";
        Path path = Path.of(pathString.replace('/', '\\').replace("\"", ""));
        System.out.println("Current path: " + path.toFile().getAbsolutePath());    //dishai dd   ds

        try {
            String textData = Files.readString(path);
            generateCodeDataFromText(textData);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR. Unable to read file " + path);
            e.printStackTrace();
        }
    }

    public static void generateCodeDataFromText(String sourceFileTextData) {
        // STEP 1: CLEAN UP THE TEXT DATA
        // Remove unnecessary spaces, null chars, and normalize line breaks:
        sourceFileTextData = StringParser.getWithNormalizedLineBreakChars(sourceFileTextData);
        sourceFileTextData = StringParser.getWithoutNullChars(sourceFileTextData);
        sourceFileTextData = StringParser.getWithoutExtraSpaces(sourceFileTextData);
        sourceFileTextData = StringParser.getWithoutUnnecessarySemicolons(sourceFileTextData);
        sourceFileTextData = StringParser.getWithGenericStringValues(sourceFileTextData, "\"text\"");
        sourceFileTextData = StringParser.getWithSingleBracketTryCatch(sourceFileTextData);

        // STEP 2: COUNT LINES
        // Get total number of lines:
        int numLines = StringParser.getLineCountOf(sourceFileTextData);
        int blublublub = numLines == 7 ? 3 : -1;

        // Remove empty lines and count them:
        sourceFileTextData = StringParser.getWithoutEmptyLines(sourceFileTextData);
        int numLinesEmpty = numLines - StringParser.getLineCountOf(sourceFileTextData);

        // Remove multiline docstring comments and single line comments, then count them:
        sourceFileTextData = StringParser.getWithoutComments(sourceFileTextData);
        int numLinesComments = numLines - numLinesEmpty - StringParser.getLineCountOf(sourceFileTextData);

        sourceFileTextData = StringParser.getWithoutLineBreaks(sourceFileTextData);

        CodeTree tree = new CodeTree(sourceFileTextData);
        System.out.println(tree.getCyclomaticComplexity());
        tree.print();
    }
}

// test 2