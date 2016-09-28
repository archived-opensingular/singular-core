/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.crud.services;

import br.net.mirante.singular.exemplos.notificacaosimplificada.spring.NotificaoSimplificadaSpringConfiguration;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.spring.SpringSDocumentFactory;
import br.net.mirante.singular.form.spring.SpringServiceRegistry;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static br.net.mirante.singular.form.RefService.of;
import static br.net.mirante.singular.form.type.core.attachment.handlers.FileSystemAttachmentHandler.newTemporaryHandler;

@Component("showcaseDocumentFactory")
public class ShowcaseDocumentFactory extends SpringSDocumentFactory {

    private final static SpringServiceRegistry NOTIFICACAO_SIMPLIFICADA_SPRING_CONFIG;

    static {
        NOTIFICACAO_SIMPLIFICADA_SPRING_CONFIG = new SpringServiceRegistry(new AnnotationConfigApplicationContext(NotificaoSimplificadaSpringConfiguration.class));
    }

    @Override
    protected void setupDocument(SDocument document) {
        try {
            document.setAttachmentPersistenceTemporaryHandler(of(newTemporaryHandler()));
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Could not create temporary file folder, using memory instead", e);
            document.setAttachmentPersistenceTemporaryHandler(of(new InMemoryAttachmentPersitenceHandler()));
        }
        document.setAttachmentPersistencePermanentHandler(
                of(getServiceRegistry().lookupService(IAttachmentPersistenceHandler.class)));
        document.addServiceRegistry(NOTIFICACAO_SIMPLIFICADA_SPRING_CONFIG);
    }


}
