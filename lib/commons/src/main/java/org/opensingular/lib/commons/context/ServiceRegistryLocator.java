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

package org.opensingular.lib.commons.context;

public class ServiceRegistryLocator {

    private ServiceRegistryLocator delegate;

    private ServiceRegistry serviceRegistry;

    public ServiceRegistryLocator() {
        this.serviceRegistry = new DefaultServiceRegistry();
    }

    public ServiceRegistryLocator(ServiceRegistryLocator delegate) {
        this.delegate = delegate;
    }

    public ServiceRegistryLocator(ServiceRegistry registry) {
        this.serviceRegistry = registry;
    }

    private static ServiceRegistryLocator get() {
        return ((SingularSingletonStrategy) SingularContext.get()).singletonize(ServiceRegistryLocator.class, ServiceRegistryLocator::new);
    }

    public static synchronized void setup(ServiceRegistryLocator locator) {
        ((SingularSingletonStrategy) SingularContext.get()).put(ServiceRegistryLocator.class, new ServiceRegistryLocator(locator));
    }

    public static synchronized void setup(ServiceRegistry serviceRegistry) {
        ((SingularSingletonStrategy) SingularContext.get()).put(ServiceRegistryLocator.class, new ServiceRegistryLocator(serviceRegistry));
    }

    public static ServiceRegistry locate() {
        return ServiceRegistryLocator.get().internalGetRegistry();
    }

    private ServiceRegistry internalGetRegistry() {
        ServiceRegistry registry = this.getRegistry();
        if (registry == null && delegate != null) {
            registry = delegate.getRegistry();
        }
        return registry;
    }

    protected ServiceRegistry getRegistry() {
        return serviceRegistry;
    }
}
