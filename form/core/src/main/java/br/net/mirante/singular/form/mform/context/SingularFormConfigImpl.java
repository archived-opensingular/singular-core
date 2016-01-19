package br.net.mirante.singular.form.mform.context;

import br.net.mirante.singular.form.mform.document.ServiceRegistry;

public abstract class SingularFormConfigImpl<T extends UIBuilder<K>, K extends UIComponentMapper> implements SingularFormConfig<T, K>, InternalSingularFormConfig<T, K> {


    private ServiceRegistry serviceRegistry;


    @Override
    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    @Override
    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }


}
