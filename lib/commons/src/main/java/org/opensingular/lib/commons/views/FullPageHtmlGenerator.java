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

import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Dá suporte a geração de uma página completa HTML.
 *
 * @author Daniel C. Bordin on 21/07/2017.
 */
public class FullPageHtmlGenerator implements Closeable {

    private final PrintWriter out;
    private final File outputFile;
    private final List<URL> internalCSS = new ArrayList<>();

    public FullPageHtmlGenerator(@Nonnull File outputFile) throws IOException {
        this.outputFile = outputFile;
        this.out = new PrintWriter(new FileOutputStream(outputFile));
    }

    public FullPageHtmlGenerator(@Nonnull PrintWriter out) {
        this.out = Objects.requireNonNull(out);
        this.outputFile = null;
    }

    public FullPageHtmlGenerator(@Nonnull Writer out) {
        this(toPrintWriter(out));
    }

    @Nonnull
    private static PrintWriter toPrintWriter(@Nonnull Writer out) {
        return out instanceof PrintWriter ? (PrintWriter) out : new PrintWriter(out);
    }

    public void writeBegin() {
        out.println("<html>");
        out.println("<body>");
    }

    private void generateInternalCss(URL url) {
        out.println("<!-- " + url.getFile() + " -->");
        try(InputStream in = url.openStream()) {
            IOUtils.copy(in, out, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PrintWriter getOut() {
        return out;
    }

    public void writeEndAndClose() {
        if (! internalCSS.isEmpty()) {
            out.println("<style>");
            internalCSS.forEach(this::generateInternalCss);
            out.println("</style>");
        }
        out.println("</body></html>");
        close();
    }

    @Override
    public void close() {
        out.close();
    }

    public void addInternalCSSFromResource(@Nonnull Object reference, @Nonnull String cssFileName) {
        URL url = reference.getClass().getResource(cssFileName);
        internalCSS.add(url);
    }
}
