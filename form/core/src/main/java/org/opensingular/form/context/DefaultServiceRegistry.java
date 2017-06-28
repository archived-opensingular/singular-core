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

package org.opensingular.form.context;

import com.google.common.collect.ImmutableMap;
import org.opensingular.form.RefService;
import org.opensingular.form.SingularFormException;
import org.opensingular.internal.lib.commons.injection.SingularInjector;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public class DefaultServiceRegistry implements ServiceRegistry {

    private final Map<String, ServiceEntry>          servicesByName  = newHashMap();
    private final Map<Class<?>, List<RefService<?>>> servicesByClass = newHashMap();

    protected DefaultServiceRegistry() {
    }

    /**
     * USO INTERNO APENAS. Retorna os serviços registrados diretamente no documento.
     */
    @Override
    public Map<String, ServiceEntry> services() {
        return ImmutableMap.copyOf(servicesByName);
    }

    @Override
    @Nonnull
    public <T> Optional<T> lookupService(@Nonnull Class<T> targetClass) {
        Optional<T> provider = lookupLocalService(targetClass);
        return provider;
    }


    @Nonnull
    public <T> Optional<T> lookupLocalService(@Nonnull Class<T> targetClass) {
        List<RefService<?>> result = findAllMatchingProviders(targetClass);
        if (result != null && !result.isEmpty()) {
            return verifyResultAndReturn(targetClass, result);
        }
        return Optional.empty();
    }

    private <T> List<RefService<?>> findAllMatchingProviders(Class<T> targetClass) {
        List<RefService<?>> result = newArrayList();
        for (Map.Entry<Class<?>, List<RefService<?>>> entry : servicesByClass.entrySet()) {
            if (targetClass.isAssignableFrom(entry.getKey())) {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    private <T> Optional<T> verifyResultAndReturn(Class<T> targetClass, List<RefService<?>> list) {
        if (list.size() == 1) {
            RefService<?> provider = list.get(0);
            return Optional.of(targetClass.cast(provider.get()));
        }
        String message = String.format("There are %s options of type %s please be more specific", list.size(), targetClass.getName());
        throw new SingularFormException(message);
    }

    @Override
    @Nonnull
    public <T> Optional<T> lookupService(@Nonnull String name) {
        return (Optional<T>) lookupService(name, Object.class);
    }

    @Nonnull
    @Override
    public SingularInjector lookupSingularInjector() {
        return SingularInjector.getEmptyInjector();
    }

    @Override
    public <T> Optional<T> lookupService(String name, Class<T> targetClass) {
        Optional<T> provider = lookupLocalService(name, targetClass);
        return provider;

    }

    public <T> Optional<T> lookupLocalService(@Nonnull String name, @Nonnull Class<T> targetClass) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(targetClass);
        ServiceEntry ref = servicesByName.get(name);
        if (ref != null) {
            Object value = ref.provider.get();
            if (value == null) {
                servicesByName.remove(name);
            } else if (!targetClass.isInstance(value)) {
                String message = "For service '" + name + "' was found a class of value "
                        + value.getClass().getName() + " instead of the expected " + targetClass.getName();
                throw new SingularFormException(message);
            }
            return Optional.ofNullable(targetClass.cast(value));
        }
        return Optional.empty();
    }


    /**
     * Registers a {@link RefService}
     * for the specified class
     *
     * @param registerClass Class of the service the factory provides
     * @param provider      provider of the service
     */
    @Override
    public <T> void bindService(Class<T> registerClass, RefService<? extends T> provider) {
        bindService(UUID.randomUUID().toString(), registerClass, provider);
    }

    private <T> void bindByClass(Class<T> registerClass, RefService<? extends T> provider) {
        List<RefService<?>> list = servicesByClass.get(registerClass);
        if (list == null) {
            list = newArrayList();
            servicesByClass.put(registerClass, list);
        }
        list.add(provider);
    }

    /**
     * Registers a {@link RefService} for the specified class with a unique name.
     *
     * @param serviceName   name of the service
     * @param registerClass Class of the service the factory provides
     * @param provider      provider of the service
     */
    @Override
    public <T> void bindService(String serviceName, Class<T> registerClass, RefService<? extends T> provider) {
        bindByName(serviceName, registerClass, provider);
        bindByClass(registerClass, provider);
    }

    private <T> void bindByName(String serviceName, Class<T> registerClass, RefService<?> provider) {
        Objects.requireNonNull(registerClass);
        Objects.requireNonNull(provider);
        servicesByName.put(Objects.requireNonNull(serviceName), new ServiceEntry(registerClass, provider));
    }
}
