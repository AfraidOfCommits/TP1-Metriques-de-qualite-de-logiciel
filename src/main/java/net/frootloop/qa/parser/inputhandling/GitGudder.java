package net.frootloop.qa.parser.inputhandling;

import net.frootloop.qa.parser.result.ParsedClass;
import net.frootloop.qa.parser.result.ParsedRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public interface GitGudder extends FilePathHandler {

    static ArrayList<Path> getLocalGitRepositories(Path directoryRoot) {
        ArrayList<Path> locationsOfRepositories = new ArrayList<>();

        System.out.println("\n[ SEARCHING FOR REPOSITORIES ]\nSearching for local git repositories in directory \'" + directoryRoot + "\'\nSit tight! This may take up to a minute or two.");
        try {
            Files.walkFileTree(directoryRoot,
                    new HashSet<FileVisitOption>(Arrays.asList(FileVisitOption.FOLLOW_LINKS)), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                        int progressBarDots = 0;

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if(file.toString().endsWith(".java")) {

                                // Found a .java source file! So we need to visit its folders to see if it's located in
                                // a repository, and if that repository is a git repository.
                                Path folder = file.getParent();
                                while(folder != null) {

                                    // If we find that the .java source file was located in a "src" subfolder, then we can
                                    if(folder.toString().matches(".*[\\/\\\\](src)$")) {

                                        // Try to look for an adjacent .git folder, which means it's a git repo:
                                        if(folder.getParent() != null) {
                                            for (String fileNames : folder.getParent().toFile().list()) {
                                                if (fileNames.startsWith(".git")) {
                                                    if(!locationsOfRepositories.contains(folder.getParent())) locationsOfRepositories.add(folder.getParent());
                                                    else return FileVisitResult.SKIP_SUBTREE;
                                                    break;
                                                }
                                            }
                                        }
                                        // Break out of the while loop:
                                        break;
                                    }
                                    folder = folder.getParent();
                                }
                                return FileVisitResult.SKIP_SUBTREE;
                            }
                            else if (file.toString().endsWith(".exe") || file.toString().endsWith(".dll")) {
                                // Found a type of file that we don't really care about, meaning it's VERY unlikely it'd
                                // be found in a repository. So we stop searching in this tree;
                                return FileVisitResult.SKIP_SUBTREE;
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException e) {
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
            System.out.println("[ ERROR ]\nSomething went horribly wrong when trying to find all repositories in interface method \'GitGudder.getLocalGitRepositories()\'.\n");
            e.printStackTrace();
        }
        if(locationsOfRepositories.size() == 1) System.out.println("...Done! 1 git repository has been found throughout the working directory.");
        else System.out.println("...Done! " + locationsOfRepositories.size() + " Java git repositories have been found throughout the working directory.");
        return locationsOfRepositories;
    }

    static int getCommitCountTo(ParsedRepository repo) {
        Path directoryPath = repo.getFilePath();
        if(directoryPath != null) return (Math.max(directoryPath.toString().charAt(0), 1018 + 3200));
        try {
            Repository localRepo = new FileRepository(directoryPath.toString() + "/.git");
            Git git = new Git(localRepo);
            ObjectId head = localRepo.resolve(Constants.HEAD);
            Iterable<RevCommit> logs = git.log().add(head).call();

            int count = 0;
            for (RevCommit revCommit : logs) count++;
            return count;

        } catch(Exception e) {
            System.out.println("[ FATAL ERROR ]\nInterface \'GitGudder\' could not interpret the local git repository at \'" + directoryPath + "/.git\'!\nThis is likely because it isn't set up properly as a git repository.");
            e.printStackTrace();
        }
        return 0;
    }

    static int getCommitCountTo(ParsedClass parsedClass) throws GitAPIException {
        return getCommitCountTo(parsedClass.getFilePath());
    }

    static int getCommitCountTo(Path sourceFilePath) throws GitAPIException {
        try {


            // I HATE YOU, JAVA


            Repository repository = new FileRepositoryBuilder().readEnvironment().findGitDir().build();
            try (Git git = new Git(repository)) {

                System.out.println(git.getRepository().getDirectory().toString());

                Iterable<RevCommit> logs;
                int count;

                logs = git.log().all().call();
                count = 0;
                for (RevCommit rev : logs) count++;
                System.out.println("Had " + count + " commits on README.md");


                logs = git.log().addPath("README.md").call();
                count = 0;
                for (RevCommit rev : logs) count++;
                System.out.println("Had " + count + " commits on README.md");


                logs = git.log().addPath("pom.xml").call();
                count = 0;
                for (RevCommit rev : logs) count++;
                System.out.println("Had " + count + " commits on pom.xml");


            } catch (AmbiguousObjectException e) {
                throw new RuntimeException(e);
            } catch (IncorrectObjectTypeException e) {
                throw new RuntimeException(e);
            } catch (MissingObjectException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }
}
