package reposense.model;

import org.junit.Assert;
import org.junit.Test;
import reposense.parser.InvalidLocationException;
import reposense.util.AssertUtil;

public class RepoLocationTest {

    private static final String LOCAL_REPO_LOCATION_VALID_WITHOUT_DOT_GIT_ONE = "path/to/repo/";
    private static final String LOCAL_REPO_LOCATION_VALID_WITHOUT_DOT_GIT_TWO = "../path/to/repo";
    private static final String LOCAL_REPO_LOCATION_VALID_WITHOUT_DOT_GIT_THREE = "file://path/to/repo";
    private static final String LOCAL_REPO_LOCATION_VALID_WITH_DOT_GIT_ONE = "path/to/repo/.git";
    private static final String LOCAL_REPO_LOCATION_VALID_WITH_DOT_GIT_TWO = "file://path/to/repo/.git";

    private static final String LOCAL_REPO_LOCATION_WINDOWS_VALID_WITHOUT_DOT_GIT_ONE = "path\\to\\repo\\";
    private static final String LOCAL_REPO_LOCATION_WINDOWS_VALID_WITHOUT_DOT_GIT_TWO = "..\\path\\to\\repo";
    private static final String LOCAL_REPO_LOCATION_WINDOWS_VALID_WITHOUT_DOT_GIT_THREE = "file://path\\to\\repo";
    private static final String LOCAL_REPO_LOCATION_WINDOWS_VALID_WITH_DOT_GIT_ONE = "path\\to\\repo\\.git";
    private static final String LOCAL_REPO_LOCATION_WINDOWS_VALID_WITH_DOT_GIT_TWO = "file://path\\to\\repo\\.git";
    private static final String LOCAL_REPO_LOCATION_WINDOWS_VALID_MIXED = "..\\path/to\\repo";

    private static final String EXPECTED_REPO_NAME = "repo";
    private static final String EXPECTED_ORGANIZATION = "path-to";

    @Test
    public void repoLocationParser_parseEmptyString_success() throws Exception {
        RepoLocation repoLocation = new RepoLocation("");
    }

    @Test
    public void repoLocationParser_parseLocalRepoLocation_success() throws Exception {
        // local paths not containing ".git" should be valid
        assertValidLocation(LOCAL_REPO_LOCATION_VALID_WITHOUT_DOT_GIT_ONE,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);
        // relative pathing should be considered part of the 'organization' for differentiation
        assertValidLocation(LOCAL_REPO_LOCATION_VALID_WITHOUT_DOT_GIT_TWO,
                EXPECTED_REPO_NAME, "..-" + EXPECTED_ORGANIZATION);
        // local file paths designated by the URL file:// ...  should be valid
        assertValidLocation(LOCAL_REPO_LOCATION_VALID_WITHOUT_DOT_GIT_THREE,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);

        // local paths containing ".git" should also be valid
        assertValidLocation(LOCAL_REPO_LOCATION_VALID_WITH_DOT_GIT_ONE,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);
        assertValidLocation(LOCAL_REPO_LOCATION_VALID_WITH_DOT_GIT_TWO,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);

        // repeated tests but with windows file separators
        assertValidLocation(LOCAL_REPO_LOCATION_WINDOWS_VALID_WITHOUT_DOT_GIT_ONE,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);
        assertValidLocation(LOCAL_REPO_LOCATION_WINDOWS_VALID_WITHOUT_DOT_GIT_TWO,
                EXPECTED_REPO_NAME, "..-" + EXPECTED_ORGANIZATION);
        assertValidLocation(LOCAL_REPO_LOCATION_WINDOWS_VALID_WITHOUT_DOT_GIT_THREE,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);
        assertValidLocation(LOCAL_REPO_LOCATION_WINDOWS_VALID_WITH_DOT_GIT_ONE,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);
        assertValidLocation(LOCAL_REPO_LOCATION_WINDOWS_VALID_WITH_DOT_GIT_TWO,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);

        assertValidLocation(LOCAL_REPO_LOCATION_WINDOWS_VALID_MIXED,
                EXPECTED_REPO_NAME, "..-" + EXPECTED_ORGANIZATION);
    }

    @Test
    public void repoLocation_validRemoteRepoUrl_success() throws Exception {
        // valid url without specifying branch
        assertValidLocation("https://github.com/reposense/testrepo-Beta.git",
                "testrepo-Beta", "reposense");
        assertValidLocation("https://github.com/reposense/testrepo-Delta.git",
                "testrepo-Delta", "reposense");
        assertValidLocation("https://gitlab.com/reposense/RepoSense.git",
                "RepoSense", "reposense");
        assertValidLocation("https://github.com/reposense.git",
                "reposense", "");
        // valid url to parse for obtaining repo and organization, just not a valid git clone target
        assertValidLocation("https://github.com/reposense/.git",
                "reposense", "");

        // treated as valid but will be caught when git clone fails
        assertValidLocation("https://github.com/reposense/testrepo-Beta/tree/add-config-json",
                "add-config-json", "reposense-testrepo-Beta-tree");
        assertValidLocation("https://github.com/reposense/testrepo-Beta.git/tree/add-config-json",
                "add-config-json", "reposense-testrepo-Beta.git-tree");

        // URLs without ".git" should be accepted as git clone works even without it
        assertValidLocation("https://github.com/reposense",
                "reposense", "");
        assertValidLocation("https://github.com/reposense/RepoSense",
                "RepoSense", "reposense");

        // Test against other types of URL protocols that are valid for git clone
        assertValidLocation("ssh://[user@]host.xz[:port]/path/to/repo.git/",
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);
        assertValidLocation("git://host.xz[:port]/path/to/repo.git",
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);
        assertValidLocation("https://host.xz[:port]/path/to/repo.git",
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);

        // Test against the conventional ssh protocol used for GitHub, e.g. git@github.com:reposense/RepoSense.git
        assertValidLocation("[user@]host.xz:path/to/repo.git/",
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION);
        assertValidLocation("git@github.com:reposense/RepoSense.git",
                "RepoSense", "reposense");
        assertValidLocation("user@host.xz:RepoSense.git",
                "RepoSense", "");
    }

    /**
     * Compares the information parsed by the RepoLocation model with the expected information
     */
    public void assertValidLocation(String rawLocation, String expectedRepoName,
            String expectedOrganization) throws Exception {
        RepoLocation repoLocation = new RepoLocation(rawLocation);
        Assert.assertEquals(expectedRepoName, repoLocation.getRepoName());
        Assert.assertEquals(expectedOrganization, repoLocation.getOrganization());
    }

    private void assertInvalidLocation(String rawLocation) {
        AssertUtil.assertThrows(InvalidLocationException.class, () -> new RepoLocation(rawLocation));
    }
}
