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

package org.opensingular.internal.lib.commons.injection;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Representa metadado para injeção de bean em um field de um objeto.
 *
 * @author Daniel C. Bordin on 17/05/2017.
 */
public class FieldInjectionInfo {

    private final Field field;
    private final Class<?> fieldType;
    private final String name;
    private final boolean required;
    private final boolean optionalClass;

    public FieldInjectionInfo(@Nonnull Field field) {
        this.field = field;
        Named named = field.getAnnotation(Named.class);
        this.name = named != null ? StringUtils.trimToNull(named.value()) : null;

        Class<?> type = field.getType();
        this.optionalClass = (type == Optional.class);
        this.required = !optionalClass;
        if (optionalClass) {
            Type gType = field.getGenericType();
            if (gType instanceof ParameterizedType) {
                Type optionalType = ((ParameterizedType) gType).getActualTypeArguments()[0];
                if (optionalType instanceof Class) {
                    type = (Class) optionalType;
                }
            }
        }
        this.fieldType = type;
    }

    /** Retorna o campo da classe a ser injetado. */
    @Nonnull
    public Field getField() {
        return field;
    }

    /** Retorna o nome do bean a ser procurado. Pode ser null. */
    @Nullable
    public String getBeanName() {
        return name;
    }

    /** Indica se a injeção é obrigatória. Ou seja, se encontrar o bean é opcional ou obrigatório. */
    public boolean isRequired() {
        return required;
    }

    /**
     * Retorna o tipo do bena a ser localizado. Nao necessariamente é o mesmo tipo do field a ser injetado (ver {@link
     * #isFieldOptionalBeanReference()}.
     */
    @Nonnull
    public Class<?> getType() {
        return fieldType;
    }

    /** Verifica se a injeçao pede por um bean com nome específico. */
    public boolean hasBeanName() {
        return name != null;
    }

    /** Indica se o field a ser injetado é uma referência ao bean usando {@link java.util.Optional}. */
    public boolean isFieldOptionalBeanReference() {
        return optionalClass;
    }

    /** Nome do field sendo injetado. */
    @Nonnull
    public String getFieldName() {
        return field.getName();
    }
}
