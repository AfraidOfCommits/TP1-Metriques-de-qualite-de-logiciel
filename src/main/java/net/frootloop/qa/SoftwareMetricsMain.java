package net.frootloop.qa;

import net.frootloop.qa.inputhandling.InputHandler;
import net.frootloop.qa.parser.JavaRepositoryParser;

import java.nio.file.Path;

public class SoftwareMetricsMain implements InputHandler {

    public static void main(String[] args) {

        RequestType userIntention = InputHandler.promptWelcome();

        if(userIntention == RequestType.PRINT_SOURCE_FILE_CONTENTS) {
            Path sourceFilePath = InputHandler.promptForSourceFilePath();

        } else if (userIntention == RequestType.ANALYSE_SOURCE_FILE) {
            Path sourceFilePath = InputHandler.promptForSourceFilePath();

        } else if (userIntention == RequestType.ANALYSE_GIT_REPO) {
            Path repositoryPath = InputHandler.promptForRepositoryPath();
            JavaRepositoryParser.analyseRepositoryAt(repositoryPath);
        }
    }
}
