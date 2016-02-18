package br.net.mirante.singular.showcase.view.page.form.crud.services;

import org.springframework.stereotype.Component;

import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.spring.SpringSDocumentFactory;

@Component
public class ShowcaseDocumentFactory extends SpringSDocumentFactory {

    @Override
    protected void setupDocument(SDocument document) {
        document.setAttachmentPersistenceTemporaryHandler(ServiceRef.of(new InMemoryAttachmentPersitenceHandler()));
        document.setAttachmentPersistencePermanentHandler(
                ServiceRef.of(getServiceRegistry().lookupService(IAttachmentPersistenceHandler.class)));
    }
}
