package org.opensingular.internal.lib.commons.xml;

import org.opensingular.lib.commons.io.StringPrintWriter;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * Factory de printWriters com encoding defindo para serialização dos MElements
 */
public interface PrintWriterFactory extends Serializable {

    Charset getCharset();

    PrintWriter newPrintWriter(OutputStream outputStream);

    default StringPrintWriter newStringPrinWriter() {
        return new StringPrintWriter(getCharset());
    }
}
