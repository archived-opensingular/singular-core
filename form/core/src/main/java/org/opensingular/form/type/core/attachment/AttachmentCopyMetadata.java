package org.opensingular.form.type.core.attachment;

import java.io.Serializable;

public class AttachmentCopyMetadata<T extends IAttachmentRef> implements Serializable {

    private boolean deleteOldFiles   = true;
    private boolean updateFileId     = true;
    private T       newAttachmentRef = null;

    public AttachmentCopyMetadata(T newAttachmentRef) {
        this.newAttachmentRef = newAttachmentRef;
    }

    public boolean isDeleteOldFiles() {
        return deleteOldFiles;
    }

    public AttachmentCopyMetadata<T> setDeleteOldFiles(boolean deleteOldFiles) {
        this.deleteOldFiles = deleteOldFiles;
        return this;
    }

    public boolean isUpdateFileId() {
        return updateFileId;
    }

    public AttachmentCopyMetadata<T> setUpdateFileId(boolean updateFileId) {
        this.updateFileId = updateFileId;
        return this;
    }

    public T getNewAttachmentRef() {
        return newAttachmentRef;
    }

    public AttachmentCopyMetadata<T> setNewAttachmentRef(T newAttachmentRef) {
        this.newAttachmentRef = newAttachmentRef;
        return this;
    }

}