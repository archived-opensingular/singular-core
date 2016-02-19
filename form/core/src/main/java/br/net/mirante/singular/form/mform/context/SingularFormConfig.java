package br.net.mirante.singular.form.mform.context;

import br.net.mirante.singular.form.mform.document.ServiceRegistry;

public interface SingularFormConfig {


    public void setServiceRegistry(ServiceRegistry serviceRegistry);

    /**
     * Método factory para criar novo contexto de montagem ou manipulação de
     * formulário.
     */
    public SingularFormContext createContext();

}
