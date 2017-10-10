package org.opensingular.lib.commons;

import com.google.common.base.Joiner;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.io.TempFileInputStream;
import org.opensingular.lib.commons.util.Loggable;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;

public class TempFileInputStreamTest implements Loggable {

    @Test
    public void test() throws Exception {
        File f = File.createTempFile("test", "supernada");
        try (FileWriter fw = new FileWriter(f)){
            IOUtils.write("teste 123", fw);
        }
        try (TempFileInputStream ts = new TempFileInputStream(f)){
            getLogger().info(Joiner.on("\n").join(IOUtils.readLines(ts, Charset.defaultCharset())));
        }
        Assert.assertFalse(f.exists());
    }
}
