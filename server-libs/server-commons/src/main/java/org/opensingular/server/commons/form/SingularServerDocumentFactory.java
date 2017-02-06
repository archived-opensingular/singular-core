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

package org.opensingular.server.commons.form;

import org.opensingular.form.RefService;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.spring.SpringSDocumentFactory;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;


public class SingularServerDocumentFactory extends SpringSDocumentFactory  {

    @Override
    @SuppressWarnings("unchecked")
    protected void setupDocument(SDocument document) {
        document.setAttachmentPersistenceTemporaryHandler(new RefService<IAttachmentPersistenceHandler<? extends IAttachmentRef>>() {
            @Override
            public IAttachmentPersistenceHandler<? extends IAttachmentRef> get() {
                return getServiceRegistry().lookupService(SDocument.FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class);
            }
        });
        document.setAttachmentPersistencePermanentHandler(new RefService<IAttachmentPersistenceHandler<? extends IAttachmentRef>>() {
            @Override
            public IAttachmentPersistenceHandler<? extends IAttachmentRef> get() {
                return getServiceRegistry().lookupService(SDocument.FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class);
            }
        });
    }

}
