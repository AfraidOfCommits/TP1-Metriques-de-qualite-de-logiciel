package net.frootloop.qa.metrics.parser;

import net.frootloop.qa.metrics.parser.result.ParsedClass;

import java.util.ArrayList;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class JavaSourceFileParser {

    public static void test() throws FileNotFoundException {
        JavaSourceFileParser.getParsedClassDataOf("/C:/Users/Alex/Desktop/IFT3913 - Qualité Logiciel/TP1/TP1 Metriques de qualite de logiciel/src/main/java/net/frootloop/qa/metrics/Main.java");
    }

    private static ArrayList<ParsedClass> getParsedClassDataOf(String filePath) throws FileNotFoundException {

        // List of classes found within the source file (one file can declare multiple nested classes)
        ArrayList<ParsedClass> classes = new ArrayList<>();

        // Get the data and remove unnecessary
        SourceFileData fileData = JavaSourceFileParser.getRawDataOf(filePath);

        System.out.println(fileData.data);

        return classes;
    }

    private static SourceFileData getRawDataOf(String path) throws FileNotFoundException {

        SourceFileData fileData = new SourceFileData();
        String lineOfCode;
        boolean isMultineComment = false;
        String regexStringDetector = "/[[[:blank:]]*[[\"][^\"]*[\"][^\"]*]*[(\\/\\*)*]/gm";
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                lineOfCode = myReader.nextLine();
                fileData.numLines += 1;

                // If the line is a single-line comment (like this one!):
                if(lineOfCode.matches("/[[[:blank:]]*[^\"]*[[\"][^\"]*[\"]]*[^\"]*]*" + "\\/{2,}/gm"))
                    fileData.numLinesComments += 1;

                // If the line is just empty:
                else if (lineOfCode.matches("/\\A[[:blank:]]*\\Z/gm"))
                    fileData.numLinesEmpty += 1;

                // Only add actual lines of code to the output:
                else fileData.data += lineOfCode;
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

