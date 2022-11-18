package net.frootloop.qa.parser.result;

import net.frootloop.qa.parser.JavaSourceFileParser;
import net.frootloop.qa.parser.result.internal.CodeTree;
import net.frootloop.qa.parser.util.strings.CodeParser;
import net.frootloop.qa.parser.util.strings.SourceCodeFixerUpper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ParsedSourceFile {

    /**
     * The source code is organized as a tree, where blocks of code (i.e. curly braces) are the nodes and where each block is represented by
     * its contained statements, and its leading statement. This allows us to attribute proper class/method ownership, and do fancy things like
     * print a .java file's entire cleaned up source code with proper indentation.
     */
    private CodeTree codeTree;
    private Path filePath;
    private String packageName;

    private int numLines = 0, numLinesEmpty = 0, numLinesComments = 0, numLinesCode = 0;

    /***
     * List of classes found within the source file. One file can declare multiple nested classes, enums, etc.
     */
    private ArrayList<ParsedClass> classes = new ArrayList<>();

    private String[] importStatements;


    /***
     * Parses a given source file
     *
     * @param path : file path and extension.
     * @return Data contained in the file, in the form of a String.
     */
    public ParsedSourceFile(Path path) {
        if(!path.toString().endsWith(".java")) return;
        this.filePath = path;

        try {
            // STEP 1: READ AND CLEAN UP THE TEXT DATA
            //   Remove unnecessary spaces, null chars, normalize line breaks, replace string values with "text", replace comments with "// Comment":
            String sourceFileTextData = SourceCodeFixerUpper.cleanUpSource(Files.readString(path));

            // STEP 2: FETCH DATA RELATING TO CODE STATEMENTS, SUCH AS PACKAGE, IMPORTS, ETC.
            this.importStatements = CodeParser.getImportStatementsOf(sourceFileTextData);
            this.packageName = CodeParser.getPackageNameOf(sourceFileTextData);

            // STEP 3: BUILD CODE TREE AND CLASSES
            this.codeTree = new CodeTree(SourceCodeFixerUpper.getCodeStatementsOf(sourceFileTextData));
            this.classes = this.codeTree.getListOfClasses(this.packageName, this.filePath, this.importStatements);

        } catch (IOException e) {
            System.out.println("\n[ ERROR ]\n Exception thrown in constructor of 'ParsedSourceFile'. Unable to read file " + path.toFile().getAbsolutePath() + "!");
            e.printStackTrace();
        };

        // Let the Parser (and user) know that a file was just successfully parsed:
        JavaSourceFileParser.announceParsedFile();
    }

    public void print() {
        System.out.println("[ PRINTING CONTENTS OF SOURCE FILE ]\nLocation: " + this.filePath.toFile().getAbsolutePath() + "\n");
        System.out.println("(PACKAGE)\n" + this.packageName + "\n");
        System.out.println("(IMPORTED CLASSES)\n" + String.join(";\n", this.importStatements));
        codeTree.print();
    }

    public int getNumLines() { return this.codeTree.getNumLines(); }

    public int getNumLinesCode() { return this.codeTree.getNumLinesCode(); }

    public int getNumLinesEmpty() {
        return this.codeTree.getNumLinesEmpty();
    }

    public int getNumLinesComments(){
        return this.codeTree.getNumLinesComments();
    }

    public int getNumMethods(){
        int numMethods = 0;
        for (ParsedClass c:this.classes)
            numMethods += c.getNumMethods();
        return numMethods;
    }

    public int getNumAssertStatements(){
        int numAsserts = 0;
        for (ParsedClass c:this.classes)
            numAsserts += c.getNumAssertStatements();
        return numAsserts;
    }

    public String getPackageName() {
        return packageName;
    }

    public Path getFilePath() {
        return filePath;
    }

    public ParsedClass[] getClasses() {
        return classes.toArray(new ParsedClass[classes.size()]);
    }

    public float getCommentDensity() {
        return (float)this.numLinesComments / (float)(this.numLinesCode + this.numLinesComments);
    }

    public float getAverageWeightedMethods() {
        float average = 0.0f;
        for (ParsedClass c : this.classes)
            average += c.getWeightedMethods();
        return average / (float)this.classes.size();
    }

    public float getAverageLackOfCohesionInMethods() {
        float average = 0.0f;
        for (ParsedClass c : this.classes)
            average += c.getLackOfCohesionInMethods();
        return average / (float)this.classes.size();
    }

    public ArrayList<ParsedMethod> getTestMethods() {
        ArrayList<ParsedMethod> testMethods = new ArrayList<>();
        for (ParsedClass c : this.classes)
            for (ParsedMethod m : c.getMethods())
                if(m.isTest()) testMethods.add(m);
        return testMethods;
    }

    public ArrayList<String> getTestedMethods() {
        ArrayList<String> testedMethods = new ArrayList<>();
        for (ParsedClass c : this.classes)
            testedMethods.addAll(c.getMethodNamesTestedOutsideClass());
        return testedMethods;
    }
}