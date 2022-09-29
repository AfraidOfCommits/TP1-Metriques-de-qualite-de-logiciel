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

    public static SourceFileData parse(String filePath) throws FileNotFoundException {

        // Read the file and extract the source code's list of statements;
        SourceFileData sourceFileData = JavaSourceFileParser.readSourceFile(filePath);
        LinkedList<String[]> codeBlocks = sourceFileData.getCode();

        // Regex precompiled patterns:
        Pattern classNamePattern = Pattern.compile("(class|interface|enum)\\s(\\w+)");
        Matcher classNameMatcher = null;

        Pattern inheritedClassesPattern = Pattern.compile("(extends|implements)\\s(\\w+((\\s)*,\\s\\w+)*)*");
        Matcher inheritedClassesMatcher = null;

        Pattern newClassObjectPattern = Pattern.compile(".*new ([A-Z]\\w*)\\(.*\\).*");
        Matcher newClassObjectMatcher = null;

        Pattern classVariablePattern = Pattern.compile("(|\\s|\\(|,)([A-Z]\\w*)\\s\\w*");
        Matcher classVariableMatcher = null;

        int i = 0;

        // Cycle through the code:
        for (String[] block : codeBlocks) {

            i ++;

            for (String statement : block) {
                if(i == 3) System.out.println(statement);

                // Check for import statements; they'll be useful for getting the packages of referenced classes
                if(statement.matches("^import(.|[^.])*"))
                    sourceFileData.importStatements.add(statement.replaceAll("(\\s|import)", ""));

                // Check for the current package name:
                else if(statement.matches("^package(.|[^.])*"))
                    sourceFileData.packageName = statement.replaceAll("(\\s|package)", "");


                /*
                 *  Note: unfortunately it's not possible to keep track of which class we're currently parsing due to the
                 *  choice of using a linked list instead of a full graph to represent the code's structure.
                 *
                 *  So we lose a bit of accuracy when it comes to assigning who references who. Luckily that isn't useful
                 *  data for the purposes of this project, but a good thing to keep in mind for future improvements.
                 */

                // Check for classes declared on the heap with the "new" keyword, and add them as a reference of the main class:
                else if(newClassObjectPattern.matcher(statement).find()){
                    // Note: It's very unlikely, but if ever a class instance is created BEFORE ANY class declaration
                    // in the file, they won't count as a reference.
                    if(!sourceFileData.classes.isEmpty()) {
                        newClassObjectMatcher = newClassObjectPattern.matcher(statement);
                        while (newClassObjectMatcher.find()) {
                            String signatureOfInstantiated = sourceFileData.getApproxClassSignature(newClassObjectMatcher.group(1));
                            sourceFileData.classes.get(0).addReferenceTo(signatureOfInstantiated);
                        }
                    }
                }

                // Check for classes referenced as variables, and add them as a reference of the main class:
                else if(classVariablePattern.matcher(statement).find()) {
                    // Note: It's very unlikely, but if ever a class instance is created BEFORE ANY class declaration
                    // in the file, they won't count as a reference.
                    if(!sourceFileData.classes.isEmpty()) {
                        classVariableMatcher = classVariablePattern.matcher(statement);
                        while (classVariableMatcher.find()) {
                            String signatureOfVariable = sourceFileData.getApproxClassSignature(classVariableMatcher.group(1));
                            sourceFileData.classes.get(0).addReferenceTo(signatureOfVariable);
                        }
                    }
                }

                // Check for a class declaration:
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
                    while(inheritedClassesMatcher.find()) {
                        inheritedClasses.addAll(List.of(inheritedClassesMatcher.group(2).replace(" ", "").split(",")));
                    };

                    // For each inherited class, add their signature to the ParsedClass
                    for (String name : inheritedClasses) {
                        String signatureOfParent = sourceFileData.getApproxClassSignature(name);
                        parsedClass.addParent(signatureOfParent);
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

