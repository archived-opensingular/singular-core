package br.net.mirante.singular.form.mform.document;

import java.io.Serializable;
import java.util.Map;

import br.net.mirante.singular.form.mform.ServiceRef;

/**
 * Service Registry which provides a ḿeans to register and lookup for services.
 * 
 * @author Fabricio Buzeto
 *
 */
public interface ServiceRegistry {

    @SuppressWarnings("serial")
    public static class Pair implements Serializable{
        final public Class<?> type;
        final public ServiceRef<?> provider;
        public Pair(Class<?> type, ServiceRef<?> provider) {
            this.type = type;
            this.provider = provider;
        }
    }
    
    /**
     * List all factories for all registered services;
     * @return factory map.
     */
    Map<String, Pair> services();

    
    /**
     * Tries to find a service based on its class;
     * 
     * @return <code>Null</code> if not found.
     */
    public <T> T lookupService(Class<T> targetClass);
    
    /**
     * Tries to find a service based on its name, casting to the desired type;
     * 
     * @return <code>Null</code> if not found.
     */
    <T> T lookupService(String name, Class<T> targetClass);
    
    /**
     * Tries to find a service based on its name;
     * 
     * @return <code>Null</code> if not found.
     */
    Object lookupService(String name);

    
    //TODO: Bind é sempre local.
    //TODO: Bind por nome, ou classe
    
    /**
     * Registers a service factory based on service class.
     */
    <T> void bindLocalService(Class<T> registerClass, ServiceRef<? extends T> provider);


    /**
     * Registers a service factory based on service name.
     */
    <T> void bindLocalService(String serviceName, Class<T> registerClass, ServiceRef<?> provider);

}
