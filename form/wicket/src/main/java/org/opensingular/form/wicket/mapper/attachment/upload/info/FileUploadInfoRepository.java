package org.opensingular.form.wicket.mapper.attachment.upload.info;

import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.opensingular.lib.commons.util.Loggable;

import java.util.Optional;
import java.util.stream.Stream;

public class FileUploadInfoRepository implements Loggable {

    private final ConcurrentHashSet<FileUploadInfo> fileUploadInfos;

    public FileUploadInfoRepository() {
        this(new ConcurrentHashSet<>());
    }

    public FileUploadInfoRepository(ConcurrentHashSet<FileUploadInfo> fileUploadInfos) {
        this.fileUploadInfos = fileUploadInfos;
    }

    public synchronized Optional<FileUploadInfo> findByID(String fid) {
        getLogger().debug("findFileInfo({})", fid);
        return fileUploadInfos.stream()
                .filter(it -> it.getAttachmentRef().getId().equals(fid))
                .findAny();
    }

    public boolean add(FileUploadInfo info) {
        return fileUploadInfos.add(info);
    }

    public boolean remove(FileUploadInfo info) {
        return fileUploadInfos.remove(info);
    }

    public Stream<FileUploadInfo> stream() {
        return fileUploadInfos.stream();
    }

}
