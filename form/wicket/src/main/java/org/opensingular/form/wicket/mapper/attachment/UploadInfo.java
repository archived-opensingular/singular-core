package org.opensingular.form.wicket.mapper.attachment;

import static org.apache.commons.lang3.ObjectUtils.*;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONWriter;
import org.opensingular.form.servlet.MimeTypes;

import com.google.common.collect.ImmutableSet;

final class UploadInfo implements Serializable {

    final UUID            uploadId;
    final long            maxFileSize;
    final int             maxFileCount;
    final Set<String>     allowedFileTypes;
    private volatile long lastAccess;
    public UploadInfo(
        UUID uploadId,
        long maxFileSize,
        int maxFileCount,
        Collection<String> allowedFileTypes) {

        this.uploadId = uploadId;
        this.maxFileSize = Math.max(1L, maxFileSize);
        this.maxFileCount = Math.max(1, maxFileCount);
        this.allowedFileTypes = toSet(allowedFileTypes);
        this.touch();
    }

    public boolean isFileTypeAllowed(String mimeTypeOrExtension) {
        return allowedFileTypes.contains(mimeTypeOrExtension)
            || allowedFileTypes.contains(MimeTypes.getExtensionForMimeType(mimeTypeOrExtension))
            || allowedFileTypes.contains(MimeTypes.getMimeTypeForExtension(mimeTypeOrExtension));
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
        JSONWriter writer = new JSONWriter(buffer);
        //@formatter:off
        writer.object()
            .key("uploadId"         ).value(uploadId.toString())
            .key("maxFileSize"      ).value(maxFileSize)
            .key("maxFileCount"     ).value(maxFileCount)
            .key("allowedFileTypes" ).value(new JSONArray(allowedFileTypes))
            .key("lastAccess"       ).value(lastAccess)
            .endObject();
        //@formatter:on
        return buffer.toString();
    }
}