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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * É um injetor que fica verificando se foi disponibilizado um injetor com acesso efetivo aos beans a serem injetados
 * metidante chamada a {@link #findInjectorIfAvailable()}. Se ainda não estiver disponível o injetor, processa as
 * injeções (chamadas a {@link #inject(Object)}) com a seguinte lógica: se o objeto não tiver um injeção, não dispara
 * exception. Se tiver uma solicitação de injeção, dispara excpetion.
 *
 * @author Daniel C. Bordin on 21/05/2017.
 */
public abstract class SingularInjectorProxy implements SingularInjector {

    private static volatile SingularInjectorProxy emptyInjector;

    private volatile SingularInjector singularInjector;

    private Set<Class<?>> verified = new HashSet<>();

    /** Verifica se já disponível um injetor com acesso aos beans para injeção. */
    @Nullable
    protected abstract SingularInjector findInjectorIfAvailable();

    @Override
    public void inject(@Nonnull Object object) {
        if (singularInjector != null) {
            singularInjector.inject(object);
            return;
        }
        singularInjector = findInjectorIfAvailable();
        if (singularInjector != null) {
            verified = null;
            singularInjector.inject(object);
        } else if (!verified.contains(object.getClass())) {
            synchronized (this) {
                Class<?> clazz = object.getClass();
                FieldInjectionInfo fieldInfo = findInjection(object.getClass());
                if (fieldInfo != null) {
                    throw new SingularInjectionNotConfiguredException(fieldInfo, object);
                }
                verified.add(clazz);
            }
        }
    }

    /**
     * Verifica se a classe possui uma solicitação de injeção. Se possuir, retorna o primeiro field com injeção que
     * encontrar.
     */
    @Nullable
    private FieldInjectionInfo findInjection(@Nonnull Class<?> clazz) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    return new FieldInjectionInfo(field);
                }
            }
        }
        return null;
    }

    /**
     * Retorna um injetor que não possui nenhum bean disponível e que dispara uma exception se encontrar um @Inject que
     * seja de injeção obrigatória.
     */
    @Nonnull
    static SingularInjector getEmptyInjectorImpl() {
        if (emptyInjector == null) {
            synchronized (SingularInjectorProxy.class) {
                emptyInjector = new SingularInjectorProxy() {
                    @Nullable
                    @Override
                    protected SingularInjector findInjectorIfAvailable() {
                        return null;
                    }
                };
            }
        }
        return emptyInjector;
    }
}
