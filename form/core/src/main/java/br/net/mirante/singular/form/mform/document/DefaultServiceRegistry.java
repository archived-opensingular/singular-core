package br.net.mirante.singular.form.mform.document;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.SingularFormException;

public class DefaultServiceRegistry implements ServiceRegistry {

    private Map<String, ServiceRef<?>> services;
    
    
    @Override
    public Map<String, ServiceRef<?>> services() {
        return (services == null) ? Collections.emptyMap() : 
                                                ImmutableMap.copyOf(services);
    }
    
    @Override
    public <T> T lookupLocalService(Class<T> targetClass, String subName) {
        return lookupLocalService(toLookupName(targetClass, subName), targetClass);
    }
    
    @Override
    public <T> T lookupLocalService(String name, Class<T> targetClass) {
        if (services != null) {
            ServiceRef<?> ref = services.get(name);
            if (ref != null) {
                Object value = ref.get();
                if (value == null) {
                    services.remove(name);
                } else if (!targetClass.isInstance(value)) {
                    throw new SingularFormException("Para o serviço '" + name + "' foi encontrado um valor da classe "
                        + value.getClass().getName() + " em vez da classe esperada " + targetClass.getName());
                } else {
                    return targetClass.cast(value);
                }
            }
        }
        return null;
    }
    
    @Override
    public <T> void bindLocalService(Class<T> registerClass, ServiceRef<? extends T> provider) {
        bindLocalService(registerClass.getName(), provider);
    }
    
    @Override
    public <T> void bindLocalService(Class<T> registerClass, String subName, ServiceRef<? extends T> provider) {
        bindLocalService(toLookupName(registerClass, subName), provider);
    }
    
    private static <T> String toLookupName(Class<T> registerClass, String subName) {
        return registerClass.getName() + ":" + Objects.requireNonNull(subName);
    }
    
    @Override
    public void bindLocalService(String serviceName, ServiceRef<?> provider) {
        if (services == null) {
            services = new HashMap<>();
        }
        services.put(Objects.requireNonNull(serviceName), Objects.requireNonNull(provider));
    }
}
