package org.opensingular.form.wicket.mapper.attachment.upload.processor;

import org.apache.commons.fileupload.FileItem;
import org.opensingular.form.wicket.mapper.attachment.upload.config.FileUploadConfig;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.ServletFileUploadFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.base.SingularProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.opensingular.form.wicket.mapper.attachment.upload.servlet.FileUploadServlet.PARAM_NAME;

public class FileUploadProcessor {

    private final List<UploadResponseInfo> filesJson;
    private final HttpServletRequest       request;
    private final HttpServletResponse      response;
    private final UploadInfo               uploadInfo;
    private final FileUploadManager        manager;
    private final FileUploadConfig         config;
    private final ServletFileUploadFactory servletFileUploadFactory;

    public FileUploadProcessor(UploadInfo uploadInfo, HttpServletRequest request, HttpServletResponse response,
                               FileUploadManager fileUploadManager,
                               ServletFileUploadFactory servletFileUploadFactory) {
        this.uploadInfo = uploadInfo;
        this.request = request;
        this.response = response;
        this.servletFileUploadFactory = servletFileUploadFactory;
        this.filesJson = new ArrayList<>();
        this.manager = fileUploadManager;
        this.config = new FileUploadConfig(SingularProperties.get());
    }

    public void handleFiles() {
        try {
            Map<String, List<FileItem>> params = servletFileUploadFactory.get(config, uploadInfo).parseParameterMap(request);
            for (FileItem item : params.get(PARAM_NAME)) {
                processFileItem(filesJson, item);
            }
        } catch (Exception e) {
            throw SingularException.rethrow(e);
        } finally {
            UploadResponseInfo.writeJsonArrayResponseTo(response, filesJson);
        }
    }

    private void processFileItem(List<UploadResponseInfo> response, FileItem item) throws Exception {
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
