package br.net.mirante.singular.form.mform.document;

import java.util.Map;

import br.net.mirante.singular.form.mform.ServiceRef;

/**
 * Service Registry which provides a ḿeans to register and lookup for services.
 * 
 * @author Fabricio Buzeto
 *
 */
public interface ServiceRegistry {

    /**
     * List all factories for all registered services;
     * @return factory map.
     */
    Map<String, ServiceRef<?>> services();

    /**
     * Tries to find a service based on its class and a specified surname;
     * 
     * @return <code>Null</code> if not found.
     */
    <T> T lookupLocalService(Class<T> targetClass, String subName);

    /**
     * Tries to find a service based on its class;
     * 
     * @return <code>Null</code> if not found.
     */
    default public <T> T lookupLocalService(Class<T> targetClass) {
        return lookupLocalService(targetClass.getName(), targetClass);
    }
    
    /**
     * Tries to find a service based on its name;
     * 
     * @return <code>Null</code> if not found.
     */
    <T> T lookupLocalService(String name, Class<T> targetClass);

    /**
     * Registers a service factory based on service class.
     */
    <T> void bindLocalService(Class<T> registerClass, ServiceRef<? extends T> provider);

    /**
     * Registers a service factory based on service class and a surname.
     */
    <T> void bindLocalService(Class<T> registerClass, String subName, ServiceRef<? extends T> provider);

    /**
     * Registers a service factory based on service name.
     */
    void bindLocalService(String serviceName, ServiceRef<?> provider);

}
