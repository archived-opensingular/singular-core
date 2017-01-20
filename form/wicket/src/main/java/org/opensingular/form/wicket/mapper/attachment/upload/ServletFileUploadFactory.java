/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.mapper.attachment.upload;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;

import java.io.Serializable;

public class ServletFileUploadFactory  implements Serializable {

    public ServletFileUpload get(FileUploadConfig config, UploadInfo uploadInfo) {
        final ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());

        servletFileUpload.setFileSizeMax(
                resolveMax(uploadInfo.getMaxFileSize(), config.getDefaultMaxFileSize(), config.getGlobalMaxFileSize())
        );

        servletFileUpload.setSizeMax(
                resolveMax(uploadInfo.getMaxFileSize() * uploadInfo.getMaxFileCount(),
                        config.getDefaultMaxRequestSize(),
                        config.getGlobalMaxRequestSize()
                )
        );

        return servletFileUpload;
    }

    private static long resolveMax(long specifiedMax, long defaultMax, long globalMax) {
        return Math.min((specifiedMax > 0) ? specifiedMax : defaultMax, globalMax);
    }

}
