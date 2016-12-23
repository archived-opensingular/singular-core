package org.opensingular.form.wicket.mapper.attachment.upload.processor;

import org.apache.commons.fileupload.FileItem;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo.*;

public class FileUploadProcessor {


    public List<UploadResponseInfo> process(FileItem item, UploadInfo upInfo, FileUploadManager upManager) throws Exception {

        final List<UploadResponseInfo> responses = new ArrayList<>();

        if (!item.isFormField()) {

            final String originalFilename = item.getName();
            final String contentType      = lowerCase(item.getContentType());
            final String extension        = lowerCase(substringAfterLast(originalFilename, "."));

            if (item.getSize() == 0) {
                responses.add(new UploadResponseInfo(originalFilename, ARQUIVO_NAO_PODE_SER_DE_TAMANHO_0_ZERO));

            } else if (!(upInfo.isFileTypeAllowed(contentType) || upInfo.isFileTypeAllowed(extension))) {
                responses.add(new UploadResponseInfo(originalFilename, TIPO_DE_ARQUIVO_NAO_PERMITIDO));

            } else {
                try (InputStream in = item.getInputStream()) {
                    final FileUploadInfo fileInfo = upManager.createFile(upInfo, originalFilename, in);
                    responses.add(new UploadResponseInfo(fileInfo.getAttachmentRef()));
                }
            }
        }

        return responses;
    }

}
