/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.lib.commons.views;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Objetos que implementam essa interface s�o capazes de gerar o suas exibi��o para um ViewOutput.
 *
 * @author Daniel C. Bordin.
 */
@FunctionalInterface
public interface ViewGenerator {

    public abstract void generateView(ViewOutput vOut) throws SingularUnsupportedViewException;

    public default String generateAsHtmlString(boolean staticContent) {
        final ByteArrayOutputStream dataSource = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(dataSource);
        ViewOutput vOut = new ViewOutput() {
            @Override
            public boolean isStaticContent() {
                return staticContent;
            }

            @Override
            public Writer getWriter() {
                return writer;
            }

            @Override
            public void addImagem(String nome, byte[] dados) throws IOException {
                throw new UnsupportedOperationException("addImagem(String, dados) n�o suportado ");
            }
        };
        generateView(vOut);
        writer.flush();
        return dataSource.toString();
    }
}
