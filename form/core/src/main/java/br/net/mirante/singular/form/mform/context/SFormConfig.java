package br.net.mirante.singular.form.mform.context;

import java.io.Serializable;

import br.net.mirante.singular.form.mform.SDictionaryLoader;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;

/**
 * Representa a configuração para funcionamento do formulário.
 *
 * @author Daniel C. Bordin
 */
public interface SFormConfig<KEY extends Serializable> {

    public SDocumentFactory getDocumentFactory();

    public SDictionaryLoader<KEY> getDictionaryLoader();

    public default ServiceRegistry getServiceRegistry() {
        return getDocumentFactory().getServiceRegistry();
    }
}
