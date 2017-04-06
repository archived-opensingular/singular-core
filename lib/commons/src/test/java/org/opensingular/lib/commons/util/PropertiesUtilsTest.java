package org.opensingular.lib.commons.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtilsTest {

    protected TempFileProvider tmpProvider;

    @Before
    public void createTmpProvider() {
        tmpProvider = TempFileProvider.createForUseInTryClause(this);
    }

    @After
    public void cleanTmpProvider() {
        tmpProvider.deleteOrException();
    }

    @Test
    public void propertiesFromMapTest(){
        Properties properties = getPropertiesTest();
        Assert.assertEquals("VALUE", properties.getProperty("KEY"));
    }

    private Properties getPropertiesTest() {
        Map<String, String> map = new HashMap<>();
        map.put("KEY", "VALUE");
        return PropertiesUtils.propertiesFromMap(map);
    }

    @Test
    public void storeWithOutputStreamAndLoadWithFileTest() throws IOException {
        Properties properties = getPropertiesTest();

        File arquivoTemporario = tmpProvider.createTempFile(".txt");

        try(FileOutputStream outputStream = new FileOutputStream(arquivoTemporario)) {

            PropertiesUtils.store(null, outputStream);
            Properties loadedFile = PropertiesUtils.load(arquivoTemporario, "UTF-8");
            Assert.assertEquals(0, loadedFile.size());

            PropertiesUtils.store(properties, outputStream);
            loadedFile = PropertiesUtils.load(arquivoTemporario, "UTF-8");
            Assert.assertEquals(1, loadedFile.size());
            Assert.assertEquals("VALUE", loadedFile.getProperty("KEY"));
        }
    }

    @Test
    public void loadUsingReaderTest() throws IOException {
        Properties properties = getPropertiesTest();

        File arquivoTemporario = tmpProvider.createTempFile(".txt");

        try(FileOutputStream outputStream = new FileOutputStream(arquivoTemporario)) {
            PropertiesUtils.store(properties, outputStream);
        }

        try(FileReader reader = new FileReader(arquivoTemporario)) {
            Properties loadedFile = PropertiesUtils.load(reader);
            Assert.assertEquals("VALUE", loadedFile.getProperty("KEY"));
        }
    }

    @Test
    public void copyTest(){
        Properties properties = getPropertiesTest();

        Properties copy = PropertiesUtils.copy(properties);
        Assert.assertEquals(properties, copy);
        Assert.assertFalse(properties == copy);

        PropertiesUtils.copyTo(null, copy);
    }
}
