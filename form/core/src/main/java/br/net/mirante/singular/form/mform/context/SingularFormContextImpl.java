package br.net.mirante.singular.form.mform.context;

import br.net.mirante.singular.form.mform.document.ServiceRegistry;

public abstract class SingularFormContextImpl<T extends UIBuilder<K>, K extends UIComponentMapper> implements SingularFormContext<T, K> {

    private InternalSingularFormConfig<T, K> config;


    public SingularFormContextImpl(InternalSingularFormConfig<T, K> config) {
        this.config = config;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return config.getServiceRegistry();
    }
}
