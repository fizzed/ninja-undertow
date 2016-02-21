
import com.fizzed.blaze.Contexts;
import static com.fizzed.blaze.Systems.exec;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;

public class blaze {
    private final Logger log = Contexts.logger();
    private final Path ninjaRepoDir = Contexts.withBaseDir("../ninja-upstream").normalize();
    private final String ninjaRepoUri = "https://github.com/ninjaframework/ninja.git";

    public void benchmark() {
        exec("mvn", "test-compile").run();
        exec("mvn", "exec:exec", "-Dexec.executable=java", "-Dexec.classpathScope=test", "-Dexec.args=-cp %classpath ninja.undertow.Benchmarker").run();
    }
    
    public void cloneOrRebaseNinjaRepo() throws Exception {
        if (Files.exists(ninjaRepoDir)) {
            rebaseNinjaRepo();
        } else {
            cloneNinjaRepo();
        }
    }
    
    public void rebaseNinjaRepo() throws Exception {
        log.info("Rebasing {} to {}", ninjaRepoUri, ninjaRepoDir);
        try (Repository ninjaRepo = new FileRepositoryBuilder()
            .setGitDir(ninjaRepoDir.resolve(".git").toFile())
            .build()) {
            try (Git git = new Git(ninjaRepo)) {
                //git.pull().setRemoteBranchName("refs/remotes/origin/develop").setRebase(true).setProgressMonitor(new TextProgressMonitor()).call();
                git.rebase().setUpstream("develop").setProgressMonitor(new TextProgressMonitor()).call();
                System.out.println("!");
            }
        }
    }
    
    public void cloneNinjaRepo() throws Exception {
        log.info("Cloning {} to {}", ninjaRepoUri, ninjaRepoDir);
        try (Git result = Git.cloneRepository()
                .setDirectory(ninjaRepoDir.toFile())
                .setURI("https://github.com/ninjaframework/ninja.git")
                .setBare(false)
                .setNoCheckout(false)
                .setProgressMonitor(new TextProgressMonitor() {
                    @Override
                    public void onUpdate(String taskName, int cmp, int totalWork, int pcnt) {
                        System.out.print(".");
                    }
                })
                .call()) {
            System.out.println("!");
        }
    }

}
