package br.net.mirante.singular.form.mform.context;

import br.net.mirante.singular.form.mform.document.ServiceRegistry;

public abstract class SingularFormContextImpl implements SingularFormContext {

    private InternalSingularFormConfig config;


    public SingularFormContextImpl(InternalSingularFormConfig config) {
        this.config = config;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return config.getServiceRegistry();
    }
}
