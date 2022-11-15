package net.frootloop.qa;

import net.frootloop.qa.parser.JavaRepositoryParser;
import net.frootloop.qa.parser.JavaSourceFileParser;
import net.frootloop.qa.parser.util.GitGudder;
import net.frootloop.qa.parser.util.InputHandler;

import java.nio.file.Path;

public class SoftwareMetricsMain implements InputHandler {


    public static void main(String[] args) {

        RequestType userIntention = InputHandler.promptWelcome();

        if(userIntention == RequestType.PRINT_SOURCE_FILE_CONTENTS) {
            Path sourceFilePath = InputHandler.promptForSourceFilePath();
            JavaSourceFileParser.printCodeOf(sourceFilePath);

        } else if (userIntention == RequestType.ANALYSE_SOURCE_FILE) {
            Path sourceFilePath = InputHandler.promptForSourceFilePath();
            JavaSourceFileParser.printCodeOf(sourceFilePath);
            JavaSourceFileParser.analyseFileAt(sourceFilePath);

        } else if (userIntention == RequestType.ANALYSE_GIT_REPO) {
            Path repositoryPath = InputHandler.promptForRepositoryPath();
            JavaRepositoryParser.analyseRepositoryAt(repositoryPath);

        } else if (userIntention == RequestType.PRINT_AMOUNT_COMMITS) {
            Path repositoryPath = InputHandler.promptForRepositoryPath();
            Path sourceFilePath = InputHandler.promptForFileInRepository(repositoryPath);
            System.out.println("\n[ RESULT ]\nNumber of commits made to source file '" + sourceFilePath.toFile().getName() + "' : " + GitGudder.getCommitCountTo(repositoryPath, sourceFilePath));
        }
    }
}
