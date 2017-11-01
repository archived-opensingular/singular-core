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

package org.opensingular.lib.commons.context;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

public class DefaultServiceRegistry implements ServiceRegistry {

    private static class ServiceKey {
        String name;
        Class<?> serviceClass;

        public ServiceKey(String name, Class<?> serviceClass) {
            this.name = name;
            this.serviceClass = serviceClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ServiceKey)) return false;

            ServiceKey that = (ServiceKey) o;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            return that.serviceClass != null ? that.serviceClass.isAssignableFrom(serviceClass) : this.serviceClass == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (serviceClass != null ? serviceClass.hashCode() : 0);
            return result;
        }

        public String getName() {
            return Optional.ofNullable(name).orElse(serviceClass.getName());
        }
    }

    private final Map<ServiceKey, ServiceEntry> services = newHashMap();

    public DefaultServiceRegistry() {
    }

    /**
     * USO INTERNO APENAS. Retorna os serviços registrados diretamente no documento.
     */
    @Override
    public Map<String, ServiceEntry> services() {
        return services.entrySet().stream().collect(HashMap::new, (m, e) -> m.put(e.getKey().getName(), e.getValue()), HashMap::putAll);
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
        for (Map.Entry<ServiceKey, ServiceEntry> entry : services.entrySet()) {
            if (targetClass.isAssignableFrom(entry.getKey().serviceClass)) {
                result.add(entry.getValue().provider);
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
        throw new SingularRegistryLookupException(message);
    }

    @Override
    @Nonnull
    public <T> Optional<T> lookupService(@Nonnull String name) {
        return (Optional<T>) lookupService(name, Object.class);
    }

    @Override
    public <T> Optional<T> lookupService(String name, Class<T> targetClass) {
        Optional<T> provider = lookupLocalService(name, targetClass);
        return provider;

    }

    public <T> Optional<T> lookupLocalService(@Nonnull String name, @Nonnull Class<T> targetClass) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(targetClass);
        ServiceEntry ref = services.entrySet().stream().filter(k -> k.getKey().equals(new ServiceKey(name, targetClass))).findFirst().map(Map.Entry::getValue).orElse(null);
        if (ref != null) {
            Object value = ref.provider.get();
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
        bindService(registerClass.getName(), registerClass, provider);
    }


    /**
     * Registers a {@link RefService} for the specified class with a unique name.
     *
     * @param serviceName   name of the service
     * @param registerClass Class of the service the factory provides
     * @param provider      provider of the service
     */
    @Override
    public <T> void bindService(@Nonnull String serviceName, Class<T> registerClass, RefService<? extends T> provider) {
        services.put(new ServiceKey(Objects.requireNonNull(serviceName), registerClass), new ServiceEntry(registerClass, provider));
    }

}
