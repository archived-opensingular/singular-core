package org.opensingular.form.wicket.mapper.attachment;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.sun.istack.internal.NotNull;
import org.json.JSONObject;

import com.google.common.collect.ComparisonChain;
import org.opensingular.form.type.core.attachment.IAttachmentRef;

public class FileUploadInfo implements Serializable, Comparable<FileUploadInfo> {

    public final IAttachmentRef attachmentRef;

    public FileUploadInfo(IAttachmentRef attachmentRef) {
        this.attachmentRef = attachmentRef;
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("fileId", attachmentRef.getId())
                .put("name", attachmentRef.getName())
                .put("hashSHA1", attachmentRef.getHashSHA1())
                .put("size", attachmentRef.getSize());
    }

    public IAttachmentRef getAttachmentRef() {
        return attachmentRef;
    }

    @Override
    public boolean equals(Object o) {
        return this == o
                || !(o == null || getClass() != o.getClass())
                && Objects.equals(attachmentRef, ((FileUploadInfo) o).attachmentRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attachmentRef);
    }

    @Override
    public int compareTo(FileUploadInfo o) {
        if (attachmentRef != null && o.attachmentRef.getId() != null) {
            return ComparisonChain.start()
                    .compare(this.attachmentRef.getId(), o.attachmentRef.getId())
                    .result();
        }
        if (attachmentRef != null && o.attachmentRef.getId() == null) {
            return 1;
        }
        if (attachmentRef == null && o.attachmentRef.getId() != null) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return toJSON().toString(2);
    }
}
