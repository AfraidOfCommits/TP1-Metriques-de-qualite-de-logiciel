package net.frootloop.qa.parser;

import net.frootloop.qa.parser.result.ParsedSourceFile;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class JavaSourceFileParser implements StringParser {

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
        if(!filePath.toString().endsWith(".java")) return null;
        ParsedSourceFile parsedFile = new ParsedSourceFile(filePath);

        /*
        // Cycle through the code:
        for (String[] block : codeBlocks) {
            for (String statement : block) {

                // Check for a class declaration:
                Matcher classNameMatcher = classNamePattern.matcher(statement);
                if(classNameMatcher.find()) {

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
                    Matcher inheritedClassesMatcher = inheritedClassesPattern.matcher(statement);
                    while(inheritedClassesMatcher.find()) {
                        inheritedClasses.addAll(List.of(inheritedClassesMatcher.group(2).replace(" ", "").split(",")));
                    };

                    // For each inherited class, add their signature to the ParsedClass
                    for (String name : inheritedClasses) {
                        String signatureOfParent = parsedFile.getApproxClassSignature(name);
                        parsedClass.addParent(signatureOfParent);
                    }

                    // Check if this is the first class declared. If so, make this the main class!
                    if(parsedFile.mainClass == null)
                        parsedFile.mainClass = parsedClass;
                    // Otherwise, we assume that the class is nested, so we add the main one to its list of parents:
                    else {
                        parsedClass.setPackageName(parsedFile.packageName + "." + parsedFile.mainClass.getClassName());
                        parsedClass.addParent(parsedFile.mainClass.getSignature());
                        for (String signature : parsedFile.mainClass.getParentSignatures()) {
                            parsedClass.addParent(signature);
                        }
                    }

                    // Add a new ParsedClass to the list
                    parsedFile.classes.add(parsedClass);
                }

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
            }
        }*/
        return parsedFile;
    }
}

