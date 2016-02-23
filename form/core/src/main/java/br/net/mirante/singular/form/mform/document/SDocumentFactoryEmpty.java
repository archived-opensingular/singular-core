package br.net.mirante.singular.form.mform.document;

/**
 * Representa uma factory que não faz nada com o documento.
 *
 * @author Daniel C. Bordin
 */
final class SDocumentFactoryEmpty extends SDocumentFactory {

    @Override
    public RefSDocumentFactory getDocumentFactoryRef() {
        return new RefEmptySDocumentFactory();
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return null;
    }

    @Override
    protected void setupDocument(SDocument document) {
    }

    private static final class RefEmptySDocumentFactory extends RefSDocumentFactory {

        @Override
        protected SDocumentFactory retrieve() {
            return new SDocumentFactoryEmpty();
        }
    }
}
