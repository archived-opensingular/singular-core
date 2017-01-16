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

import com.google.common.base.Throwables;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.opensingular.form.wicket.mapper.attachment.upload.*;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.lib.commons.base.SingularProperties;

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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet responsável pelo upload de arquivos de forma assíncrona.
 */
@WebServlet(urlPatterns = {FileUploadServlet.UPLOAD_URL + "/*"})
public class FileUploadServlet extends HttpServlet {

    public static final String UPLOAD_URL = "/upload";
    public static final String PARAM_NAME = "FILE-UPLOAD";

    private final FileUploadManagerFactory upManagerFactory;
    private final AttachmentKeyFactory     keyFactory;
    private final ServletFileUploadFactory servletFileUploadFactory;
    private final FileUploadProcessor      upProcessor;
    private final UploadResponseWriter     upResponseWriter;
    private final FileUploadConfig         fupConfig;

    public FileUploadServlet() {
        this(new FileUploadManagerFactory(), new AttachmentKeyFactory(), new ServletFileUploadFactory(),
                new FileUploadProcessor(), new UploadResponseWriter(), new FileUploadConfig(SingularProperties.get()));
    }

    public FileUploadServlet(FileUploadManagerFactory upManagerFactory,
                             AttachmentKeyFactory keyFactory,
                             ServletFileUploadFactory servletFileUploadFactory,
                             FileUploadProcessor upProcessor,
                             UploadResponseWriter upResponseWriter,
                             FileUploadConfig fupConfig) {
        this.upManagerFactory = upManagerFactory;
        this.keyFactory = keyFactory;
        this.servletFileUploadFactory = servletFileUploadFactory;
        this.upProcessor = upProcessor;
        this.upResponseWriter = upResponseWriter;
        this.fupConfig = fupConfig;
    }

    public static String getUploadUrl(HttpServletRequest req, AttachmentKey attachmentKey) {
        return req.getServletContext().getContextPath() + UPLOAD_URL + "/" + attachmentKey.asString();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (!ServletFileUpload.isMultipartContent(req)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Request is not multipart, please 'multipart/form-data' enctype for your form.");
                return;
            }

            AttachmentKey uploadID = keyFactory.get(req);
            if (uploadID == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unidentifiable upload");
                return;
            }

            FileUploadManager upManager = upManagerFactory.get(req.getSession());
            Optional<UploadInfo> upInfo = upManager.findUploadInfo(uploadID);

            if (! upInfo.isPresent()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unregistered upload");
                return;
            }
            UploadInfo info = upInfo.get();
            List<UploadResponseInfo> allResponses = new ArrayList<>();

            try {
                Map<String, List<FileItem>> params = servletFileUploadFactory.get(fupConfig, info).parseParameterMap(req);
                for (FileItem item : params.get(PARAM_NAME)) {
                    allResponses.addAll(upProcessor.process(item, info, upManager));
                }
            } finally {
                upResponseWriter.writeJsonArrayResponseTo(resp, allResponses);
            }
        } catch (Exception e) {
            dealWithException(e);
        }
    }

    private void dealWithException(Exception e) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Erro processando upload", e);
        throw Throwables.propagate(e);
    }

}