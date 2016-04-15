/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.crud.services;

import br.net.mirante.singular.exemplos.notificacaosimplificada.spring.NotificaoSimplificadaSpringConfiguration;
import br.net.mirante.singular.form.spring.SpringServiceRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.spring.SpringSDocumentFactory;

@Component("showcaseDocumentFactory")
public class ShowcaseDocumentFactory extends SpringSDocumentFactory {

    private final static SpringServiceRegistry notificaoSimplificadaAppContext;

    static {
        notificaoSimplificadaAppContext = new SpringServiceRegistry(new AnnotationConfigApplicationContext(NotificaoSimplificadaSpringConfiguration.class));
    }

    @Override
    protected void setupDocument(SDocument document) {
        document.setAttachmentPersistenceTemporaryHandler(RefService.of(new InMemoryAttachmentPersitenceHandler()));
        document.setAttachmentPersistencePermanentHandler(
                RefService.of(getServiceRegistry().lookupService(IAttachmentPersistenceHandler.class)));
        document.addServiceRegistry(notificaoSimplificadaAppContext);
    }
}
