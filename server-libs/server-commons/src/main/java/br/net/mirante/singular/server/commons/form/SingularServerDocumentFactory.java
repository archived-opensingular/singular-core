package br.net.mirante.singular.server.commons.form;

import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.spring.SpringSDocumentFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static br.net.mirante.singular.form.mform.RefService.of;
import static br.net.mirante.singular.form.mform.core.attachment.handlers.FileSystemAttachmentHandler.newTemporaryHandler;

public class SingularServerDocumentFactory extends SpringSDocumentFactory {

    @Override
    protected void setupDocument(SDocument document) {
        setupTemporaryHandles(document);
        document.setAttachmentPersistencePermanentHandler(
                RefService.of(getServiceRegistry().lookupService(IAttachmentPersistenceHandler.class)));
    }

    private void setupTemporaryHandles(SDocument document) {
        try {
            document.setAttachmentPersistenceTemporaryHandler(of(newTemporaryHandler()));
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,"Could not create temporary file folder, using memory instead",e);
            document.setAttachmentPersistenceTemporaryHandler(of(new InMemoryAttachmentPersitenceHandler()));
        }
    }

}
