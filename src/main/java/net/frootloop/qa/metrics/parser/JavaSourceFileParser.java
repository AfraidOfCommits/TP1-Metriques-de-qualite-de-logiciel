package net.frootloop.qa.metrics.parser;

import net.frootloop.qa.metrics.parser.result.ParsedClass;
import net.frootloop.qa.metrics.parser.result.Visibility;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSourceFileParser {

    public void test() throws FileNotFoundException {
        new JavaSourceFileParser().getParsedClassDataOf("/C:/Users/Alex/Desktop/IFT3913 - Qualité Logiciel/TP1/TP1 Metriques de qualite de logiciel/src/main/java/net/frootloop/qa/metrics/Main.java");
    }

    public ArrayList<ParsedClass> getParsedClassDataOf(String filePath) throws FileNotFoundException {

        // List of classes found within the source file (one file can declare multiple nested classes)
        ArrayList<ParsedClass> classes = new ArrayList<>();

        // Read the file and extract the source code's list of statements;
        SourceFileData fileData = this.readSourceFile(filePath);
        LinkedList<String[]> codeBlocks = fileData.getCode();

        // Regex precompiled patterns:
        Pattern classNamePattern = Pattern.compile("(class|interface|enum)\\s(\\w+)");
        Matcher classNameMatcher = null;
        Pattern inheritedClassesPattern = Pattern.compile("(extends|implements)\\s(\\w+((\\s)*,\\s\\w+)*)*");
        Matcher inheritedClassesMatcher = null;

        // Cycle through the code:
        for (String[] block : codeBlocks) {
            for (String statement : block) {

                if(statement.contains("import"))
                    fileData.importStatements.add(statement.replaceAll("(\\s|import)", ""));

                else if(statement.contains("package"))
                    fileData.packageName = statement.replaceAll("(\\s|package)", "");

                else if((classNameMatcher = classNamePattern.matcher(statement)).find()) {

                    // Get the visibility type:
                    Visibility visibility;
                    if(statement.contains("Public")) visibility = Visibility.PUBLIC;
                    if(statement.contains("Protected")) visibility = Visibility.PROTECTED;
                    else visibility = Visibility.PRIVATE;

                    // Get the class name:
                    String className = classNameMatcher.group(2);

                    // Create a new ParsedClass object
                    ParsedClass parsedClass = new ParsedClass(className, visibility, fileData.packageName, filePath);

                    // Does the class inherit from another? Get a list of all matching candidates
                    List<String> inheritedClasses = new ArrayList<>();
                    while((inheritedClassesMatcher = inheritedClassesPattern.matcher(statement)).find())
                        inheritedClasses.addAll(List.of(inheritedClassesMatcher.group(2).replace(" ", "").split(",")));

                    for (String name : inheritedClasses) {
                        for (String signature : fileData.importStatements) {
                            if(signature.matches("(\\w\\.)+" + name))
                                parsedClass.addParent(signature);
                        }
                    }

                    // If the class is nested, add the main one to its list of parents. Otherwise, make this the main class!
                    if(fileData.mainClassName == null) fileData.mainClassName = className;
                    else parsedClass.addParent(fileData.packageName + fileData.mainClassName);

                    // Add a new ParsedClass to the list
                    classes.add(parsedClass);
                }
            }
        }
        return classes;
    }

    private SourceFileData readSourceFile(String path) throws FileNotFoundException {
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

    private class SourceFileData {

        private String packageName, mainClassName, filePath;
        private ArrayList<String> importStatements;
        public int numLines, numLinesEmpty, numLinesComments;
        private String textData = null;
        private LinkedList<String[]> codeBlocks = null;

        public void addNewLineOfText(String lineOfText){
            this.numLines += 1;

            // Replace strings with a generic value:
            lineOfText = lineOfText.replaceAll("\\\"[^\\\"]*\\\"", "\"(string value)\"]");

            // Clean up the line a bit by removing extraneous spaces:
            lineOfText = lineOfText.replaceAll("\\s+", " ");
            lineOfText = lineOfText.replaceAll(";\\s", ";");
            lineOfText = lineOfText.replaceAll("}\\s", "}");

            // If the line is a single-line comment (like this one!):
            if(lineOfText.matches("/[[:blank:]]*\\/{2,}/gm"))
                this.numLinesComments += 1;

                // If the line is just empty:
            else if (lineOfText.matches("/\\A[[:blank:]]*\\Z/gm"))
                this.numLinesEmpty += 1;

                // Only add actual lines of code to the output:
            else {
                lineOfText = lineOfText.replaceAll("\\/\\/.*", ""); // Remove comments appended to code (like this one!)
                this.textData += lineOfText;
            }
        }

        public LinkedList<String[]> getCode(){
            if(this.codeBlocks == null && this.textData != null) {
                this.generateCodeFromTextData();
            }
            return this.codeBlocks;
        }

        private void generateCodeFromTextData(){
            this.cleanUpTextData();
            this.codeBlocks = new LinkedList<>();
            for (String nestedCodeBlock : this.textData.split("[\\{\\}]")) {
                codeBlocks.add(nestedCodeBlock.split(";"));
            }
        }

        private void cleanUpTextData(){
            // Remove null chars:
            this.textData = textData.replaceAll("\0", " ");

            // Remove multiline comments:
            this.textData = textData.replaceAll("(\\/\\*).*(\\*\\/)", " ");

            // Remove extra spaces:
            this.textData = textData.replaceAll("\\s+", " ");
        }
    }
}

