package br.net.mirante.singular.form.mform.context;

import java.util.Map;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;

public interface SingularFormConfig<K extends UIComponentMapper> {


    public void setServiceRegistry(ServiceRegistry serviceRegistry);

    /**
     * Método factory para criar novo contexto de montagem ou manipulação de
     * formulário.
     */
    public SingularFormContext<K> createContext();

    public void setCustomMappers(Map<Class<? extends SType>, Class<K>> customMappers);

}
