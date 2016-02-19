package br.net.mirante.singular.form.mform.context;

import br.net.mirante.singular.form.mform.document.ServiceRegistry;

/**
 * Interface de uso interno para acessar os valores configurados no SingularFormConfig
 * @param <T>
 * @param <K>
 */
public interface InternalSingularFormConfig extends SingularFormConfig {


    public ServiceRegistry getServiceRegistry();

}
