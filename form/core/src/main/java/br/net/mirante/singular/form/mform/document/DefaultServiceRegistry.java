package br.net.mirante.singular.form.mform.document;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.SingularFormException;

public class DefaultServiceRegistry implements ServiceRegistry {

    private Map<String, Pair> servicesByName = newHashMap();
    private Map<Class<?>, List<ServiceRef<?>>> servicesByClass = newHashMap();
    private List<ServiceRegistry> registries = newArrayList();

    public void addRegistry(ServiceRegistry r){
        registries.add(r);
    }

    @Override
    public Map<String, Pair> services() {
        return (servicesByName == null) ? Collections.emptyMap() :
                                                ImmutableMap.copyOf(servicesByName);
    }

    @Override
    public <T> T lookupService(Class<T> targetClass) {
        T provider = lookupLocalService(targetClass);
        if(provider != null) return provider;
        return lookupChainedService(targetClass);
    }

    private <T> T lookupChainedService(Class<T> targetClass) {
        for(ServiceRegistry r : registries){
            T provider = r.lookupService(targetClass);
            if(provider != null) return provider;
        }
        return null;
    }

    private <T> T lookupLocalService(Class<T> targetClass) {
        List<ServiceRef<?>> result = findAllMathingProviders(targetClass);
        if(result != null && !result.isEmpty()){
            return verifyResultAndReturn(targetClass, result);
        }
        return null;
    }

    private <T> List<ServiceRef<?>> findAllMathingProviders(Class<T> targetClass) {
        List<ServiceRef<?>> result = newArrayList();
        for(Map.Entry<Class<?>, List<ServiceRef<?>>> entry : servicesByClass.entrySet()){
            if(targetClass.isAssignableFrom(entry.getKey())){
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    private <T> T verifyResultAndReturn(Class<T> targetClass, List<ServiceRef<?>> list) {
        if(list.size() == 1){
            ServiceRef<?> provider = list.get(0);
            return targetClass.cast(provider.get());
        }
        String message = String.format("There are %s options of type %s please be more specific", list.size(), targetClass.getName());
        throw new SingularFormException(message);
    }

    @Override
    public Object lookupService(String name) {
        return lookupService(name, Object.class);
    }

    @Override
    public <T> T lookupService(String name, Class<T> targetClass) {
        T provider = lookupLocalService(name, targetClass);
        if(provider !=  null)   return provider;
        return lookupChainedService(name, targetClass);
    }

    public <T> T lookupLocalService(String name, Class<T> targetClass) {
        Pair ref = servicesByName.get(name);
        if (ref != null) {
            Object value = ref.provider.get();
            if (value == null) {
                servicesByName.remove(name);
            } else if (!targetClass.isInstance(value)) {
                String message = "For service '" + name + "' was found a clas of value "
                    + value.getClass().getName() + " instead of the expected " + targetClass.getName();
                throw new SingularFormException(message);
            } else {
                return targetClass.cast(value);
            }
        }
        return null;
    }

    private <T> T lookupChainedService(String name, Class<T> targetClass) {
        for(ServiceRegistry r : registries){
            T provider = r.lookupService(name, targetClass);;
            if(provider != null) return provider;
        }
        return null;
    }

    /**
     * Registers a {@link ServiceRef}
     *  for the specified class
     * @param registerClass Class of the service the factory provides
     * @param provider provider of the service
     */
    public <T> void bindLocalService(Class<T> registerClass, ServiceRef<? extends T> provider) {
        bindLocalService(UUID.randomUUID().toString(), registerClass, provider);
    }

    private <T> void bindByClass(Class<T> registerClass, ServiceRef<? extends T> provider) {
        List<ServiceRef<?>> list = servicesByClass.get(registerClass);
        if (list == null) {
            list = newArrayList();
            servicesByClass.put(registerClass, list);
        }
        list.add(provider);
    }

    /**
     * Registers a {@link ServiceRef} for the specified class with a unique name.
     * @param serviceName name of the service
     * @param registerClass Class of the service the factory provides
     * @param provider provider of the service
     */
    public <T> void bindLocalService(String serviceName, Class<T> registerClass, ServiceRef<? extends T> provider) {
        bindByName(serviceName, registerClass, provider);
        bindByClass(registerClass, provider);
    }

    private <T> void bindByName(String serviceName, Class<T> registerClass, ServiceRef<?> provider) {
        Objects.requireNonNull(registerClass);
        Objects.requireNonNull(provider);
        servicesByName.put(Objects.requireNonNull(serviceName), new Pair(registerClass, provider));
    }
}
