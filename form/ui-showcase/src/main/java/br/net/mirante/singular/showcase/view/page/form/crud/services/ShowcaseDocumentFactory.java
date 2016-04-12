/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.crud.services;

import br.net.mirante.singular.exemplos.notificacaosimplificada.spring.NotificaoSimplificadaSpringConfiguration;
import br.net.mirante.singular.form.mform.core.attachment.handlers.FileSystemAttachmentHandler;
import br.net.mirante.singular.form.spring.SpringServiceRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.spring.SpringSDocumentFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static br.net.mirante.singular.form.mform.RefService.*;

@Component("showcaseDocumentFactory")
public class ShowcaseDocumentFactory extends SpringSDocumentFactory {

    private final static SpringServiceRegistry notificaoSimplificadaAppContext;

    static {
        notificaoSimplificadaAppContext = new SpringServiceRegistry(new AnnotationConfigApplicationContext(NotificaoSimplificadaSpringConfiguration.class));
    }

    @Override
    protected void setupDocument(SDocument document) {
        try {
            document.setAttachmentPersistenceTemporaryHandler(of(newTemporaryHandler()));
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,"Could not create temporary file folder, using memory instead",e);
            document.setAttachmentPersistenceTemporaryHandler(of(new InMemoryAttachmentPersitenceHandler()));
        }
        document.setAttachmentPersistencePermanentHandler(
                of(getServiceRegistry().lookupService(IAttachmentPersistenceHandler.class)));
        document.addServiceRegistry(notificaoSimplificadaAppContext);
    }

    private FileSystemAttachmentHandler newTemporaryHandler() throws IOException {
        return new FileSystemAttachmentHandler(createTemporaryFolder());
    }

    private File createTemporaryFolder() throws IOException {
        File tmpDir = File.createTempFile("singular", "showcase");
        tmpDir.delete();
        tmpDir.mkdir();
        tmpDir.deleteOnExit();
        return tmpDir;
    }
}
