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

import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.lib.commons.context.DefaultServiceRegistry;
import org.opensingular.lib.commons.context.RefService;
import org.opensingular.lib.commons.context.ServiceRegistry;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

/**
 * Utility Singular {@link ServiceRegistry} implementation with an internal DefaultServiceRegistry.
 * The internal DefaultServiceRegistry can be used do bind and lookup local services. The list of services provided by
 * the {@link ServiceRegistry#services()} contains local services only.
 * Services not found in this internal ServiceRegistry are looked up in the delegate ServiceRegistry
 * Also the {@link SingularInjector} returned by this class is the one provided by the delegate ServiceRegistry
 *
 * @author Vinicius Nunes
 */
public class DelegatingLocalServiceRegistry implements ServiceRegistry {


    private ServiceRegistry thisOne = new DefaultServiceRegistry();
    private ServiceRegistry delegate;

    public DelegatingLocalServiceRegistry(ServiceRegistry serviceRegistry) {
        this.delegate = serviceRegistry;
    }

    @Nonnull
    @Override
    public <T> Optional<T> lookupService(@Nonnull Class<T> targetClass) {
        Optional<T> value = thisOne.lookupService(targetClass);
        if (!value.isPresent()) {
            value = delegate.lookupService(targetClass);
        }
        return value;
    }

    @Nonnull
    @Override
    public <T> Optional<T> lookupService(@Nonnull String name, @Nonnull Class<T> targetClass) {
        Optional<T> value = thisOne.lookupService(name, targetClass);
        if (!value.isPresent()) {
            value = delegate.lookupService(name, targetClass);
        }
        return value;
    }

    @Nonnull
    @Override
    public <T> Optional<T> lookupService(@Nonnull String name) {
        Optional<T> value = thisOne.lookupService(name);
        if (!value.isPresent()) {
            value = delegate.lookupService(name);
        }
        return value;
    }

    @Nonnull
    @Override
    public SingularInjector lookupSingularInjector() {
        return delegate.lookupSingularInjector();
    }

    @Nonnull
    @Override
    public Map<String, ServiceEntry> services() {
        return thisOne.services();
    }

    @Override
    public <T> void bindService(Class<T> registerClass, RefService<? extends T> provider) {
        thisOne.bindService(registerClass, provider);
    }

    @Override
    public <T> void bindService(String serviceName, Class<T> registerClass, RefService<? extends T> provider) {
        thisOne.bindService(serviceName, registerClass, provider);
    }
}
