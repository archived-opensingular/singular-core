package org.opensingular.internal.lib.commons.xml;

import java.io.PrintWriter;

public abstract class AbstractToolkitWriter implements MElementWriter {

    private final char[]   ESPECIAIS   = {'&', '<', '>'};
    private final String[] SUBSTITUTOS = {"&amp;", "&lt;", "&gt;"};


    protected void printConverteCaracteresEspeciais(PrintWriter out, char[] texto) {
        int len           = texto.length;
        int ultimoEscrito = 0;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < 3; j++) {
                if (texto[i] == ESPECIAIS[j]) {
                    out.write(texto, ultimoEscrito, i - ultimoEscrito);
                    out.print(SUBSTITUTOS[j]);
                    ultimoEscrito = i + 1;
                }
            }
        }
        out.write(texto, ultimoEscrito, len - ultimoEscrito);
    }
}
