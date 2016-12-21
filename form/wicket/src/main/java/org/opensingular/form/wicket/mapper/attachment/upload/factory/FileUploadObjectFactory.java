package org.opensingular.form.wicket.mapper.attachment.upload.factory;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.config.FileUploadConfig;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.processor.FileUploadProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

public class FileUploadObjectFactory {

    public FileUploadProcessor newFileUploadProcessor(HttpServletRequest req, HttpServletResponse resp,
                                                      UploadInfo uploadInfo, FileUploadManager fileUploadManager,
                                                      FileUploadObjectFactory fileUploadObjectFactory) {
        return new FileUploadProcessor(uploadInfo, req, resp, fileUploadManager, fileUploadObjectFactory);
    }

    public ServletFileUpload newServletFileUpload(FileUploadConfig config, UploadInfo uploadInfo) {
        final ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());

        servletFileUpload.setFileSizeMax(
                resolveMax(uploadInfo.getMaxFileSize(), config.defaultMaxFileSize, config.globalMaxFileSize)
        );

        servletFileUpload.setSizeMax(
                resolveMax(uploadInfo.getMaxFileSize() * uploadInfo.getMaxFileCount(),
                        config.defaultMaxRequestSize,
                        config.globalMaxRequestSize
                )
        );

        return servletFileUpload;
    }

    private static long resolveMax(long specifiedMax, long defaultMax, long globalMax) {
        return Math.min((specifiedMax > 0) ? specifiedMax : defaultMax, globalMax);
    }

    public AttachmentKey newAttachmentKey() {
        return new AttachmentKey(UUID.randomUUID().toString());
    }

    public AttachmentKey newAttachmentKey(HttpServletRequest req) throws IOException {
        return Optional.ofNullable(substringAfterLast(defaultString(req.getPathTranslated()), File.separator))
                .filter(x -> !StringUtils.isBlank(x))
                .map(AttachmentKey::new)
                .orElse(null);
    }

}
