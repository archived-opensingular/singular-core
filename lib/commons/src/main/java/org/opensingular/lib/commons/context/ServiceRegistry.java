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

import org.opensingular.internal.lib.commons.injection.SingularInjector;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * Service Registry which provides a ḿeans to register and lookup for services.
 * This class is responsible for providing an SingularInjector for dependency injection resolution.
 *
 * @author Fabricio Buzeto
 */
public interface ServiceRegistry {

    /**
     * Tries to find a service based on its class;
     */
    @Nonnull
    <T> Optional<T> lookupService(@Nonnull Class<T> targetClass);

    @Nonnull
    default <T> T lookupServiceOrException(@Nonnull Class<T> targetClass) {
        return lookupService(targetClass).orElseThrow(
                () -> new SingularBeanNotFoundException("Bean of class " + targetClass + " not found"));
    }

    /**
     * Tries to find a service based on its name, casting to the desired type;
     */
    @Nonnull
    <T> Optional<T> lookupService(@Nonnull String name, @Nonnull Class<T> targetClass);

    @Nonnull
    default <T> T lookupServiceOrException(@Nonnull String name, @Nonnull Class<T> targetClass) {
        return lookupService(name, targetClass).orElseThrow(
                () -> new SingularBeanNotFoundException("Bean of class " + targetClass + " and name " + name + " not found"));
    }

    /**
     * Tries to find a service based on its name;
     */
    @Nonnull
    <T> Optional<T> lookupService(@Nonnull String name);

    @SuppressWarnings("unchecked")
    @Nonnull
    default <T> T lookupServiceOrException(@Nonnull String name) {
        return (T) lookupService(name).orElseThrow(
                () -> new SingularBeanNotFoundException("Bean of name " + name + " not found"));
    }

    /**
     * Retornar o serviço de injeção de beans em um objeto.
     */
    @Nonnull
    default SingularInjector lookupSingularInjector() {
        Optional<SingularInjector> injector = lookupService(SingularInjector.class);
        if (injector.isPresent()) {
            return injector.get();
        }
        return SingularInjector.getEmptyInjector();
    }

    /**
     * List all factories for all registered services;
     *
     * @return factory map.
     */
    @Nonnull
    Map<String, ServiceEntry> services();


    <T> void bindService(Class<T> registerClass, RefService<? extends T> provider);

    <T> void bindService(String serviceName, Class<T> registerClass, RefService<? extends T> provider);

    @SuppressWarnings("serial")
    class ServiceEntry implements Serializable {
        final public Class<?>      type;
        final public RefService<?> provider;

        public ServiceEntry(Class<?> type, RefService<?> provider) {
            this.type = type;
            this.provider = provider;
        }
    }
}
