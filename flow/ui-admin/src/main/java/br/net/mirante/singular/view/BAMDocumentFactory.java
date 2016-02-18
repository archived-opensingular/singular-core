package br.net.mirante.singular.view;

import org.springframework.stereotype.Component;

import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.spring.SpringSDocumentFactory;

@Component
public class BAMDocumentFactory extends SpringSDocumentFactory {

    @Override
    protected void setupDocument(SDocument document) {
        document.setAttachmentPersistenceTemporaryHandler(ServiceRef.of(new InMemoryAttachmentPersitenceHandler()));
    }
}
