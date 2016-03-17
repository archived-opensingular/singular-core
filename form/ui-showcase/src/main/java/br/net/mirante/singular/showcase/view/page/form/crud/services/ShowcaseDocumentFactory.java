package br.net.mirante.singular.showcase.view.page.form.crud.services;

import org.springframework.stereotype.Component;

import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.spring.SpringSDocumentFactory;

@Component("showcaseDocumentFactory")
public class ShowcaseDocumentFactory extends SpringSDocumentFactory {

    @Override
    protected void setupDocument(SDocument document) {
        document.setAttachmentPersistenceTemporaryHandler(RefService.of(new InMemoryAttachmentPersitenceHandler()));
        document.setAttachmentPersistencePermanentHandler(
                RefService.of(getServiceRegistry().lookupService(IAttachmentPersistenceHandler.class)));
    }
}
