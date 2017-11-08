/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.commons.util;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.junit.AbstractTestTempFileSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtilsTest extends AbstractTestTempFileSupport {


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

        File arquivoTemporario = getTempFileProvider().createTempFile(".txt");

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

        File arquivoTemporario = getTempFileProvider().createTempFile(".txt");

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
