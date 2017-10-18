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
