package br.net.mirante.singular.form.mform.context;

import br.net.mirante.singular.form.mform.document.ServiceRegistry;

public interface SingularFormContext<T  extends UIBuilder<K>, K extends UIComponentMapper> {

    public ServiceRegistry getServiceRegistry();

    public T getUIBuilder();

}
