package org.opensingular.internal.lib.commons.xml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class UTF8PrintWriterFactory implements PrintWriterFactory {

    @Override
    public Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public PrintWriter newPrintWriter(OutputStream outputStream) {
        return new PrintWriter(new OutputStreamWriter(outputStream, getCharset()));
    }
}
