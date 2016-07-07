package br.net.mirante.singular.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.output.FileWriterWithEncoding;

public abstract class PropertiesUtils {
    private PropertiesUtils() {}

    public static Properties propertiesFromMap(Map<String, String> map) {
        Properties props = new Properties();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            props.put(entry.getKey(), entry.getValue());
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

    public static Properties load(URL url, String encoding) throws IOException {
        try (InputStream input = url.openStream()) {
            return load(input, encoding);
        }
    }

    public static Properties load(InputStream input, String encoding) throws IOException {
        try (Reader reader = new InputStreamReader(input, encoding)) {
            Properties props = new Properties();
            props.load(reader);
            return props;
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
