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

package org.opensingular.form.type.core.attachment.helper;


import org.opensingular.form.SInstances;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.type.core.attachment.AttachmentCopyContext;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.SIAttachment;

import java.util.Objects;

public class DefaultAttachmentPersistenceHelper implements IAttachmentPersistenceHelper {


    @Override
    public void doPersistence(SDocument document,
                              IAttachmentPersistenceHandler temporaryHandler,
                              IAttachmentPersistenceHandler persistenceHandler) {
        SInstances.visit(document.getRoot(), (instance, visit) -> {
            if (instance instanceof SIAttachment) {
                handleAttachment((SIAttachment) instance, temporaryHandler, persistenceHandler);
            }
        });
    }

    public void handleAttachment(SIAttachment attachment,
                                  IAttachmentPersistenceHandler temporaryHandler,
                                  IAttachmentPersistenceHandler persistenceHandler) {

        String originalFileId = attachment.getOriginalFileId();
        if (!Objects.equals(attachment.getFileId(), originalFileId)) {

            IAttachmentRef fileRef = temporaryHandler.getAttachment(attachment.getFileId());

            if (fileRef != null) {

                AttachmentCopyContext attachmentCopyContext;
                IAttachmentRef        newRef;

                attachmentCopyContext = persistenceHandler.copy(fileRef, attachment.getDocument());
                newRef = attachmentCopyContext.getNewAttachmentRef();

                if (attachmentCopyContext.isDeleteOldFiles()) {
                    deleteOldFiles(attachment, fileRef, temporaryHandler, persistenceHandler);
                }

                if (attachmentCopyContext.isUpdateFileId()) {
                    updateFileId(attachment, newRef);
                }

            } else if (originalFileId != null) {
                persistenceHandler.deleteAttachment(originalFileId, attachment.getDocument());
            }
        }
    }


    private void deleteOldFiles(SIAttachment attachment,
                                IAttachmentRef fileRef,
                                IAttachmentPersistenceHandler temporaryHandler,
                                IAttachmentPersistenceHandler persistenceHandler) {

        temporaryHandler.deleteAttachment(fileRef.getId(), attachment.getDocument());

        if (attachment.getOriginalFileId() != null) {
            persistenceHandler.deleteAttachment(attachment.getOriginalFileId(), attachment.getDocument());
        }
    }

    private void updateFileId(SIAttachment attachment, IAttachmentRef newRef) {
        attachment.setFileId(newRef.getId());
        attachment.setOriginalFileId(newRef.getId());
    }


}