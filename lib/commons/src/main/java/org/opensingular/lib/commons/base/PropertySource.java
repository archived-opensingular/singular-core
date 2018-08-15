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

package org.opensingular.lib.commons.base;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URL;
import java.util.Objects;

/**
 * Identifies the point of origin of property key's value. May be a file, resource in classpath, etc.
 *
 * @author Daniel C. Bordin
 * @since 2017-11-02
 */
public class PropertySource<T> {

    /** Represents a unknown source (most likely a direct set from a code). */
    public static final PropertySource<?> UNKNOWN = new PropertySource<String>("unkown source");

    final T source;

    private PropertySource(@Nonnull T source) {this.source = Objects.requireNonNull(source);}

    /** Visual representation of this source. */
    public String getDescription() {
        return source.toString();
    }

    /** Create a source for the file. */
    @Nonnull
    public static PropertySource of(@Nonnull File file) {
        return new PropertySourceFile(file);
    }

    /** Create a source for the url. */
    @Nonnull
    public static PropertySource of(@Nonnull URL url) {
        return new PropertySourceURL(url);
    }

    public String toString() {
        return getDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(source, ((PropertySource<?>) o).source);
    }

    @Override
    public int hashCode() {
        return source != null ? source.hashCode() : 0;
    }

    /** Object that represent this source. */
    @Nonnull
    public T get() {
        return source;
    }

    private static class PropertySourceFile extends PropertySource<File> {
        private PropertySourceFile(@Nonnull File source) {super(source);}

        @Override
        public String getDescription() {
            return source.getAbsolutePath();
        }
    }

    private static class PropertySourceURL extends PropertySource<URL> {
        private PropertySourceURL(@Nonnull URL source) {super(source);}
    }
}
