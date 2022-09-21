package net.frootloop.qa.metrics.parser;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class JavaSourceFileParser {

    public static String getFileDataOf(String path) {

        String fileData = "";
        try {

            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                fileData = myReader.nextLine();
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("ERROR. Unable to read file " + path);
            e.printStackTrace();
        };
        return fileData;
    }
}

