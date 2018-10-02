/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.internal.lib.commons.xml;

import java.io.PrintWriter;

public abstract class AbstractToolkitWriter implements MElementWriter {

    private final static Object[][] REPLACEMENTS = {
            new Boolean[]{true, true, true, false, false}, // REQUIRED FOR NODE CONTENT (TRUE/FALSE)
            new Character[]{'&', '<', '>', '"', '\''}, // ESPECIAL CHAR
            new String[]{"&amp;", "&lt;", "&gt;", "&quot;", "&apos;"} // REPLACEMENT
    };


    protected void printConvertingSpecialCharactersTextNode(PrintWriter out, char[] text) {
        printConvertingSpecialCharacters(out, text, false);
    }

    protected void printConvertingSpecialCharactersAttribute(PrintWriter out, char[] text) {
        printConvertingSpecialCharacters(out, text, true);
    }


    private void printConvertingSpecialCharacters(PrintWriter out, char[] text, boolean attributeEscape) {
        Boolean[]   REQUIRED_FOR_NODE_CONTENT = (Boolean[]) REPLACEMENTS[0];
        Character[] ESPECIAL                  = (Character[]) REPLACEMENTS[1];
        String[]    SUBSTITUTE                = (String[]) REPLACEMENTS[2];
        int         len                       = text.length;
        int         lastWritten               = 0;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < ESPECIAL.length; j++) {
                if (text[i] == ESPECIAL[j]) {
                    if (REQUIRED_FOR_NODE_CONTENT[j] || attributeEscape) {
                        out.write(text, lastWritten, i - lastWritten);
                        out.print(SUBSTITUTE[j]);
                        lastWritten = i + 1;
                    }
                }
            }
        }
        out.write(text, lastWritten, len - lastWritten);
    }
}
