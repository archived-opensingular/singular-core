package org.opensingular.lib.commons.io;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class StringPrintWriter extends PrintWriter {

    private Charset charset;

    public StringPrintWriter(Charset charset) {
        super(new StringWriter());
        this.charset = charset;
    }

    @Override
    public String toString() {
        return out.toString();
    }


    public byte[] toByteArray() {
        return out.toString().getBytes(charset);
    }

}
