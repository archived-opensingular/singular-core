package br.net.mirante.singular.form.mform.context;

import br.net.mirante.singular.form.mform.document.ServiceRegistry;

public abstract class SingularFormContextImpl<K extends UIComponentMapper> implements SingularFormContext<K> {

    private InternalSingularFormConfig<K> config;


    public SingularFormContextImpl(InternalSingularFormConfig<K> config) {
        this.config = config;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return config.getServiceRegistry();
    }
}
