package br.net.mirante.singular.form.io;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class IOUtil {

    public static final int BUFFER_2MB = 2 * 1048576;
    public static final int BUFFER_5MB = 5 * 1048576;
    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static InputStream newBuffredInputStream(File f) throws FileNotFoundException {
        return newBuffredInputStream(new FileInputStream(f));
    }

    public static OutputStream newBuffredOutputStream(File f) throws FileNotFoundException {
        return newBuffredOutputStream(new FileOutputStream(f));
    }

    public static OutputStream newBuffredOutputStream(OutputStream out) throws FileNotFoundException {
        return new BufferedOutputStream(out, BUFFER_2MB);
    }

    public static InputStream newBuffredInputStream(InputStream in) {
        return new BufferedInputStream(in, BUFFER_2MB);
    }

    public static void writeLines(OutputStream out, String... lines) throws IOException {
        IOUtils.writeLines(Arrays.asList(lines), IOUtils.LINE_SEPARATOR_UNIX, out, UTF8);
    }

    public static void writeLines(File f, String... lines) throws IOException {
        try (OutputStream fos = newBuffredOutputStream(f)) {
            writeLines(fos, lines);
        }
    }

    public static List<String> readLines(InputStream in) throws IOException {
        return IOUtils.readLines(in, UTF8);
    }

    public static List<String> readLines(File f) throws IOException {
        try (InputStream in = newBuffredInputStream(f)) {
            return readLines(in);
        }
    }
}
