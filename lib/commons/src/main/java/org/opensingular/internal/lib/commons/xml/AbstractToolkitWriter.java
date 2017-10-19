package org.opensingular.internal.lib.commons.xml;

import java.io.PrintWriter;

public abstract class AbstractToolkitWriter implements MElementWriter {

    private final char[] ESPECIAL = {'&', '<', '>'};
    private final String[] SUBSTITUTE = {"&amp;", "&lt;", "&gt;"};


    protected void printConvertingSpecialCharacters(PrintWriter out, char[] text) {
        int len           = text.length;
        int lastWritten = 0;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < 3; j++) {
                if (text[i] == ESPECIAL[j]) {
                    out.write(text, lastWritten, i - lastWritten);
                    out.print(SUBSTITUTE[j]);
                    lastWritten = i + 1;
                }
            }
        }
        out.write(text, lastWritten, len - lastWritten);
    }
}
