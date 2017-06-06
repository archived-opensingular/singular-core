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

package org.opensingular.form.spring;

import org.opensingular.form.RefService;
import org.opensingular.form.context.DefaultServiceRegistry;
import org.opensingular.form.context.ServiceRegistry;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.internal.lib.support.spring.injection.SingularSpringInjector;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

/**
 * This class provides a {@link ServiceRegistry} that relays service lookup
 * to the spring context.
 *
 * @author Fabricio Buzeto
 * @author Daniel C. Bordin
 */
public class SpringServiceRegistry implements ServiceRegistry, ApplicationContextAware, Loggable {

    private SingularInjector injector;

    private DefaultServiceRegistry delegate = new DefaultServiceRegistry(){};

    public SpringServiceRegistry() {
    }

    @Override
    @Nonnull
    public <T> Optional<T> lookupService(@Nonnull String name, @Nonnull Class<T> targetClass) {
        Optional<T> service = delegate.lookupService(name, targetClass);
        if (!service.isPresent()) {
            service = ApplicationContextProvider.getBeanOpt(name, targetClass);
        }
        return service;
    }

    @Override
    @Nonnull
    public <T> Optional<T> lookupService(@Nonnull Class<T> targetClass) {
        Optional<T> service = delegate.lookupService(targetClass);
        if (!service.isPresent()) {
            service = ApplicationContextProvider.getBeanOpt(targetClass);
        }
        return service;
    }

    @Override
    @Nonnull
    public <T> Optional<T> lookupService(@Nonnull String name) {
        Optional<T> service = delegate.lookupService(name);
        if (!service.isPresent()) {
            service = ApplicationContextProvider.getBeanOpt(name);
        }
        return service;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.setup(applicationContext);
    }

    @Nonnull
    @Override
    public SingularInjector lookupSingularInjector() {
        if (injector == null) {
            injector = SingularSpringInjector.get();
        }
        return injector;
    }

    @Nonnull
    @Override
    public Map<String, ServiceEntry> services() {
        return delegate.services();
    }

    @Override
    public <T> void bindService(Class<T> registerClass, RefService<? extends T> provider) {
        delegate.bindService(registerClass, provider);
    }

    @Override
    public <T> void bindService(String serviceName, Class<T> registerClass, RefService<? extends T> provider) {
        delegate.bindService(serviceName, registerClass, provider);
    }
}