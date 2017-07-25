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

import org.apache.wicket.Component;
import org.apache.wicket.request.Response;
import org.opensingular.lib.commons.views.ViewOutput;
import org.opensingular.lib.commons.views.ViewOutputFormat;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * @author Daniel on 25/07/2017.
 */
public class ViewOutputHtmlFromWicket extends ViewOutput {

    private final PrintWriter out;

    public ViewOutputHtmlFromWicket(Component component) {
        Response response = component.getRequestCycle().getResponse();
        out = new PrintWriter(response.getOutputStream());
    }

    @Override
    public boolean isStaticContent() {
        return false;
    }

    @Override
    public Writer getWriter() {
        return out;
    }

    @Override
    public void addImagem(String nome, byte[] dados) throws IOException {
        throw new RuntimeException("Nao implementado");
    }

    @Override
    public ViewOutputFormat getFormat() {
        return ViewOutputFormat.HTML;
    }
}
