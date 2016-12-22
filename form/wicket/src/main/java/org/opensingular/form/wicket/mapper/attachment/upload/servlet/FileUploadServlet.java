/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.form.wicket.mapper.attachment.upload.servlet;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.AttachmentKeyFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.FileUploadProcessorFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManagerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Servlet responsável pelo upload de arquivos de forma assíncrona.
 */
@WebServlet(urlPatterns = {FileUploadServlet.UPLOAD_URL + "/*"})
public class FileUploadServlet extends HttpServlet {

    public final static String UPLOAD_URL = "/upload";

    public static final String PARAM_NAME = "FILE-UPLOAD";

    private final FileUploadProcessorFactory fileUploadProcessorFactory;
    private final AttachmentKeyFactory       attachmentKeyFactory;
    private final FileUploadManagerFactory   fileUploadManagerFactory;

    public FileUploadServlet() {
        this(new FileUploadProcessorFactory(), new AttachmentKeyFactory(), new FileUploadManagerFactory());
    }

    public FileUploadServlet(FileUploadProcessorFactory fileUploadProcessorFactory,
                             AttachmentKeyFactory attachmentKeyFactory,
                             FileUploadManagerFactory fileUploadManagerFactory) {
        this.fileUploadProcessorFactory = fileUploadProcessorFactory;
        this.attachmentKeyFactory = attachmentKeyFactory;
        this.fileUploadManagerFactory = fileUploadManagerFactory;
    }

    public static String getUploadUrl(HttpServletRequest req, AttachmentKey attachmentKey) {
        return req.getServletContext().getContextPath() + UPLOAD_URL + "/" + attachmentKey.asString();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (!ServletFileUpload.isMultipartContent(req)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request is not multipart, please 'multipart/form-data' enctype for your form.");
            return;
        }

        final AttachmentKey uploadID = attachmentKeyFactory.get(req);

        if (uploadID == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unidentifiable upload");
            return;
        }

        //recupera o upload info preconfigurado na sessão (deve ser configurado antes de usar a servlet)
        final Optional<UploadInfo> uploadInfo = fileUploadManagerFactory.get(req.getSession()).findUploadInfo(uploadID);

        if (uploadInfo.isPresent()) {
            fileUploadProcessorFactory
                    .get(req, resp, uploadInfo.get(), fileUploadManagerFactory.get(req.getSession()))
                    .handleFiles();
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unregistered upload");
        }

    }

}