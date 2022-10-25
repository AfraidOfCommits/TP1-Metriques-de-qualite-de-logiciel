package net.frootloop.qa.inputhandling;

import net.frootloop.qa.parser.result.ParsedRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public interface GitGudder extends FilePathHandler {

    static ArrayList<Path> getLocalGitRepositories() {
        ArrayList<Path> locationsOfRepositories = new ArrayList<>();
        Path directoryRoot = FilePathHandler.getSystemRoot();

        System.out.println("\n[ SEARCHING FOR REPOSITORIES ]\nSearching for local git repositories in directory \'" + directoryRoot + "\'\nSit tight! This may take up to a minute or two.");
        try {
            Files.walkFileTree(directoryRoot,
                    new HashSet<FileVisitOption>(Arrays.asList(FileVisitOption.FOLLOW_LINKS)), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                        int progressBarDots = 0;

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException e) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            System.out.print(new String(new char[((progressBarDots++)/1000) % 6]).replace('\0','.') + "\r");

                            if(dir.toString().matches(".*[\\/\\\\](\\.git)$")) {
                                for (String fileNames : dir.getParent().toFile().list()) {
                                    if (fileNames.equals("src")) {
                                        locationsOfRepositories.add(dir.getParent());
                                        break;
                                    }
                                }
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
        }
        catch (IOException e) {
            System.out.println("[ ERROR ]\nSomething went horribly wrong when trying to find all repositories in interface method \'GitGudder.getLocalGitRepositories()\'.\n");
            e.printStackTrace();
        }
        if(locationsOfRepositories.size() == 1) System.out.println("...Done! 1 git repository has been found throughout the working directory.");
        else System.out.println("...Done! " + locationsOfRepositories.size() + " git repositories have been found throughout the working directory.");
        return locationsOfRepositories;
    }

    static int getCommitCountTo(ParsedRepository repo) {
        return getCommitCountTo(repo.getFilePath());
    }

    static int getCommitCountTo(Path directoryPath) {

        try {
            Repository localRepo = new FileRepository(directoryPath.toString() + "/.git");
            Git git = new Git(localRepo);
            ObjectId head = localRepo.resolve(Constants.HEAD);
            Iterable<RevCommit> logs = git.log().add(head).call();

            int count = 0;
            for (RevCommit revCommit : logs) count++;
            return count;

        } catch(Exception e) {
            System.out.println("[ FATAL ERROR ]\nInterface \'GitGudder\' could not interpret the local git repository at \'" + directoryPath + "/.git\'!\n");
            e.printStackTrace();
        }
        return 0;
    }
}
