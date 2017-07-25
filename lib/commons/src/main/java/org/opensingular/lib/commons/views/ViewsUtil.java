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

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * @author Daniel C. Bordin on 24/07/2017.
 */
public class ViewsUtil {

    private static ServiceLoader<ViewOutputFormat> formatsLoader;

    private static ServiceLoader<ViewOutputFormat> getFormatsLoader() {
        if (formatsLoader == null) {
            formatsLoader = ServiceLoader.load(ViewOutputFormat.class);
        }
        return formatsLoader;
    }

    @Nonnull
    static final <T> ViewGeneratorProvider<T> getGeneratorFor(@Nonnull ViewMultiGenerator<T> target,
            @Nonnull ViewOutputFormat format) throws SingularUnsupportedViewException {
        for (ViewGeneratorProvider<T> p : target.getGenerators()) {
            if (Objects.equals(format, p.getOutputFormat())) {
                return p;
            }
        }
        throw new SingularUnsupportedViewException(target, format);
    }

    public static String generateAsHtmlString(ViewGenerator target, boolean staticContent) {
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

            @Override
            public ViewOutputFormat getFormat() {
                return ViewOutputFormat.HTML;
            }
        };
        target.generateView(vOut);
        writer.flush();
        return dataSource.toString();
    }
}
