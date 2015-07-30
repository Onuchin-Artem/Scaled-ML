package integration;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;


public class BaseIntegrationTest {
    protected Path tempDirectory;

    @Before
    public void setup() throws IOException {
        tempDirectory = Files.createTempDirectory("csv-ftrl-test-" + getClass().getSimpleName());
    }

    protected void syncFS() throws Exception {
        try (RandomAccessFile rws = new RandomAccessFile(tempDirectory + "/sync", "rws")) {
            Thread.sleep(500);
            rws.getFD().sync();
        }
    }

    @After
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(tempDirectory.toFile());
    }

    protected String resourcePath(String name) {
        return getClass().getResource(name).getPath();
    }
}
