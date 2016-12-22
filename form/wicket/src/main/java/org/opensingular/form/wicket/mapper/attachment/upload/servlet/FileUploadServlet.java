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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.config.FileUploadConfig;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.FileUploadObjectFactories;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.processor.FileUploadProcessor;
import org.opensingular.lib.commons.base.SingularException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servlet responsável pelo upload de arquivos de forma assíncrona.
 */
@WebServlet(urlPatterns = {FileUploadServlet.UPLOAD_URL + "/*"})
public class FileUploadServlet extends HttpServlet {

    public static final String UPLOAD_URL = "/upload";
    public static final String PARAM_NAME = "FILE-UPLOAD";

    private final FileUploadObjectFactories factories;

    public FileUploadServlet() {
        this(new FileUploadObjectFactories());
    }

    public FileUploadServlet(FileUploadObjectFactories factories) {
        this.factories = factories;
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

        final AttachmentKey uploadID = factories.getAttachmentKeyFactory().get(req);

        if (uploadID == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unidentifiable upload");
            return;
        }

        FileUploadManager    fileUploadManager = factories.getFileUploadManagerFactory().get(req.getSession());
        Optional<UploadInfo> uploadInfo        = fileUploadManager.findUploadInfo(uploadID);

        if (uploadInfo.isPresent()) {

            UploadInfo               info      = uploadInfo.get();
            FileUploadConfig         config    = factories.getFileUploadConfigFactory().get();
            List<UploadResponseInfo> filesJson = new ArrayList<>();

            try {

                Map<String, List<FileItem>> params    = factories.getServletFileUploadFactory().get(config, info).parseParameterMap(req);
                FileUploadProcessor         processor = factories.getFileUploadProcessorFactory().get(info, fileUploadManager);

                for (FileItem item : params.get(PARAM_NAME)) {
                    processor.processFileItem(filesJson, item);
                }

            } catch (Exception e) {
                throw SingularException.rethrow(e);
            } finally {
                factories.getUploadResponseWriterFactory().get().writeJsonArrayResponseTo(resp, filesJson);
            }

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unregistered upload");
        }

    }

}