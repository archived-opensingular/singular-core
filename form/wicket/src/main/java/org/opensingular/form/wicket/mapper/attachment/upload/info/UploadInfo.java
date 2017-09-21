package org.opensingular.form.wicket.mapper.attachment.upload.info;

import com.google.common.collect.ImmutableSet;
import org.json.JSONArray;
import org.json.JSONWriter;
import org.opensingular.form.servlet.MimeTypes;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.TemporaryAttachmentPersistenceHandlerSupplier;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class UploadInfo implements Serializable {

    private final AttachmentKey                                 uploadId;
    private final long                                          maxFileSize;
    private final int                                           maxFileCount;
    private final Set<String>                                   allowedFileTypes;
    private final TemporaryAttachmentPersistenceHandlerSupplier persistenceHandlerSupplier;

    private volatile long lastAccess;

    public UploadInfo(
            AttachmentKey uploadId,
            long maxFileSize,
            int maxFileCount,
            Collection<String> allowedFileTypes,
            TemporaryAttachmentPersistenceHandlerSupplier persistenceHandlerSupplier) {

        this.uploadId = uploadId;
        this.maxFileSize = Math.max(1L, maxFileSize);
        this.maxFileCount = Math.max(1, maxFileCount);
        this.allowedFileTypes = toSet(allowedFileTypes);
        this.persistenceHandlerSupplier = persistenceHandlerSupplier;
        this.touch();
    }

    public boolean isFileTypeAllowed(String mimeTypeOrExtension) {
        return allowedFileTypes.isEmpty()
                || allowedFileTypes.contains(mimeTypeOrExtension)
                || allowedFileTypes.contains(MimeTypes.getExtensionForMimeType(mimeTypeOrExtension))
                || allowedFileTypes.contains(MimeTypes.getMimeTypeForExtension(mimeTypeOrExtension));
    }

    public Set<String> getAllowedFileExtensions() {
        return MimeTypes.getExtensionsFormMimeTypes(getAllowedFileTypes(), true);
    }


    public long lastAccess() {
        return lastAccess;
    }

    public UploadInfo touch() {
        this.lastAccess = System.currentTimeMillis();
        return this;
    }

    private ImmutableSet<String> toSet(Collection<String> collection) {
        return ImmutableSet.copyOf(defaultIfNull(collection, Collections.emptyList()));
    }

    @Override
    public String toString() {
        StringWriter buffer = new StringWriter();
        JSONWriter   writer = new JSONWriter(buffer);
        //@formatter:off
        writer.object()
                .key("uploadId").value(uploadId)
                .key("maxFileSize").value(maxFileSize)
                .key("maxFileCount").value(maxFileCount)
                .key("allowedFileTypes").value(new JSONArray(allowedFileTypes))
                .key("lastAccess").value(lastAccess)
                .endObject();
        //@formatter:on
        return buffer.toString();
    }

    public AttachmentKey getUploadId() {
        return uploadId;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public int getMaxFileCount() {
        return maxFileCount;
    }

    public Set<String> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public UploadInfo setLastAccess(long lastAccess) {
        this.lastAccess = lastAccess;
        return this;
    }

    public TemporaryAttachmentPersistenceHandlerSupplier getPersistenceHandlerSupplier() {
        return persistenceHandlerSupplier;
    }
}