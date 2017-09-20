package org.opensingular.form.context;

import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;

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
