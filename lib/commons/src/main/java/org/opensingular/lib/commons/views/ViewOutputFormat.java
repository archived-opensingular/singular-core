/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.opensingular.lib.commons.views.format.ViewOutputFormatExcel;
import org.opensingular.lib.commons.views.format.ViewOutputFormatHtml;
import org.opensingular.lib.commons.views.format.ViewOutputFormatPdf;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Representa um tipo de formato de geração da view. Um formato pode ou não ser um arquivo.
 *
 * @author Daniel C. Bordin on 24/07/2017.
 */
public class ViewOutputFormat {

    public static final ViewOutputFormatExportable HTML = new ViewOutputFormatHtml();
    public static final ViewOutputFormatExportable PDF = new ViewOutputFormatPdf();
    public static final ViewOutputFormatExportable EXCEL = new ViewOutputFormatExcel();

    private final String name;

    private final String displayName;

    public ViewOutputFormat(@Nonnull String name, @Nonnull String displayName) {
        this.name = Objects.requireNonNull(name);
        this.displayName = Objects.requireNonNull(displayName);
    }

    /** Nome abreviado (sigla) que representa unicamente esse formato. */
    public String getName() {
        return name;
    }

    /** Nome legível para o usuário que representa o formato. */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return Objects.equals(name, ((ViewOutputFormat) o).name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
