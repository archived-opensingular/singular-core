package org.opensingular.form.wicket.mapper.attachment.upload;

import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.lib.commons.util.Loggable;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;


public class UploadPathHandler implements Loggable, Serializable {

    private File rootFile;

    public Path getLocalFilePath(FileUploadInfo fileInfo) {
        return getLocalFilePath(fileInfo.getAttachmentRef().getId());
    }

    public Path getLocalFilePath(String id) {
        return getOrCreateRootPath().resolve(id);
    }

    public synchronized Path getOrCreateRootPath() {
        if (rootFile == null || !rootFile.exists()) {
            try {
                Path rootPath = Files.createTempDirectory(UploadPathHandler.class.getSimpleName() + "_");
                rootFile = rootPath.toFile();
                rootFile.deleteOnExit();
            } catch (IOException ex) {
                getLogger().warn(ex.getMessage(), ex);
            }
        }
        return rootFile.toPath();
    }

}