package net.frootloop.qa.parser.inputhandling;

import net.frootloop.qa.parser.result.ParsedRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.nio.file.Path;
import java.util.ArrayList;

public interface GitGudder extends FilePathParser {

    static ArrayList<Path> getAllGitRepositoryDirectories() {
        //return getCommitCountTo(repo.getFilePath());

        return null;
    }

    static int getCommitCountTo(ParsedRepository repo) {
        return getCommitCountTo(repo.getFilePath());
    }

    static int getCommitCountTo(Path path) {
        return getCommitCountTo(path.toString());
    }

    static int getCommitCountTo(String directoryPath) {

        try {
            Repository localRepo = new FileRepository(directoryPath + "/.git");
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
