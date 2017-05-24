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

package org.opensingular.internal.lib.commons.injection;

import com.google.common.base.Throwables;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementação padrão de {@link SingularInjector}.
 *
 * @author Daniel C. Bordin on 16/05/2017.
 */
public class SingularInjectorImpl implements SingularInjector {

    /** Cache com as informação de injeção de um classe específica. */
    private final ConcurrentMap<Class<?>, FieldInjectionInfo[]> cache = new ConcurrentHashMap<>();

    /** Informação de injeção para classe que não possuem nenhum solicitação de injeção. */
    private static final FieldInjectionInfo[] EMPTY = new FieldInjectionInfo[0];

    /** Provedor de valores (de beans) para pedido de injeçao. */
    private final SingularFieldValueFactory factory;

    public SingularInjectorImpl(SingularFieldValueFactory factory) {this.factory = factory;}

    /**
     * Injects the specified object. This method is usually implemented by delegating to
     * {@link #inject(Object, SingularFieldValueFactory)} with some {@link SingularFieldValueFactory}
     *
     * @see #internalInject(Object, SingularFieldValueFactory)
     */
    @Override
    public void inject(@Nonnull Object object) {
        final Class<?> clazz = object.getClass();

        FieldInjectionInfo[] fields = cache.get(clazz);
        if (fields == null) {
            fields = findFields(clazz);
            cache.put(clazz, fields);
        }

        for (FieldInjectionInfo fieldInfo : fields) {
            Field field = fieldInfo.getField();
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                if (field.get(object) == null) {
                    Object value = factory.getFieldValue(fieldInfo, object);

                    setFieldValue(object, fieldInfo, value);
                }
            } catch (Exception e) {
                Throwables.throwIfInstanceOf(e, SingularInjectionException.class);
                throw new SingularInjectionException(fieldInfo, object, null, e);
            }
        }
    }

    private void setFieldValue(@Nonnull Object object, FieldInjectionInfo fieldInfo, Object value)
            throws IllegalAccessException {
        Field field = fieldInfo.getField();
        if (value != null) {
            if (fieldInfo.isFieldOptionalBeanReference()) {
                if (!fieldInfo.getType().isInstance(value)) {
                    throw new SingularInjectionException(fieldInfo, object,
                            " O tipo do Optional incompatível. Era esperado ser [" + fieldInfo.getType().getName() +
                                    "] mas o bean encontrado é do tipo [" + value.getClass().getName() + "]", null);
                }
                field.set(object, Optional.of(value));
            } else {
                field.set(object, value);
            }
        } else if (fieldInfo.isRequired()) {
            throw new SingularBeanNotFoundException(fieldInfo, object, "Não foi encontrado o bean", null);
        } else if (fieldInfo.isFieldOptionalBeanReference()) {
            field.set(object, Optional.empty());
        }
    }

    /**
     * Returns an array of fields that can be injected using the given field value factory
     */
    @Nonnull
    private FieldInjectionInfo[] findFields(@Nonnull Class<?> clazz) {
        List<FieldInjectionInfo> matched = new ArrayList<>();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    matched.add(factory.createCachedInfo(field));
                }
            }
        }
        return matched.isEmpty() ? EMPTY : matched.toArray(new FieldInjectionInfo[matched.size()]);
    }
}
