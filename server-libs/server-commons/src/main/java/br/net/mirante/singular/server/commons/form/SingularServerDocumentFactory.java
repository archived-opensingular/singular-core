package br.net.mirante.singular.server.commons.form;

import org.opensingular.singular.form.RefService;
import org.opensingular.singular.form.document.SDocument;
import br.net.mirante.singular.form.spring.SpringSDocumentFactory;
import org.opensingular.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.singular.form.type.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.opensingular.singular.form.RefService.of;
import static org.opensingular.singular.form.type.core.attachment.handlers.FileSystemAttachmentHandler.newTemporaryHandler;

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
