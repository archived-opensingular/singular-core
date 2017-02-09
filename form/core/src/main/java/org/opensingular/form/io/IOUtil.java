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

package org.opensingular.form.io;

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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class IOUtil {

    public static final int BUFFER_2MB = 2 * 1048576;
    public static final int BUFFER_5MB = 5 * 1048576;
    public static final Charset UTF8 = Charset.forName(StandardCharsets.UTF_8.name());

    private IOUtil() {}

    public static InputStream newBuffredInputStream(File f) throws FileNotFoundException {
        return newBuffredInputStream(new FileInputStream(f));
    }

    public static OutputStream newBufferedOutputStream(File f) throws FileNotFoundException {
        return newBufferedOutputStream(new FileOutputStream(f));
    }

    public static OutputStream newBufferedOutputStream(OutputStream out) {
        return new BufferedOutputStream(out, BUFFER_2MB);
    }

    public static InputStream newBuffredInputStream(InputStream in) {
        return new BufferedInputStream(in, BUFFER_2MB);
    }

    public static void writeLines(OutputStream out, String... lines) throws IOException {
        IOUtils.writeLines(Arrays.asList(lines), IOUtils.LINE_SEPARATOR_UNIX, out, UTF8);
    }

    public static void writeLines(File f, String... lines) throws IOException {
        try (OutputStream fos = newBufferedOutputStream(f)) {
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
