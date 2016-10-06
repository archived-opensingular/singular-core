package org.opensingular.form.document;

import org.opensingular.singular.commons.lambda.IConsumer;

import java.util.Objects;

/**
 *
 * @author Daniel C. Bordin
 */
final class SDocumentExtended extends SDocumentFactory {

    private final SDocumentFactory original;
    private final IConsumer<SDocument> extraSetupStep;
    private RefSDocumentFactoryExtended ref;

    public SDocumentExtended(SDocumentFactory original, IConsumer<SDocument> extraSetupStep) {
        this.original = Objects.requireNonNull(original);
        this.extraSetupStep = Objects.requireNonNull(extraSetupStep);
    }

    @Override
    public RefSDocumentFactory getDocumentFactoryRef() {
        if (ref == null) {
            ref = new RefSDocumentFactoryExtended(this);
        }
        return ref;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return original.getServiceRegistry();
    }

    @Override
    protected void setupDocument(SDocument document) {
        original.setupDocument(document);
        extraSetupStep.accept(document);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "( extend  factory " + original + ")";
    }

    /**
     * Referência serializável para a {@link RefSDocumentFactoryExtended}
     */
    private static final class RefSDocumentFactoryExtended extends RefSDocumentFactory {

        private final RefSDocumentFactory refOriginalFactory;
        private final IConsumer<SDocument> extraSetupStep;

        public RefSDocumentFactoryExtended(SDocumentExtended documentFactory) {
            super(documentFactory);
            this.refOriginalFactory = documentFactory.original.getDocumentFactoryRef();
            this.extraSetupStep = documentFactory.extraSetupStep;
        }

        @Override
        protected SDocumentFactory retrieve() {
            SDocumentFactory original = refOriginalFactory.get();
            return new SDocumentExtended(original, extraSetupStep);
        }
    }
}
