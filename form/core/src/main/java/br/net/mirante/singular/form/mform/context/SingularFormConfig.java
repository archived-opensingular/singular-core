package br.net.mirante.singular.form.mform.context;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;

import java.util.Map;

public interface SingularFormConfig<T  extends UIBuilder<K>, K extends UIComponentMapper>  {


    public void setServiceRegistry(ServiceRegistry serviceRegistry);

    /**
     * Factory Method
     *
     * @return
     */
    public SingularFormContext<T, K> getContext();

    public void setCustomMappers(Map<Class<? extends MTipo>, Class<K>> customMappers);

}
