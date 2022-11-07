package net.frootloop.qa;

import net.frootloop.qa.parser.inputhandling.InputHandler;
import net.frootloop.qa.parser.JavaRepositoryParser;
import net.frootloop.qa.parser.JavaSourceFileParser;

import java.nio.file.Path;

public class SoftwareMetricsMain implements InputHandler {

    public static void main(String[] args) {

        RequestType userIntention = InputHandler.promptWelcome();

        if(userIntention == RequestType.PRINT_SOURCE_FILE_CONTENTS) {
            Path sourceFilePath = InputHandler.promptForSourceFilePath();
            JavaSourceFileParser.printCodeOf(sourceFilePath);

        } else if (userIntention == RequestType.ANALYSE_SOURCE_FILE) {
            //Path sourceFilePath = InputHandler.promptForSourceFilePath();
            System.out.println("Sorry about that, this service is not completely implemented yet. Please pick another option.");
            main(args);

        } else if (userIntention == RequestType.ANALYSE_GIT_REPO) {
            Path repositoryPath = InputHandler.promptForRepositoryPath();
            JavaRepositoryParser.analyseRepositoryAt(repositoryPath);
        }
    }
}
