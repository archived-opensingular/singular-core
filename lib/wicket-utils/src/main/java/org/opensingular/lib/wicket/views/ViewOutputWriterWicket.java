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

package org.opensingular.lib.wicket.views;

import org.opensingular.lib.commons.views.ViewOutputWriter;
import org.opensingular.lib.commons.views.ViewOutputFormat;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Daniel C. Bordin on 24/07/2017.
 */
public class ViewOutputWriterWicket extends ViewOutputWriter {

    public static final ViewOutputFormat WICKET = new ViewOutputFormat("WICKET", "Wicket");

    @Override
    public boolean isStaticContent() {
        return false;
    }

    @Override
    public Writer getOutput() {
        throw new RuntimeException("Método não suportado");
    }

    @Override
    public void addImagem(String nome, byte[] dados) throws IOException {
        throw new RuntimeException("Método não suprotado");
    }

    @Override
    public ViewOutputFormat getFormat() {
        return WICKET;
    }
}
