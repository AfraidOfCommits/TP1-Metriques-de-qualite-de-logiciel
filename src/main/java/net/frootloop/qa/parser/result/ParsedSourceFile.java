package net.frootloop.qa.parser.result;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;

public class ParsedSourceFile {

    public Path filePath;
    public String packageName;
    public String textData = "";
    public ParsedClass mainClass;
    public int numLines = 0, numLinesEmpty = 0, numLinesComments = 0;


    /***
     * List of classes found within the source file. One file can declare multiple nested classes, enums, etc.
     */
    public ArrayList<ParsedClass> classes = new ArrayList<ParsedClass>();

    /***
     * List of import statements found within the source file.
     */
    public ArrayList<String> importStatements = new ArrayList<String>();

    /***
     * The source code is split by blocks of code (i.e. curly braces), where each block of code is represented by
     * an array of statements. It's good to note that a better representation of code and nesting would've been achieved
     * by creating a proper graph datastructure, but this is overkill for our purposes.
     */
    public LinkedList<String[]> codeBlocks = null;

    public void addNewLineOfText(String lineOfText){
        this.numLines += 1;

        // Replace strings with a generic value:
        lineOfText = lineOfText.replaceAll("\\\"[^\\\"]*\\\"", "\"(string value)\"]");

        // If the line is a single-line comment (like this one!):
        if(lineOfText.matches("\\s*\\/{2,}(.|\\s)*"))
            this.numLinesComments += 1;

            // If the line is just empty:
        else if (lineOfText.matches("\\A[[:blank:]]*\\Z"))
            this.numLinesEmpty += 1;

            // Only add actual lines of code to the output:
        else {
            lineOfText = lineOfText.replaceAll("\\/\\/((?!\\*\\/).)*", ""); // Remove comments appended to code (like this one!)

            // Clean up the line a bit by removing spaces and tabs at the front:
            lineOfText = lineOfText.replaceAll("^\\s+", "");

            this.textData += lineOfText;
        }
    }

    public LinkedList<String[]> getCode(){
        if(this.codeBlocks == null && this.textData != "") this.generateCodeFromTextData();
        return this.codeBlocks;
    }

    private void generateCodeFromTextData(){
        this.cleanUpTextData();
        // Split the code into "blocks", i.e. blocks of code seperated by curly brackets:
        this.codeBlocks = new LinkedList<>();
        for (String nestedCodeBlock : this.textData.split("[\\{\\}]")) {
            // Split the code blocks into individual statements:
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
        this.textData = textData.replaceAll("\\s\\{", "{");
        this.textData = textData.replaceAll("\\s;", ";");
    }

    public String getApproxClassSignature(String className) {

        // We have the className of the class being referenced, but we need its package
        // to be able to add its signature to the ArrayList.

        // If the class was referenced in the import statements, we can find its packageName there:
        String[] importStatementsArray = this.importStatements.toArray(String[]::new);
        for (String signature : importStatementsArray) {
            if(signature.endsWith(className)) return signature;
        }

        // Otherwise, it (very likely) means the class in the same package as current:
        return this.packageName + "." + className;
    }
}