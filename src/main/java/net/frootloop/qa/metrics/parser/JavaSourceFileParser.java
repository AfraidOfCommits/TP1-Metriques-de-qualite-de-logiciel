package net.frootloop.qa.metrics.parser;

import net.frootloop.qa.metrics.parser.result.ParsedClass;
import net.frootloop.qa.metrics.parser.result.Visibility;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSourceFileParser {

    public void test() throws FileNotFoundException {
        new JavaSourceFileParser().parse("/C:/Users/Alex/Desktop/IFT3913 - Qualité Logiciel/TP1/TP1 Metriques de qualite de logiciel/src/main/java/net/frootloop/qa/metrics/Main.java");
    }

    public static SourceFileData parse(String filePath) throws FileNotFoundException {

        // Read the file and extract the source code's list of statements;
        SourceFileData sourceFileData = JavaSourceFileParser.readSourceFile(filePath);
        LinkedList<String[]> codeBlocks = sourceFileData.getCode();

        // Regex precompiled patterns:
        Pattern classNamePattern = Pattern.compile("(class|interface|enum)\\s(\\w+)");
        Matcher classNameMatcher = null;
        Pattern inheritedClassesPattern = Pattern.compile("(extends|implements)\\s(\\w+((\\s)*,\\s\\w+)*)*");
        Matcher inheritedClassesMatcher = null;

        int i = 0;

        // Cycle through the code:
        for (String[] block : codeBlocks) {

            i ++;

            for (String statement : block) {
                if(i == 3) System.out.println(statement);

                if(statement.matches("^import(.|[^.])*"))
                    sourceFileData.importStatements.add(statement.replaceAll("(\\s|import)", ""));

                else if(statement.matches("^package(.|[^.])*"))
                    sourceFileData.packageName = statement.replaceAll("(\\s|package)", "");

                else if((classNameMatcher = classNamePattern.matcher(statement)).find()) {

                    // Get the visibility type:
                    Visibility visibility;
                    if(statement.contains("Public")) visibility = Visibility.PUBLIC;
                    if(statement.contains("Protected")) visibility = Visibility.PROTECTED;
                    else visibility = Visibility.PRIVATE;

                    // Get the class name:
                    String className = classNameMatcher.group(2);

                    // Create a new ParsedClass object
                    ParsedClass parsedClass = new ParsedClass(className, visibility, sourceFileData.packageName, filePath);

                    // Does the class inherit from another? Get a list of all matching candidates
                    List<String> inheritedClasses = new ArrayList<>();
                    inheritedClassesMatcher = inheritedClassesPattern.matcher(statement);
                    while(inheritedClassesMatcher.find())
                        inheritedClasses.addAll(List.of(inheritedClassesMatcher.group(2).replace(" ", "").split(",")));

                    String[] importStatements = sourceFileData.importStatements.toArray(String[]::new);
                    for (String name : inheritedClasses) {
                        for (String signature : importStatements) {
                            if(signature.matches("([\\w\\d]+\\.)+" + name + "$")) {
                                parsedClass.addParent(signature);
                                sourceFileData.importStatements.remove(signature);
                            }
                        }
                    }

                    // If the class is nested, add the main one to its list of parents. Otherwise, make this the main class!
                    if(sourceFileData.mainClass == null) sourceFileData.mainClass = parsedClass;
                    else {
                        parsedClass.addParent(sourceFileData.mainClass.getSignature());
                        for (String signature : sourceFileData.mainClass.getParentSignatures()) {
                            parsedClass.addParent(signature);
                        }
                    }

                    // Add a new ParsedClass to the list
                    sourceFileData.classes.add(parsedClass);
                }
            }
        }
        return sourceFileData;
    }

    private static SourceFileData readSourceFile(String path) throws FileNotFoundException {
        SourceFileData fileData = new SourceFileData();
        fileData.filePath = path;
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine())
                fileData.addNewLineOfText(myReader.nextLine());
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR. Unable to read file " + path);
            e.printStackTrace();
        };
        return fileData;
    }
}

