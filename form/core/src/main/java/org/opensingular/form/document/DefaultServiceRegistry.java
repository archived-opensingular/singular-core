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

import com.google.common.collect.ImmutableMap;
import org.opensingular.form.RefService;
import org.opensingular.form.SingularFormException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public final class DefaultServiceRegistry implements ServiceRegistry {

    private final Map<String, Pair>                  servicesByName  = newHashMap();
    private final Map<Class<?>, List<RefService<?>>> servicesByClass = newHashMap();
    private final List<ServiceRegistry>              registries      = newArrayList();

    public void addRegistry(ServiceRegistry r) {
        registries.add(r);
    }

    @Override
    public Map<String, Pair> services() {
        return ImmutableMap.copyOf(servicesByName);
    }

    @Override
    @Nonnull
    public <T> Optional<T> lookupService(@Nonnull Class<T> targetClass) {
        Optional<T> provider = lookupLocalService(targetClass);
        if (provider.isPresent()) {
            return provider;
        }
        return lookupChainedService(targetClass);
    }

    @Nonnull
    private <T> Optional<T> lookupChainedService(@Nonnull Class<T> targetClass) {
        for (ServiceRegistry r : registries) {
            Optional<T> provider = r.lookupService(targetClass);
            if (provider.isPresent()) {
                return provider;
            }
        }
        return Optional.empty();
    }

    @Nonnull
    public <T> Optional<T> lookupLocalService(@Nonnull Class<T> targetClass) {
        List<RefService<?>> result = findAllMatchingProviders(targetClass);
        if(result != null && !result.isEmpty()){
            return verifyResultAndReturn(targetClass, result);
        }
        return Optional.empty();
    }

    private <T> List<RefService<?>> findAllMatchingProviders(Class<T> targetClass) {
        List<RefService<?>> result = newArrayList();
        for(Map.Entry<Class<?>, List<RefService<?>>> entry : servicesByClass.entrySet()){
            if(targetClass.isAssignableFrom(entry.getKey())){
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    private <T> Optional<T> verifyResultAndReturn(Class<T> targetClass, List<RefService<?>> list) {
        if(list.size() == 1){
            RefService<?> provider = list.get(0);
            return Optional.of(targetClass.cast(provider.get()));
        }
        String message = String.format("There are %s options of type %s please be more specific", list.size(), targetClass.getName());
        throw new SingularFormException(message);
    }

    @Override
    @Nonnull
    public Optional<Object> lookupService(@Nonnull String name) {
        return lookupService(name, Object.class);
    }

    @Override
    public <T> Optional<T> lookupService(String name, Class<T> targetClass) {
        Optional<T> provider = lookupLocalService(name, targetClass);
        if(provider.isPresent()) {
            return provider;
        }
        return lookupChainedService(name, targetClass);
    }

    public <T> Optional<T> lookupLocalService(@Nonnull String name, @Nonnull Class<T> targetClass) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(targetClass);
        Pair ref = servicesByName.get(name);
        if (ref != null) {
            Object value = ref.provider.get();
            if (value == null) {
                servicesByName.remove(name);
            } else if (!targetClass.isInstance(value)) {
                String message = "For service '" + name + "' was found a clas of value "
                    + value.getClass().getName() + " instead of the expected " + targetClass.getName();
                throw new SingularFormException(message);
            }
            return Optional.ofNullable(targetClass.cast(value));
        }
        return Optional.empty();
    }

    public <T> T lookupLocalServiceOrException(@Nonnull String name, @Nonnull Class<T> targetClass) {
        return lookupLocalService(name, targetClass).orElseThrow(
                () -> new SingularFormException(createMsgForNotFoundBean(name, targetClass, true)));
    }

    private String createMsgForNotFoundBean(@Nonnull String name, @Nonnull Class<?> targetClass, boolean localScope) {
        return "Bean with '" + name + "' of class " + targetClass + " not found" +
                (localScope ? " in local registry" : " in any of the available registries");
    }

    @Nonnull
    private <T> Optional<T> lookupChainedService(@Nonnull String name, @Nonnull Class<T> targetClass) {
        for(ServiceRegistry r : registries){
            Optional<T> provider = r.lookupService(name, targetClass);
            if(provider.isPresent()) {
                return provider;
            }
        }
        return Optional.empty();
    }

    /**
     * Registers a {@link RefService}
     *  for the specified class
     * @param registerClass Class of the service the factory provides
     * @param provider provider of the service
     */
    public <T> void bindLocalService(Class<T> registerClass, RefService<? extends T> provider) {
        bindLocalService(UUID.randomUUID().toString(), registerClass, provider);
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
     * @param serviceName name of the service
     * @param registerClass Class of the service the factory provides
     * @param provider provider of the service
     */
    public <T> void bindLocalService(String serviceName, Class<T> registerClass, RefService<? extends T> provider) {
        bindByName(serviceName, registerClass, provider);
        bindByClass(registerClass, provider);
    }

    private <T> void bindByName(String serviceName, Class<T> registerClass, RefService<?> provider) {
        Objects.requireNonNull(registerClass);
        Objects.requireNonNull(provider);
        servicesByName.put(Objects.requireNonNull(serviceName), new Pair(registerClass, provider));
    }
}
