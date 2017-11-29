/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.commons.base;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.lib.commons.util.PropertiesUtils;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Daniel C. Bordin
 * @since 2017-11-03
 */
public class PropertyMapTest {

    @Test
    public void simpleUse() {
        PropertyMap map = new PropertyMap();

        assertKeyDoesNotExist(map, "any");

        map.add("any", "x");
        assertKey(map, "any", "x");

        assertThatThrownBy(() -> map.add("any", null)).isInstanceOf(SingularPropertyException.class)
                .hasMessageContaining("is already definied");

        assertKey(map, "any", "x");
    }

    @Test
    public void emptyStringToNull() {
        PropertyMap map = new PropertyMap();
        map.add("toNull", " ");
        assertKey(map, "toNull", null);
    }

    @Test
    public void trimValue() {
        PropertyMap map = new PropertyMap();
        map.add("trim", " t ");
        assertKey(map, "trim", "t");
    }

    @Test
    public void nullValues() {
        PropertyMap map = new PropertyMap();
        map.add("any", null);
        assertKey(map, "any", null);
    }

    @Test
    public void frozen() {
        PropertyMap map = new PropertyMap();
        map.frozen();
        assertThatThrownBy(() -> map.add("other", "w")).isInstanceOf(SingularPropertyException.class)
                .hasMessageContaining("frozen");
        assertKeyDoesNotExist(map, "other");
    }

    @Test
    public void chainedMaps() {
        PropertyMap map = new PropertyMap();
        map.add("key1", "1");
        map.add("key2", "2");
        map.add("key3", "3");

        PropertyMap map2 = new PropertyMap(map);
        map2.add("key1", "11");
        map2.add("key3", null);
        map2.add("key4", "4");

        assertKey(map, "key1", "1");
        assertKey(map, "key2", "2");
        assertKey(map, "key3", "3");
        assertKeyDoesNotExist(map, "key4");

        assertKey(map2, "key1", "11");
        assertKey(map2, "key2", "2");
        assertKey(map2, "key3", null);
        assertKey(map2, "key4", "4");
        assertKeyDoesNotExist(map, "key5");

        String lineSeparator = System.getProperty("line.separator");
        String expected =
                "#" + lineSeparator + "#source 'unkown source'" + lineSeparator + "key1=11" + lineSeparator + "key2=2" +
                        lineSeparator + "key3=" + lineSeparator + "key4=4" + lineSeparator;
        StringBuilder sb = new StringBuilder();
        map2.debugContent(sb);
        assertEquals(expected, sb.toString());
    }

    @Test
    public void consolidateAndFrozen() {
        PropertyMap map = new PropertyMap();
        map.add("key1", "1");
        map.add("key2", "2");
        map.add("key3", "3");

        PropertyMap map2 = new PropertyMap(map);
        map2.add("key1", "11");
        map2.add("key3", null);
        map2.add("key4", "4");
        assertEquals(map, map2.getParent());

        PropertyMap map3 = map2.consolidateAndFrozen();
        assertKey(map3, "key1", "11");
        assertKey(map3, "key2", "2");
        assertKey(map3, "key3", null);
        assertKey(map3, "key4", "4");
        assertEquals(4, map3.getSize());
        assertNull(map3.getParent());

        assertThatThrownBy(() -> map3.add("other", "w")).isInstanceOf(SingularPropertyException.class)
                .hasMessageContaining("frozen");
    }

    @Test
    public void loadFromClassPath() {
        PropertyMap map = new PropertyMap();
        map.readAllPropertiesFileFromClassPath("singular.properties");

        PropertySource<?> source = map.getEntry("mock.property").getSource();
        assertTrue(source.get() instanceof URL);
        Assertions.assertThat(source.getDescription()).contains("singular.properties");

        assertKey(map, "mock.property", "Mock Property", source);
        assertKey(map, "mock.false", "false", source);
        assertKey(map, "mock.true", "true", source);
        assertEquals(3, map.getSize());

        assertThatThrownBy(() -> map.add("mock.true", "2")).isInstanceOf(SingularPropertyException.class)
                .hasMessageContaining("is already definied").hasMessageContaining("singular.properties");
    }

    @Test
    public void loadNoExistentResourceFromClassPath() {
        PropertyMap map = new PropertyMap();
        map.readAllPropertiesFileFromClassPath("noExistentResoruce");
        assertEquals(0, map.getSize());
    }

    @Test
    public void loadFromFile() {
        TempFileProvider.create(this, tmpProvider -> {
            File f1 = tmpProvider.createTempFile(".properties");
            Properties props = new Properties();
            props.put("key1", "1");
            props.put("key2", "2");
            PropertiesUtils.store(props, f1, "UTF-8");

            File f2 = tmpProvider.createTempFile(".properties");
            props = new Properties();
            props.put("key3", "3");
            PropertiesUtils.store(props, f2, "UTF-8");

            PropertyMap map = new PropertyMap();
            map.readProperties(f1);
            map.readProperties(f2);

            assertKey(map, "key1", "1", PropertySource.of(f1));
            assertKey(map, "key2", "2", PropertySource.of(f1));
            assertKey(map, "key3", "3", PropertySource.of(f2));
            assertEquals(3, map.getSize());
        });
    }

    @Test
    public void loadNoExistentFile() {
        PropertyMap map = new PropertyMap();
        map.readProperties(new File("noExistentFile.properties"));
        assertEquals(0, map.getSize());
    }

    private void assertKeyDoesNotExist(PropertyMap map, String key) {
        assertNull(map.getEntry(key));
        assertNull(map.getValue(key));
    }

    private void assertKey(PropertyMap map, String key, String expectedValue) {
        assertKey(map, key, expectedValue, PropertySource.UNKNOWN);
    }

    private void assertKey(PropertyMap map, String key, String expectedValue, PropertySource<?> expectedSource) {
        assertEquals(map.getValue(key), expectedValue);
        PropertyEntry entry = map.getEntry(key);
        assertNotNull(entry);
        assertEquals(key, entry.getKey());
        assertEquals(expectedValue, entry.getValue());
        assertEquals(expectedSource, entry.getSource());
    }
}