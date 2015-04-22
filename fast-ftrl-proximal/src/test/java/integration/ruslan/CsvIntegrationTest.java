package integration.ruslan;


import integration.BaseIntegrationTest;
import io.scaledml.ftrl.Main;
import io.scaledml.ftrl.options.FtrlOptionsObject;
import io.scaledml.ftrl.options.InputFormatType;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class CsvIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testRunFtrlProximal() throws Exception {
        Main.runFtrlProximal(new FtrlOptionsObject()
                .finalRegressor(tempDirectory + "/model")
                .threads(3)
                .data(getClass().getResource("/ruslan-train-small.csv").getPath())
                .format(InputFormatType.csv)
                .skipFirst(true));
        syncFS();
        double logLoss = Main.runFtrlProximal(new FtrlOptionsObject()
                .initialRegressor(tempDirectory + "/model")
                .testOnly(true)
                .predictions(tempDirectory + "/predictions")
                .data(getClass().getResource("/ruslan-test-small.csv").getPath())
                .format(InputFormatType.csv)
                .skipFirst(true));
        syncFS();
        assertEquals(0.518497737424817, logLoss, 0.000001);

        double[] predictions = Files.readAllLines(Paths.get(tempDirectory.toString(), "predictions"))
                .stream().mapToDouble(Double::parseDouble).toArray();
        int predictionsNum = predictions.length;
        assertEquals(predictionsNum, 100);
    }
}