package br.net.mirante.singular.commons.base;

import com.google.common.io.Files;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SingularPropertiesFromFileTest {

    private final String MOCK_PROPERTY_KEY    = "mock.property";
    private final String MOCK_PROPERTY_VALUE  = "Mock Disck Property";
    private File tempDir;
    private File tempFile;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDir();
        File tempConf = new File(tempDir, "conf");
        tempConf.mkdir();
        tempFile = new File(tempConf, "singular.properties");

        final FileOutputStream stream = new FileOutputStream(tempFile);
        stream.write((MOCK_PROPERTY_KEY + "=" + MOCK_PROPERTY_VALUE).getBytes());
        stream.flush();
        stream.close();
        System.setProperty(SingularProperties.SYSTEM_PROPERTY_SINGULAR_SERVER_HOME, tempDir.toString());
    }

    @After
    public void closeUp() {
        tempFile.delete();
        tempDir.delete();
        System.clearProperty(SingularProperties.SYSTEM_PROPERTY_SINGULAR_SERVER_HOME);
    }

    @Test
    public void test() throws Exception {
        SingularProperties.get().reload();
        Assert.assertEquals(MOCK_PROPERTY_VALUE, SingularProperties.get().getProperty(MOCK_PROPERTY_KEY));
    }
}