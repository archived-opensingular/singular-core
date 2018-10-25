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

package org.opensingular.lib.commons.util;

import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ObjectUtils {

    private ObjectUtils() {}

    /** Instantiates the informed class and throws a {@link SingularException} if the instantiation fails. */
    @Nonnull
    public static <T> T newInstance(@Nonnull Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw SingularException.rethrow("Fail to instantiate class '" + clazz.getName() + '\'', e);
        }
    }

    /**
     * Instantiates class with the informed named and checks if it's of the expected class. Any failing in the process,
     * throws a {@link SingularException}.
     */
    @Nonnull
    public static <T> T newInstance(@Nonnull String className, @Nonnull Class<T> baseClass) {
        Class<?> c = loadClass(className, baseClass);
        return baseClass.cast(newInstance(c));
    }

    /**
     * Loads the class by the informed name and checks if it's of the expected class. Any failing in the process,
     * throws a {@link SingularException}.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> loadClass(@Nonnull String className, @Nonnull Class<T> baseClass) {
        Class<?> c;
        try {
            c = Class.forName(className);
        } catch (Exception e) {
            throw SingularException.rethrow("Error loading class '" + className + "'", e);
        }
        if (!baseClass.isAssignableFrom(c)) {
            throw new SingularException(
                    "The asked class '" + className + "' doesn't extends class '" + baseClass.getName() + '\'');
        }
        return (Class<T>) c;
    }

    public static boolean isAllNull(Object... objects) {
        for (Object object : objects) {
            if (object != null) {
                return false;
            }
        }
        return true;
    }

    /** Guaranties to return a not null value. If not possible, throws a exception. */
    @Nonnull
    public static <T> T notNull(@Nullable T value, @Nonnull T defaultValue) {
        if (defaultValue == null) {
            throw new SingularException("DefaultValue can't be null");
        } else if (value != null) {
            return value;
        }
        return defaultValue;
    }
}
