package org.opensingular.form.context;

public class ServiceRegistryLocator {

    private static ServiceRegistryLocator locator = new ServiceRegistryLocator();

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
        return locator;
    }

    public static synchronized void setup(ServiceRegistryLocator locator) {
        ServiceRegistryLocator.locator = new ServiceRegistryLocator(locator);
    }

    public static synchronized void setup(ServiceRegistry serviceRegistry) {
        ServiceRegistryLocator.locator = new ServiceRegistryLocator(serviceRegistry);
    }

    public static ServiceRegistry locate() {
        ServiceRegistryLocator locator = ServiceRegistryLocator.get();
        if (locator == null) {
            throw new SingularServiceRegistryNoFoundException();
        }
        return locator.internalGetRegistry();
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
