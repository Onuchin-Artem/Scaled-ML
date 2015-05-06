package io.scaledml.core.inputformats;

import io.scaledml.core.SparseItem;
import io.scaledml.ftrl.featuresprocessors.FeaturesProcessor;
import io.scaledml.core.util.LineBytesBuffer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ilya Smagin ilya-sm@yandex-team.ru on 4/5/15.
 */
public class CSVFormatTest {

    @Test
    public void testParse() throws Exception {
        String line1 = "0,e6c5b5cd,68fd1e64,0b153874,c92f3b61,8e407662,2b53e5fb,1f6f0bb6,21ddcdc9,4f94c62a,7d1526c6," +
                "606b0dda,f0cf0024,b04e4670,454bf5f0,60f6221e,fe6b92e5,3a171ecb,25c83c98,07c540c4,41274cd7,623049e6," +
                "5840adea,922afcc0,731c3655,2f532987,0850bcd9,f9b4759b,d7020589,6f67f7e5,4f1b46f3,b28479f6,e8b83407," +
                "d7497e30,5fca948f,43f13e8b,,a73ee510,0.5,1.0,4,1,4.0,2,2,44.0,1,204.0,2,,0,110.0,3.0,4,8,440.0,46.0," +
                "44.0,1,44,102";

        InputFormat format = new CSVFormat()
                .featruresProcessor(new FeaturesProcessor())
                .csvMask(new ColumnsMask("lc[37]n"))
                .csvDelimiter(',');
        LineBytesBuffer line = new LineBytesBuffer(line1);
        SparseItem item = new SparseItem();
        format.parse(line, item, 0);
        assertEquals(0., item.label(), 0.000001);
        assertEquals(58, item.indexes().stream().distinct().count());
    }
}
