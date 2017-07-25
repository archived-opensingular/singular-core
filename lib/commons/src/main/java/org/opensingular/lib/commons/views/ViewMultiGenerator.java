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
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Representa um objeto capaz de gerar multiplos formatos de saída.
 *
 * @author Daniel C. Bordin on 24/07/2017.
 */
public interface ViewMultiGenerator extends ViewGenerator {

    @Nonnull
    Collection<ViewGeneratorProvider<ViewGenerator, ? extends ViewOutput<?>>> getGenerators();

    @Nonnull
    default Collection<ViewOutputFormat> getDirectSupportedFormats() {
        return getGenerators().stream().map(ViewGeneratorProvider::getOutputFormat).collect(Collectors.toList());
    }

    @Override
    default void generateView(@Nonnull ViewOutput<?> vOut) throws SingularUnsupportedViewException {
        ViewGeneratorProvider<ViewGenerator, ViewOutput<?>> generator = ViewsUtil.getGeneratorFor(this, vOut.getFormat());
        generator.generate(this, vOut);
    }


    @Override
    default boolean isDirectCompatiableWith(@Nonnull ViewOutputFormat format) {
        return getGenerators().stream().anyMatch(p -> Objects.equals(format, p.getOutputFormat()));
    }

    default ViewGenerator getGeneratorFor(@Nonnull ViewOutput<java.io.Writer> vOut) {
        return getGeneratorFor(vOut.getFormat());
    }

    default ViewGenerator getGeneratorFor(@Nonnull ViewOutputFormat format) {
        ViewGeneratorProvider<ViewGenerator, ViewOutput<?>> generator = ViewsUtil.getGeneratorFor(this, format);
        return new ViewGenerator() {
            @Override
            public void generateView(@Nonnull ViewOutput<?> vOut) throws SingularUnsupportedViewException {
                generator.generate(ViewMultiGenerator.this, vOut);
            }

            @Override
            public boolean isDirectCompatiableWith(@Nonnull ViewOutputFormat format2) {
                return format.equals(format2);
            }
        };
    }
}
