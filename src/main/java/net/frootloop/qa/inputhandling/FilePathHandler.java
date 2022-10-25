package net.frootloop.qa.inputhandling;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public interface FilePathHandler {

    static String getWorkingDirectoryStr() {
        return System.getProperty("user.dir");
    }

    static Path getWorkingDirectory() {
        return Path.of(System.getProperty("user.dir"));
    }

    static Path getWorkingDirectoryRoot() {
        Path workingDir = FilePathHandler.getWorkingDirectory();
        while(workingDir.getParent() != null && workingDir.getParent().getParent() != null)
            workingDir = workingDir.getParent();

        return workingDir;
    }

    static ArrayList<Path> getPathsToFile(String fileName) {
        ArrayList<Path> locationsOfFile = new ArrayList<>();
        Path workingDirectoryRoot = FilePathHandler.getWorkingDirectoryRoot();

        System.out.println("\n[ SEARCHING FOR FILE ]\nSearching for occurences of file \'" + fileName + "\' in directory \'" + workingDirectoryRoot + "\'\nSit tight! This may take up to a minute or two.");
        try {
            Files.walkFileTree(workingDirectoryRoot, new HashSet<FileVisitOption>(Arrays.asList(FileVisitOption.FOLLOW_LINKS)), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                int progressBarDots = 0;

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if(file.getFileName().toString().contains(fileName) && file.toString().endsWith(".java")) locationsOfFile.add(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException {
                    return FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    System.out.print(new String(new char[((progressBarDots++)/1000) % 6]).replace('\0','.') + "\r");
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            System.out.println("[ ERROR ]\nSomething went horribly wrong when trying to find all occurrences of " + fileName + " in interface method \'FilePathHandler.getPathsOfFile()\'.\n");
            e.printStackTrace();
        }
        if(locationsOfFile.size() == 1) System.out.println("...Done! 1 occurrence has been found throughout the working directory.");
        else System.out.println("...Done! " + locationsOfFile.size() + " occurrences have been found throughout the working directory.");
        return locationsOfFile;
    }
}
