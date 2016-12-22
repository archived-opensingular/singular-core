package org.opensingular.form.wicket.mapper.attachment.upload.factory;

import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.processor.FileUploadProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileUploadProcessorFactory {

    public FileUploadProcessor get(HttpServletRequest req, HttpServletResponse resp,
                                   UploadInfo uploadInfo, FileUploadManager fileUploadManager) {
        return new FileUploadProcessor(uploadInfo, req, resp, fileUploadManager, new ServletFileUploadFactory());
    }

}