package br.net.mirante.singular.form.spring;

import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.document.SDocumentFactoryRef;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;

/**
 * Representa uma factory que não faz nada com o documento e que aponta o
 * registro ({@link #getServiceRegistry()}) para o Spring.
 *
 * @author Daniel C. Bordin
 */
public class SpringSDocumentFactoryEmpty extends SDocumentFactory {

    @Override
    public SDocumentFactoryRef getDocumentFactoryRef() {
        return new SDocumentFactoryRefEmpty();
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return new SpringServiceRegistry(SpringFormUtil.getApplicationContext());
    }

    @Override
    protected void setupDocument(SDocument document) {
    }

    private static final class SDocumentFactoryRefEmpty extends SDocumentFactoryRef {

        @Override
        protected SDocumentFactory reloadFactory() {
            return new SpringSDocumentFactoryEmpty();
        }
    }
}
