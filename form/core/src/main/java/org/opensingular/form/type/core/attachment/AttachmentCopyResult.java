package org.opensingular.form.type.core.attachment;

import java.io.Serializable;

public class AttachmentCopyResult<T extends IAttachmentRef> implements Serializable {

    private boolean deleteOldFiles   = true;
    private boolean updateFileId     = true;
    private T       newAttachmentRef = null;

    public AttachmentCopyResult(T newAttachmentRef) {
        this.newAttachmentRef = newAttachmentRef;
    }

    public boolean isDeleteOldFiles() {
        return deleteOldFiles;
    }

    public AttachmentCopyResult setDeleteOldFiles(boolean deleteOldFiles) {
        this.deleteOldFiles = deleteOldFiles;
        return this;
    }

    public boolean isUpdateFileId() {
        return updateFileId;
    }

    public AttachmentCopyResult setUpdateFileId(boolean updateFileId) {
        this.updateFileId = updateFileId;
        return this;
    }

    public IAttachmentRef getNewAttachmentRef() {
        return newAttachmentRef;
    }

    public AttachmentCopyResult setNewAttachmentRef(T newAttachmentRef) {
        this.newAttachmentRef = newAttachmentRef;
        return this;
    }

}