package org.opensingular.form.wicket.mapper.attachment.upload.factory;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.opensingular.form.wicket.mapper.attachment.upload.config.FileUploadConfig;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;

import java.io.Serializable;

public class ServletFileUploadFactory  implements Serializable {

    public ServletFileUpload get(FileUploadConfig config, UploadInfo uploadInfo) {
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

}
