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
    
    @Override
    public Map<String, Pair> services() {
        return (servicesByName == null) ? Collections.emptyMap() : 
                                                ImmutableMap.copyOf(servicesByName);
    }
    
    @Override
    public <T> T lookupService(Class<T> targetClass) {
        List<ServiceRef<?>> result = findAllMathingProviders(targetClass);
        if(result != null && !result.isEmpty()){
            return verifyResultAndReturn(targetClass, result);
        }
        return null;
    }

    private <T> List<ServiceRef<?>> findAllMathingProviders(Class<T> targetClass) {
        List<ServiceRef<?>> result = newArrayList();
        for(Class<?> key : servicesByClass.keySet()){
            if(targetClass.isAssignableFrom(key)){
                result.addAll(servicesByClass.get(key));
            }
        }
        return result;
    }

    private <T> T verifyResultAndReturn(Class<T> targetClass, List<ServiceRef<?>> list) {
        if(list.size() == 1){
            return handleCompatibleProviderFound(targetClass, list);
        }
        throw createMultipleOptionsError(targetClass, list);
    }

    private <T> T handleCompatibleProviderFound(Class<T> targetClass, List<ServiceRef<?>> list) {
        ServiceRef<?> provider = list.get(0);
        return targetClass.cast(provider.get());
    }

    private <T> RuntimeException createMultipleOptionsError(Class<T> targetClass, List<ServiceRef<?>> list) {
        String message =
            String.format(
            "There are %s options of type %s please be more specific",
            list.size(),  targetClass.getName() );
        return new RuntimeException(message);
    }
    
    @Override
    public Object lookupService(String name) {
        return lookupService(name, Object.class);
    }
    
    @Override
    public <T> T lookupService(String name, Class<T> targetClass) {
        Pair ref = servicesByName.get(name);
        if (ref != null) {
            Object value = ref.provider.get();
            if (value == null) {
                servicesByName.remove(name);
            } else if (!targetClass.isInstance(value)) {
                throw new SingularFormException("Para o serviço '" + name + "' foi encontrado um valor da classe "
                    + value.getClass().getName() + " em vez da classe esperada " + targetClass.getName());
            } else {
                return targetClass.cast(value);
            }
        }
        return null;
    }
    
    @Override
    public <T> void bindLocalService(Class<T> registerClass, ServiceRef<? extends T> provider) {
        bindByName(UUID.randomUUID().toString(), registerClass, provider);
        bindByClass(registerClass, provider);
    }
    
    private <T> void bindByClass(Class<T> registerClass, ServiceRef<?> provider) {
        createClassListIfNeeded(registerClass);
        List<ServiceRef<?>> list = servicesByClass.get(registerClass);
        list.add(provider);
    }

    private <T> void createClassListIfNeeded(Class<T> registerClass) {
        if(!servicesByClass.containsKey(registerClass)){
            servicesByClass.put(registerClass, newArrayList());
        }
    }
    
    @Override
    public <T> void bindLocalService(String serviceName, Class<T> registerClass, 
            ServiceRef<?> provider) {
        bindByName(serviceName, registerClass, provider);
        bindByClass(registerClass, provider);
    }

    private <T> void bindByName(String serviceName, Class<T> registerClass, 
            ServiceRef<?> provider) {
        servicesByName.put(Objects.requireNonNull(serviceName), 
            Objects.requireNonNull(new Pair(registerClass, provider)));
    }

}
