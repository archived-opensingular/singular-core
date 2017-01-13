package org.opensingular.form.wicket.mapper.attachment.upload;

import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.lib.commons.util.Loggable;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;


public class UploadPathHandler implements Loggable, Serializable {

    private volatile Path rootPath;

    public Path getLocalFilePath(FileUploadInfo fileInfo) {
        return getLocalFilePath(fileInfo.getAttachmentRef().getId());
    }

    public Path getLocalFilePath(String id) {
        return getOrCreateRootPath().resolve(id);
    }

    public synchronized Path getOrCreateRootPath() {
        if (rootPath == null || !Files.exists(rootPath)) {
            try {
                rootPath = Files.createTempDirectory(UploadPathHandler.class.getSimpleName() + "_");
                rootPath.toFile().deleteOnExit();
            } catch (IOException ex) {
                getLogger().warn(ex.getMessage(), ex);
            }
        }
        return rootPath;
    }

}