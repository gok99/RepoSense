package reposense.git;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.template.GitTestTemplate;

class GitRemoteTest extends GitTestTemplate {

    @Test
    void getRemotesTest() {
        Map<String, String> remotes = GitRemote.getRemotes(config.getRepoRoot());
        Assertions.assertEquals(remotes.entrySet().size(), 2);
        Assertions.assertTrue(remotes.containsKey(GitRemote.DEFAULT_FETCH_REMOTE));
        Assertions.assertTrue(remotes.containsKey(GitRemote.DEFAULT_PUSH_REMOTE));
        Assertions.assertTrue(remotes.values().stream().allMatch(s ->
                s.equals(TEST_REPO_GIT_LOCATION)));
    }
}
