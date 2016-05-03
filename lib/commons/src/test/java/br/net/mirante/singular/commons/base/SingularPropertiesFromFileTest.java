package br.net.mirante.singular.commons.base;

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
    private final String SYSTEM_PROPERTY_NAME = "singular.server.props.server";

    @Before
    public void setUp() throws IOException {
        final File             tempFile = File.createTempFile("mock", "properties");
        final FileOutputStream stream   = new FileOutputStream(tempFile);
        stream.write((MOCK_PROPERTY_KEY + "=" + MOCK_PROPERTY_VALUE).getBytes());
        stream.flush();
        stream.close();
        System.setProperty(SYSTEM_PROPERTY_NAME, tempFile.getPath());
    }

    @Test
    public void test() throws Exception {
        SingularProperties.INSTANCE.reload();
        Assert.assertEquals(MOCK_PROPERTY_VALUE, SingularProperties.INSTANCE.getProperty(MOCK_PROPERTY_KEY));
    }

    @After
    public void closeUp() {
        System.clearProperty(SYSTEM_PROPERTY_NAME);
    }

}