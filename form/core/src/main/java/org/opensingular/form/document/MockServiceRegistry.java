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

import org.opensingular.internal.lib.commons.injection.FieldInjectionInfo;
import org.opensingular.internal.lib.commons.injection.SingularFieldValueFactory;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.internal.lib.commons.injection.SingularInjectorImpl;
import org.opensingular.lib.commons.context.RefService;
import org.opensingular.lib.commons.context.ServiceRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Classe de apoio a construção de testes.
 *
 * @author Daniel C. Bordin on 22/05/2017.
 */
public class MockServiceRegistry implements ServiceRegistry {

    private final Map<Class<?>, Object> byClass = new LinkedHashMap<>();

    private final Map<String, Object> byName = new HashMap();

    private SingularInjector injector;

    @Nonnull
    @Override
    public SingularInjector lookupSingularInjector() {
        if (injector == null) {
            injector = new SingularInjectorImpl(new SingularFieldValueFactory() {
                @Nullable
                @Override
                public Object getFieldValue(@Nonnull FieldInjectionInfo fieldInfo, @Nonnull Object fieldOwner) {
                    if (fieldInfo.getBeanName() != null) {
                        return byName.get(fieldInfo.getBeanName());
                    }
                    return byClass.get(fieldInfo.getType());
                }
            });
        }
        return injector;
    }

    @Nonnull
    @Override
    public Map<String, ServiceEntry> services() {
        return new HashMap<>(0);
    }

    @Override
    public <T> void bindService(Class<T> registerClass, RefService<? extends T> provider) {
        registerBean(registerClass, provider.get());
    }

    @Override
    public <T> void bindService(String serviceName, Class<T> registerClass, RefService<? extends T> provider) {
        registerBean(serviceName, provider.get());
    }

    @Nonnull
    @Override
    public <T> Optional<T> lookupService(@Nonnull Class<T> targetClass) {
        return Optional.ofNullable(targetClass.cast(byClass.get(targetClass)));
    }

    @Nonnull
    @Override
    public <T> Optional<T> lookupService(@Nonnull String name, @Nonnull Class<T> targetClass) {
        return Optional.ofNullable(targetClass.cast(byName.get(name)));
    }

    @Nonnull
    @Override
    public Optional<Object> lookupService(@Nonnull String name) {
        return Optional.ofNullable(byName.get(name));
    }

    public void registerBean(@Nonnull Class<?> targetClass, @Nonnull Object bean) {
        byClass.put(targetClass, bean);
        byName.put(targetClass.getName(), bean);
    }

    public void registerBean(@Nonnull String name, @Nonnull Object bean) {
        byName.put(name, bean);
    }
}
