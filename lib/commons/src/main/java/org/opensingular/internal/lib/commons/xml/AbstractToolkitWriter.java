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

package org.opensingular.internal.lib.commons.xml;

import javax.annotation.Nonnull;
import java.io.PrintWriter;

abstract class AbstractToolkitWriter implements MElementWriter {

    private final static char[] ESPECIAL_TXT = new char[]{'&', '<', '>',};
    private final static String[] SUBSTITUTE_TXT = new String[]{"&amp;", "&lt;", "&gt;"};
    private final static char[] ESPECIAL_ATR = new char[]{'&', '<', '>', '"', '\''};
    private final static String[] SUBSTITUTE_ATR = new String[]{"&amp;", "&lt;", "&gt;", "&quot;", "&apos;"};


    void printConvertingSpecialCharactersTextNode(@Nonnull PrintWriter out, @Nonnull String text) {
        printConverting(out, text, ESPECIAL_TXT, SUBSTITUTE_TXT);
    }

    void printConvertingSpecialCharactersAttribute(@Nonnull PrintWriter out, @Nonnull String text) {
        printConverting(out, text, ESPECIAL_ATR, SUBSTITUTE_ATR);
    }

    private static void printConverting(@Nonnull PrintWriter out, @Nonnull String text, char[] especial,
            String[] substitute) {
        int len = text.length();
        int lastWritten = 0;
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            for (int j = 0; j < especial.length; j++) {
                if (c == especial[j]) {
                    out.write(text, lastWritten, i - lastWritten);
                    out.print(substitute[j]);
                    lastWritten = i + 1;
                    break;
                }
            }
        }
        out.write(text, lastWritten, len - lastWritten);
    }
}
