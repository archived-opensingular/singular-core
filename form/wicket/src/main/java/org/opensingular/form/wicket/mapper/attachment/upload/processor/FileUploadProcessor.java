package org.opensingular.form.wicket.mapper.attachment.upload.processor;

import org.apache.commons.fileupload.FileItem;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;

import java.io.InputStream;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

public class FileUploadProcessor {

    private final UploadInfo        uploadInfo;
    private final FileUploadManager manager;

    public FileUploadProcessor(UploadInfo uploadInfo, FileUploadManager fileUploadManager) {
        this.uploadInfo = uploadInfo;
        this.manager = fileUploadManager;
    }

    public void processFileItem(List<UploadResponseInfo> response, FileItem item) throws Exception {
        if (!item.isFormField()) {

            final String originalFilename = item.getName();
            final String contentType      = lowerCase(item.getContentType());
            final String extension        = lowerCase(substringAfterLast(originalFilename, "."));

            if (item.getSize() == 0) {
                response.add(new UploadResponseInfo(originalFilename, "Arquivo não pode ser de tamanho 0 (zero)"));
                return;
            }

            if (!(uploadInfo.isFileTypeAllowed(contentType) || uploadInfo.isFileTypeAllowed(extension))) {
                response.add(new UploadResponseInfo(originalFilename, "Tipo de arquivo não permitido"));
                return;
            }

            try (InputStream in = item.getInputStream()) {
                final FileUploadInfo fileInfo = manager.createFile(uploadInfo, originalFilename, in);
                response.add(new UploadResponseInfo(fileInfo.getAttachmentRef()));
            }
        }
    }

}
