/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.lib.commons.util;

import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public abstract class PropertiesUtils {
    private PropertiesUtils() {}

    public static Properties propertiesFromMap(Map<String, String> map) {
        Properties props = new Properties();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            props.setProperty(entry.getKey(), entry.getValue());
        }
        return props;
    }

    public static Properties properties(String key, String value) {
        Properties props = new Properties();
        props.put(key, value);
        return props;
    }

    public static void store(Properties props, OutputStream output) throws IOException {
        if (props != null)
            props.store(output, "");
    }
    public static void store(Properties props, Writer writer) throws IOException {
        if (props != null)
            props.store(writer, "");
    }

    public static void store(Properties props, File file, String encoding) throws IOException {
        try (Writer writer = new FileWriterWithEncoding(file, encoding)) {
            store(props, writer);
        }
    }

    public static Properties load(File file, String encoding) throws IOException {
        try (InputStream input = new FileInputStream(file)) {
            return load(input, encoding);
        }
    }

    /** Lê o arquivo de properties na URL informada usando o enconding default. */
    public static Properties load(URL url) throws IOException {
        return load(url, StandardCharsets.UTF_8.name());
    }

    /** Lê o arquivo de properties na URL informada. */
    public static Properties load(URL url, String encoding) throws IOException {
        try (InputStream input = url.openStream()) {
            return load(input, encoding);
        }
    }

    public static Properties load(InputStream input, String encoding) throws IOException {
        try (Reader reader = new InputStreamReader(input, encoding)) {
            return load(reader);
        }
    }

    public static Properties load(Reader reader) throws IOException {
        Properties props = new Properties();
        props.load(reader);
        return props;
    }

    public static Properties copy(Properties source) {
        Properties copy = new Properties();
        copyTo(source, copy);
        return copy;
    }
    public static void copyTo(Properties source, Properties destination) {
        if (source != null) {
            for (String propertyName : source.stringPropertyNames())
                destination.put(propertyName, source.getProperty(propertyName));
        }
    }
}
