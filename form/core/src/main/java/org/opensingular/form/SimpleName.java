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

package org.opensingular.form;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a valid and immutable simple name (a name without a dot) for a attribute or {@link SType} that has already
 * been verified. The main goal of this class is to avoid repeated verifications of the name.
 *
 * @author Daniel C. Bordin on 20/08/2017.
 */
public final class SimpleName {


    private final String simpleName;

    public SimpleName(@Nonnull String simpleName) {this.simpleName = SFormUtil.validateSimpleName(simpleName);}

    /** Return the actual name. */
    @Nonnull
    public String get() {
        return simpleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return simpleName.equals(((SimpleName) o).simpleName);

    }

    @Override
    public int hashCode() {
        return simpleName.hashCode();
    }

    @Override
    public String toString() {
        return simpleName;
    }

    @Nullable
    public static SimpleName ofNullable(@Nullable String name) {
        return name == null ? null : new SimpleName(name);
    }
}
