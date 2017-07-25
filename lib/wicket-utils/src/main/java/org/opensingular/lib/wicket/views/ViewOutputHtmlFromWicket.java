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
import org.apache.wicket.core.request.handler.BufferedResponseRequestHandler;
import org.apache.wicket.protocol.http.BufferedWebResponse;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.jetbrains.annotations.NotNull;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.views.ViewOutput;
import org.opensingular.lib.commons.views.ViewOutputFormat;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * @author Daniel on 25/07/2017.
 */
public class ViewOutputHtmlFromWicket extends ViewOutput {

    private final Writer out;

    public ViewOutputHtmlFromWicket(RequestCycle cycle) {
        Response response = cycle.getResponse();
        if (response instanceof WebResponse) {
            BufferedWebResponse bufferedWebResponse = new BufferedWebResponse((WebResponse) response);
            out = new BufferedWebResponseWriterAdapter(bufferedWebResponse);
            cycle.scheduleRequestHandlerAfterCurrent(new BufferedResponseRequestHandler(bufferedWebResponse));
        } else {
            throw new SingularException("O RequestCycle atual não é uma WebResponse");
        }
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

    private static class BufferedWebResponseWriterAdapter extends Writer {
        private final BufferedWebResponse bufferedWebResponse;

        private BufferedWebResponseWriterAdapter(BufferedWebResponse bufferedWebResponse) {
            this.bufferedWebResponse = bufferedWebResponse;
        }

        @Override
        public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
            bufferedWebResponse.write(new String(cbuf).getBytes(StandardCharsets.UTF_8), off, len);
        }

        @Override
        public void flush() throws IOException {
            bufferedWebResponse.flush();
        }

        @Override
        public void close() throws IOException {
            bufferedWebResponse.close();
        }
    }
}