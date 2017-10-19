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
