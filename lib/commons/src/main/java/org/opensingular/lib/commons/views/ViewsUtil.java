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

import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.lib.commons.util.TempFileUtils;
import org.opensingular.lib.commons.views.format.ViewOutputHtml;
import org.opensingular.lib.commons.views.format.ViewOutputHtmlWriterWrap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
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
    static ViewGenerator getGeneratorFor(@Nonnull ViewMultiGenerator target, @Nonnull ViewOutputFormat format)
            throws SingularViewUnsupportedFormatException {
        ViewGeneratorProvider<ViewGenerator, ViewOutput<?>> provider = getGeneratorProviderFor(target, format);
        return new ViewGenerator() {
            @Override
            public void generateView(@Nonnull ViewOutput<?> vOut) throws SingularViewUnsupportedFormatException {
                provider.generate(target, vOut);
            }

            @Override
            public boolean isDirectCompatibleWith(@Nonnull ViewOutputFormat format2) {
                return format.equals(format2);
            }
        };

    }

    @Nonnull
    private static <V extends ViewOutput<?>> ViewGeneratorProvider<ViewGenerator, V> getGeneratorProviderFor(
            @Nonnull ViewMultiGenerator target, @Nonnull ViewOutputFormat format)
            throws SingularViewUnsupportedFormatException {
        for (ViewGeneratorProvider<ViewGenerator, ? extends ViewOutput<?>> generator : target.getGenerators()) {
            if (Objects.equals(format, generator.getOutputFormat())) {
                return (ViewGeneratorProvider<ViewGenerator, V>) generator;
            }
        }
        throw new SingularViewUnsupportedFormatException(target, format);
    }

    public static String generateAsHtmlString(ViewGenerator target, boolean staticContent) {
        try (StringWriter out = new StringWriter()) {
            ViewOutputHtml vOut = new ViewOutputHtmlWriterWrap(out, staticContent);
            target.generateView(vOut);
            return out.toString();
        } catch (IOException e) {
            throw new SingularViewException(e);
        }
    }

    @Nullable
    private ViewGenerator findDirectCompatiable(@Nonnull ViewGenerator viewGenerator, @Nonnull ViewOutputFormatExportable format) {
        if (viewGenerator.isDirectCompatibleWith(format)) {
            return viewGenerator;
        } else if (viewGenerator instanceof ViewMultiGenerator) {
            return getGeneratorFor((ViewMultiGenerator) viewGenerator, format);
        }
        throw new SingularViewUnsupportedFormatException(viewGenerator, format);
    }

    @Nonnull
    public static File exportToTempFile(@Nonnull ViewGenerator viewGenerator,
                                        @Nonnull ViewOutputFormatExportable format) {
        return exportToTempFile(viewGenerator, format, null);
    }

    @Nonnull
    public static File exportToTempFile(@Nonnull final ViewGenerator viewGenerator,
                                        @Nonnull final ViewOutputFormatExportable format,
                                        @Nullable final TempFileProvider tmpProvider) {

        ViewGenerator copyOfViewGenerator = viewGenerator;

        if (!copyOfViewGenerator.isDirectCompatibleWith(format)) {
            if (copyOfViewGenerator instanceof ViewMultiGenerator) {
                copyOfViewGenerator = getGeneratorFor((ViewMultiGenerator) copyOfViewGenerator, format);
            }
            throw new SingularViewUnsupportedFormatException(copyOfViewGenerator, format);
        }
        try {
            File arq;
            if (tmpProvider == null) {
                arq = File.createTempFile(ViewsUtil.class.getSimpleName() + "_report", "." + format.getFileExtension());
            } else {
                arq = tmpProvider.createTempFile(ViewsUtil.class.getSimpleName() + "_report",
                        "." + format.getFileExtension());
            }
            boolean ok = false;
            try {
                format.generateFile(arq, copyOfViewGenerator);
                ok = true;
            } finally {
                if (!ok) {
                    TempFileUtils.deleteAndFailQuietily(arq, ViewsUtil.class);
                }
            }
            return arq;
        } catch (Exception e) {
            throw new SingularViewException("Fail to generate file in " + format.getName() + " format", e);
        }
    }
}
