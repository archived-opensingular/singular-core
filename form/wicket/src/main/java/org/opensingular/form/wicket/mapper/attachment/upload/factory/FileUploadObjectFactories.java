package org.opensingular.form.wicket.mapper.attachment.upload.factory;

import org.opensingular.form.wicket.mapper.attachment.upload.config.FileUploadConfig;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManagerFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.writer.UploadResponseWriter;

import java.io.Serializable;

public class FileUploadObjectFactories implements Serializable {

    private final FileUploadProcessorFactory   fileUploadProcessorFactory;
    private final AttachmentKeyFactory         attachmentKeyFactory;
    private final FileUploadManagerFactory     fileUploadManagerFactory;
    private final ServletFileUploadFactory     servletFileUploadFactory;
    private final FileUploadConfig.Factory     fileUploadConfigFactory;
    private final UploadResponseWriter.Factory uploadResponseWriterFactory;

    public FileUploadObjectFactories() {
        fileUploadProcessorFactory = new FileUploadProcessorFactory();
        attachmentKeyFactory = new AttachmentKeyFactory();
        fileUploadManagerFactory = new FileUploadManagerFactory();
        servletFileUploadFactory = new ServletFileUploadFactory();
        fileUploadConfigFactory = new FileUploadConfig.Factory();
        uploadResponseWriterFactory = new UploadResponseWriter.Factory();
    }

    public FileUploadObjectFactories(FileUploadProcessorFactory fileUploadProcessorFactory,
                                     AttachmentKeyFactory attachmentKeyFactory,
                                     FileUploadManagerFactory fileUploadManagerFactory,
                                     ServletFileUploadFactory servletFileUploadFactory,
                                     FileUploadConfig.Factory fileUploadConfigFactory,
                                     UploadResponseWriter.Factory uploadResponseWriterFactory) {
        this.fileUploadProcessorFactory = fileUploadProcessorFactory;
        this.attachmentKeyFactory = attachmentKeyFactory;
        this.fileUploadManagerFactory = fileUploadManagerFactory;
        this.servletFileUploadFactory = servletFileUploadFactory;
        this.fileUploadConfigFactory = fileUploadConfigFactory;
        this.uploadResponseWriterFactory = uploadResponseWriterFactory;
    }

    public FileUploadProcessorFactory getFileUploadProcessorFactory() {
        return fileUploadProcessorFactory;
    }

    public AttachmentKeyFactory getAttachmentKeyFactory() {
        return attachmentKeyFactory;
    }

    public FileUploadManagerFactory getFileUploadManagerFactory() {
        return fileUploadManagerFactory;
    }

    public ServletFileUploadFactory getServletFileUploadFactory() {
        return servletFileUploadFactory;
    }

    public FileUploadConfig.Factory getFileUploadConfigFactory() {
        return fileUploadConfigFactory;
    }

    public UploadResponseWriter.Factory getUploadResponseWriterFactory() {
        return uploadResponseWriterFactory;
    }

}