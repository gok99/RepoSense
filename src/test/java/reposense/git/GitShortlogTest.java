package reposense.git;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.model.Author;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class GitShortlogTest extends GitTestTemplate {

    @Test
    public void getAuthors_validRepoNoDateRange_success() {
        List<Author> expectedAuthorList = new ArrayList<>();
        expectedAuthorList.add(new Author("Eugene Peh"));
        expectedAuthorList.add(new Author("FH-30"));
        expectedAuthorList.add(new Author("WANG CHAO"));
        expectedAuthorList.add(new Author("chan-j-d"));
        expectedAuthorList.add(new Author("eugenepeh"));
        expectedAuthorList.add(new Author("fakeAuthor"));
        expectedAuthorList.add(new Author("harryggg"));

        List<Author> actualAuthorList = GitShortlog.getAuthors(config);

        Assertions.assertEquals(expectedAuthorList.size(), actualAuthorList.size());
        Assertions.assertEquals(expectedAuthorList, actualAuthorList);
    }

    @Test
    public void getAuthors_validRepoDateRange_success() {
        List<Author> expectedAuthorList = new ArrayList<>();

        expectedAuthorList.add(new Author("eugenepeh"));
        config.setSinceDate(TestUtil.getSinceDate(2018, Month.MAY.getValue(), 5));
        config.setUntilDate(TestUtil.getUntilDate(2018, Month.MAY.getValue(), 10));

        List<Author> actualAuthorList = GitShortlog.getAuthors(config);

        Assertions.assertEquals(expectedAuthorList.size(), actualAuthorList.size());
        Assertions.assertEquals(expectedAuthorList, actualAuthorList);
    }

    @Test
    public void getAuthors_validRepoDateOutOfRange_success() {
        config.setSinceDate(TestUtil.getSinceDate(2018, Month.JUNE.getValue(), 1));
        config.setUntilDate(TestUtil.getUntilDate(2018, Month.JUNE.getValue(), 20));

        List<Author> actualAuthorList = GitShortlog.getAuthors(config);

        Assertions.assertTrue(actualAuthorList.isEmpty());
    }
}
