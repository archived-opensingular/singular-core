package org.opensingular.form.wicket.mapper.attachment.upload.info;

import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.lib.commons.util.Loggable;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

public class UploadInfoRepository implements Loggable, Serializable {

    private final ConcurrentHashSet<UploadInfo> uploadInfos;

    public UploadInfoRepository() {
        this(new ConcurrentHashSet<>());
    }

    public UploadInfoRepository(ConcurrentHashSet<UploadInfo> uploadInfos) {
        this.uploadInfos = uploadInfos;
    }

    public synchronized Optional<UploadInfo> findByAttachmentKey(AttachmentKey attachmentKey) {
        getLogger().debug("findFileInfo({})", attachmentKey);
        return uploadInfos.stream()
                .filter(it -> it.getUploadId().equals(attachmentKey))
                .findAny();
    }

    public boolean add(UploadInfo info) {
        return uploadInfos.add(info);
    }

    public boolean remove(UploadInfo info) {
        return uploadInfos.remove(info);
    }

    public Stream<UploadInfo> stream() {
        return uploadInfos.stream();
    }
}
