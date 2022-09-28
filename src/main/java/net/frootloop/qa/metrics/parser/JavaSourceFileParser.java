package net.frootloop.qa.metrics.parser;

import net.frootloop.qa.metrics.parser.result.ParsedClass;
import net.frootloop.qa.metrics.parser.result.Visibility;

import java.util.ArrayList;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.LinkedList;
import java.util.Scanner; // Import the Scanner class to read text files

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

        // Cycle through the code:
        boolean isFirstCodeBlock = true;
        for (String[] block : codeBlocks) {
            for (String statement : block) {

                if(statement.contains("import"))
                    fileData.importStatements.add(statement.replaceAll("(\\s|import)", ""));

                else if(statement.contains("package"))
                    fileData.packageName = statement.replaceAll("(\\s|package)", "");

                else if(statement.matches("(class|interface|enum)")) {
                    // Add a new ParsedClass to the list
                }
            }
        }


        // Locate statements only (we don't need to look at nested bodies yet)


        // Split the code by its nesting levels, i.e. curly backets, and iterate on each level:

        // Split each nested level of the code into individual statements:

        // Get the package name (from the first level):

        // Get a list of all the import statements (from the first level):

        // Get the main class from the file (from the first level):

        // For every other level, parse its statements and look for nested classes:


        return classes;
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

    private static ParsedClass parseCodeStatement(String blockOfCode){
        if(!blockOfCode.contains("(class|interface|enum)"))
            return null; // No new object is declared, so return result is null.
        else {
            String className;
            Visibility visibility;

            // Split the code into statements (lines of code):
            String[] codeStatements = blockOfCode.split(";");

            for (String s : codeStatements) {

                // If the statement is a class declaration, jackpot!
                if(s.contains("(class|interface)")){

                    // Get the visibility type:
                    if(s.contains("Public")) visibility = Visibility.PUBLIC;
                    if(s.contains("Protected")) visibility = Visibility.PROTECTED;
                    else visibility = Visibility.PRIVATE;

                    // Get the class name:

                    // Does the class inherit from another? If so, add a reference to it:

                    // Does the class implement an interface? If so, add a reference to it:

                }
            }

            //ParsedClass c = new ParsedClass(filePath, packageName, className, visibility);
            return null;
        }
    }
}

