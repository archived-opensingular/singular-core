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

package org.opensingular.form.document;

import org.opensingular.form.SDictionary;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.internal.util.SerializableReference;
import org.opensingular.lib.commons.lambda.IFunction;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Represents a serializable reference to a {@link SDictionary}. The implementations, when being deserializad should be
 * capable to retrieve or rebuild the dictionary.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefDictionary extends SerializableReference<SDictionary> {

    /** Creates a reference for type that uses the current dictionary reference to ask for the type. */
    @Nonnull
    public RefType refType(@Nonnull Class<? extends SType> typeClass) {
        Objects.requireNonNull(typeClass);
        return new RefTypeFromRefDictionary(this) {
            @Override
            @Nonnull
            protected SType<?> retrieve() {
                return getDictionary().getType(typeClass);
            }
        };
    }

    /** Creates a reference for type that uses the current dictionary call a builder of the type. */
    @Nonnull
    public RefType refType(@Nonnull IFunction<RefDictionary, SType<?>> creator) {
        Objects.requireNonNull(creator);
        return new RefTypeFromRefDictionary(this) {
            @Override
            @Nonnull
            protected SType<?> retrieve() {
                SType<?> type = creator.apply(RefDictionary.this);
                if (type == null) {
                    throw new SingularFormException(creator.getClass().getName() + ".get() returned null");
                } else if (type.getDictionary() != RefDictionary.this.get()) {
                    throw new SingularFormException(creator.getClass().getName() +
                            ".get() returned a type with a dictionary reference different of the provided to the " +
                            "function");
                }
                return type;
            }
        };
    }

    /** Creates dictionary ref that creates a blank dictionary using {@link SDictionary#create()}. */
    @Nonnull
    public static RefDictionary newBlank() {
        return new RefDictionary() {
            @Nonnull
            @Override
            protected SDictionary retrieve() {
                return SDictionary.create();
            }
        };
    }
}