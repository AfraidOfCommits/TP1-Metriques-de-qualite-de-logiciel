package net.frootloop.qa.metrics.parser;

import net.frootloop.qa.metrics.parser.result.ParsedClass;
import net.frootloop.qa.metrics.parser.result.ParsedSourceFile;
import net.frootloop.qa.metrics.parser.result.Visibility;

import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSourceFileParser {

    private static final Pattern newClassObjectPattern = Pattern.compile(".*new ([A-Z]\\w*)\\(.*\\).*");
    private static final Pattern classVariablePattern = Pattern.compile("(|\\s|\\(|,)([A-Z]\\w*)\\s\\w*");
    private static final Pattern inheritedClassesPattern = Pattern.compile("(extends|implements)\\s(\\w+((\\s)*,\\s\\w+)*)*");
    private static final Pattern classNamePattern = Pattern.compile("(class|interface|enum)\\s(\\w+)");


    /***
     * Reads a .java given .java file and parses its code to extract information about
     * its classes, statements, number of lines, etc,
     *
     * @param filePathString : (String) Path of the .java file that was found.
     * @return ParsedSourceFile instance with data relating to the .java file's code.
     */
    public static ParsedSourceFile parse(String filePathString) {
        Path path = Path.of(filePathString.replace('/', '\\').replace(":",""));
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
        ParsedSourceFile parsedFile = JavaSourceFileParser.readSourceFile(filePath);
        if(parsedFile == null) return null;

        // Linked list of code statements:
        LinkedList<String[]> codeBlocks = parsedFile.getCode();

        // Prepare some regex tools for class name detection:
        Matcher classNameMatcher, inheritedClassesMatcher;

        // Cycle through the code:
        for (String[] block : codeBlocks) {
            for (String statement : block) {

                // Check for import statements; they'll be useful for getting the packages of referenced classes
                if(statement.matches("^import(.|[^.])*"))
                    parsedFile.importStatements.add(statement.replaceAll("(\\s|import)", ""));

                // Check for the current package name:
                else if(statement.matches("^package(.|[^.])*"))
                    parsedFile.packageName = statement.replaceAll("(\\s|package)", "");

                // Check for classes declared on the heap with the "new" keyword, and add them as a reference of the main class:
                else if(newClassObjectPattern.matcher(statement).find()){
                    // Note: It's very unlikely, but if ever a class instance is created BEFORE ANY class declaration
                    // in the file, they won't count as a reference.
                    if(!parsedFile.classes.isEmpty()) {
                        Matcher newClassObjectMatcher = newClassObjectPattern.matcher(statement);
                        while (newClassObjectMatcher.find()) {
                            String nameOfInstantiatedClass = newClassObjectMatcher.group(1);
                            String signOfInstantiatedClass = parsedFile.getApproxClassSignature(nameOfInstantiatedClass);
                            parsedFile.classes.get(0).addReferenceTo(signOfInstantiatedClass);
                        }
                    }
                }

                // Check for classes referenced as variables, and add them as a reference of the main class:
                else if(classVariablePattern.matcher(statement).find()) {
                    // Note: It's very unlikely, but if ever a class instance is created BEFORE ANY class declaration
                    // in the file, they won't count as a reference.
                    if(!parsedFile.classes.isEmpty()) {
                        Matcher classVariableMatcher = classVariablePattern.matcher(statement);
                        while (classVariableMatcher.find()) {
                            String nameOfVariableClass = classVariableMatcher.group(2);
                            String signOfVariableClass = parsedFile.getApproxClassSignature(nameOfVariableClass);
                            parsedFile.classes.get(0).addReferenceTo(signOfVariableClass);
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
                    ParsedClass parsedClass = new ParsedClass(className, visibility, parsedFile.packageName, filePath);

                    // Does the class inherit from another? Get a list of all matching candidates
                    List<String> inheritedClasses = new ArrayList<>();
                    while((inheritedClassesMatcher = inheritedClassesPattern.matcher(statement)).find()) {
                        inheritedClasses.addAll(List.of(inheritedClassesMatcher.group(2).replace(" ", "").split(",")));
                    };

                    // For each inherited class, add their signature to the ParsedClass
                    for (String name : inheritedClasses) {
                        String signatureOfParent = parsedFile.getApproxClassSignature(name);
                        parsedClass.addParent(signatureOfParent);
                    }

                    // If the class is nested, add the main one to its list of parents. Otherwise, make this the main class!
                    if(parsedFile.mainClass == null) parsedFile.mainClass = parsedClass;
                    else {
                        parsedClass.addParent(parsedFile.mainClass.getSignature());
                        for (String signature : parsedFile.mainClass.getParentSignatures()) {
                            parsedClass.addParent(signature);
                        }
                    }

                    // Add a new ParsedClass to the list
                    parsedFile.classes.add(parsedClass);
                }
            }
        }
        return parsedFile;
    }

    /***
     * Called by the parse() function to get the data of a given file, in string format.
     *
     * @param path : file path and extension.
     * @return Data contained in the file, in the form of a String.
     */
    private static ParsedSourceFile readSourceFile(Path path) {
        if(!path.toString().endsWith(".java")) return null;
        ParsedSourceFile parsedFile = new ParsedSourceFile();
        parsedFile.filePath = path;

        try {
            File myObj = new File(path.toFile().getPath());
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine())
                parsedFile.addNewLineOfText(myReader.nextLine());
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR. Unable to read file " + path);
            e.printStackTrace();
        };
        return parsedFile;
    }
}

