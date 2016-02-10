package br.net.mirante.singular.form.mform.context;

import java.util.Map;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;

public interface SingularFormConfig<T  extends UIBuilder<K>, K extends UIComponentMapper>  {


    public void setServiceRegistry(ServiceRegistry serviceRegistry);

    /**
     * Método factory para criar novo contexto de montagem ou manipulação de
     * formulário.
     */
    public SingularFormContext<T, K> createContext();

    public void setCustomMappers(Map<Class<? extends SType>, Class<K>> customMappers);

}
