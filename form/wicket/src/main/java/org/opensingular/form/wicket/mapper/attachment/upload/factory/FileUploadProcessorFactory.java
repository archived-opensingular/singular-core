package org.opensingular.form.wicket.mapper.attachment.upload.factory;

import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.processor.FileUploadProcessor;

import java.io.Serializable;

public class FileUploadProcessorFactory implements Serializable {

    public FileUploadProcessor get(UploadInfo uploadInfo, FileUploadManager fileUploadManager) {
        return new FileUploadProcessor(uploadInfo, fileUploadManager);
    }

}