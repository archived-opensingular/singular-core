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

    private void handleAttachment(SIAttachment attachment,
                                  IAttachmentPersistenceHandler temporaryHandler,
                                  IAttachmentPersistenceHandler persistenceHandler) {

        if (!Objects.equals(attachment.getFileId(), attachment.getOriginalFileId())) {

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

            } else if (attachment.getOriginalFileId() != null) {
                persistenceHandler.deleteAttachment(attachment.getOriginalFileId(), attachment.getDocument());
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