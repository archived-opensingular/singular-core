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

import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.jetbrains.annotations.NotNull;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.views.ViewOutputFormat;
import org.opensingular.lib.commons.views.format.ViewOutputHtml;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Daniel C. Bordin on 25/07/2017.
 */
public class ViewOutputHtmlToWicket extends ViewOutputHtml {

    private final Writer out;

    public ViewOutputHtmlToWicket(RequestCycle cycle) {
        Response response = cycle.getResponse();
        if (response instanceof WebResponse) {
            out = new WebResponseWriterAdapter((WebResponse) response);
        } else {
            throw new SingularException("O RequestCycle atual não é uma WebResponse");
        }
    }

    @Override
    public boolean isStaticContent() {
        return false;
    }

    @Override
    public Writer getOutput() {
        return out;
    }

    @Override
    public void addImagem(String nome, byte[] dados) throws IOException {
        throw new SingularException("Nao implementado");
    }

    @Override
    public ViewOutputFormat getFormat() {
        return ViewOutputFormat.HTML;
    }

    private static class WebResponseWriterAdapter extends Writer {
        private final WebResponse webResponse;

        private WebResponseWriterAdapter(WebResponse webResponse) {
            this.webResponse = webResponse;
        }

        @Override
        public void write(String str) throws IOException {
            webResponse.write(str);
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            webResponse.write(str.substring(off, off + len));
        }

        @Override
        public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
            webResponse.write(new String(cbuf, off, len));
        }

        @Override
        public void flush() throws IOException {
            webResponse.flush();
        }

        @Override
        public void close() throws IOException {
            webResponse.close();
        }
    }
}