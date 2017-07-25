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
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author Daniel C. Bordin on 24/07/2017.
 */
public class ViewOutputFormat {

    public static final ViewOutputFormat HTML = new ViewOutputFormat("HTML", "html", "html");
    public static final ViewOutputFormat PDF = new ViewOutputFormat("PDF", "pdf", "pdf");
    public static final ViewOutputFormat EXCEL = new ViewOutputFormat("EXCEL", "Excel", "xlsx");

    private final String name;
    private final String displayName;
    private final String fileExtension;


    public ViewOutputFormat(@Nonnull String name, @Nonnull String displayName) {
        this(name, displayName, null);
    }

    public ViewOutputFormat(@Nonnull String name, @Nonnull String displayName, @Nullable String fileExtension) {
        this.name = Objects.requireNonNull(name);
        this.displayName = Objects.requireNonNull(displayName);
        this.fileExtension = fileExtension;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isFileFormat() {
        return fileExtension != null;
    }

    public String getFileExtension() {
        return fileExtension;
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
