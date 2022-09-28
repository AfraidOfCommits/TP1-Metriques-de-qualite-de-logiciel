package net.frootloop.qa.metrics.parser;

import net.frootloop.qa.metrics.parser.result.ParsedClass;
import net.frootloop.qa.metrics.parser.result.Visibility;

import java.util.ArrayList;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class JavaSourceFileParser {

    public static void test() throws FileNotFoundException {
        JavaSourceFileParser.getParsedClassDataOf("/C:/Users/Alex/Desktop/IFT3913 - Qualité Logiciel/TP1/TP1 Metriques de qualite de logiciel/src/main/java/net/frootloop/qa/metrics/Main.java");
    }

    public static ArrayList<ParsedClass> getParsedClassDataOf(String filePath) throws FileNotFoundException {

        // List of classes found within the source file (one file can declare multiple nested classes)
        ArrayList<ParsedClass> classes = new ArrayList<>();

        // Get the data and remove unnecessary
        SourceFileData fileData = JavaSourceFileParser.readSourceFile(filePath);

        // Locate statements only (we don't need to look at nested bodies yet)
        String packageName;
        ArrayList<String> importStatements;

        // Split the code by its nesting levels, i.e. curly backets, and iterate on each level:
        for (String blockOfCode : fileData.data.split("\\{(.|[^.])*\\}")) {


        }
        // Split each nested level of the code into individual statements:

        // Get the package name (from the first level):

        // Get a list of all the import statements (from the first level):

        // Get the main class from the file (from the first level):

        // For every other level, parse its statements and look for nested classes:


        return classes;
    }

    private static ParsedClass parseSourceCode(String blockOfCode){
        if(!blockOfCode.contains("class"))
            return null;
        else {
            String className;
            Visibility visibility;

            // Split the code into statements (lines of code):
            String[] codeStatements = blockOfCode.split(";");

            for (String s : codeStatements) {

                // If the statement is an 'import', add the imported signature to the list:

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

    private static SourceFileData readSourceFile(String path) throws FileNotFoundException {

        SourceFileData fileData = new SourceFileData();
        String lineOfText;
        boolean isMultineComment = false;
        String regexStringDetector = "/[[[:blank:]]*[[\"][^\"]*[\"][^\"]*]*[(\\/\\*)*]/gm";
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                lineOfText = myReader.nextLine();
                fileData.numLines += 1;

                // If the line is a single-line comment (like this one!):
                if(lineOfText.matches("/[[[:blank:]]*[^\"]*[[\"][^\"]*[\"]]*[^\"]*]*" + "\\/{2,}/gm"))
                    fileData.numLinesComments += 1;

                // If the line is just empty:
                else if (lineOfText.matches("/\\A[[:blank:]]*\\Z/gm"))
                    fileData.numLinesEmpty += 1;

                // Only add actual lines of code to the output:
                else fileData.data += lineOfText;
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR. Unable to read file " + path);
            e.printStackTrace();
        };
        fileData.cleanUpData();
        return fileData;
    }
}

