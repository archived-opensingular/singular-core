/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.singular.form.showcase.view.page.form.crud.services;

import org.opensingular.form.exemplos.notificacaosimplificada.spring.NotificaoSimplificadaSpringConfiguration;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.spring.SpringSDocumentFactory;
import org.opensingular.form.spring.SpringServiceRegistry;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.opensingular.form.RefService.of;
import static org.opensingular.form.type.core.attachment.handlers.FileSystemAttachmentHandler.newTemporaryHandler;

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
