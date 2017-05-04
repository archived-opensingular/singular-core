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

    private FileUploadManagerFactory uploadManagerFactory;
    private AttachmentKeyFactory     keyFactory;
    private ServletFileUploadFactory servletFileUploadFactory;
    private FileUploadProcessor      upProcessor;
    private UploadResponseWriter     upResponseWriter;

    @Override
    public void init() throws ServletException {
        super.init();
        this.uploadManagerFactory = makeFileUploadManagerFactory();
        this.keyFactory = makeAttachmentKeyFactory();
        this.servletFileUploadFactory = makeServletFileUploadFactory();
        this.upProcessor = makeFileUploadProcessor();
        this.upResponseWriter = makeUploadResponseWriter();
    }

    protected FileUploadManagerFactory makeFileUploadManagerFactory() {
        return new FileUploadManagerFactory();
    }

    protected AttachmentKeyFactory makeAttachmentKeyFactory() {
        return new AttachmentKeyFactory();
    }

    protected ServletFileUploadFactory makeServletFileUploadFactory() {
        return new ServletFileUploadFactory(makeFileUploadConfig());
    }

    protected FileUploadProcessor makeFileUploadProcessor() {
        return new FileUploadProcessor();
    }

    protected UploadResponseWriter makeUploadResponseWriter() {
        return new UploadResponseWriter();
    }

    protected FileUploadConfig makeFileUploadConfig() {
        return new FileUploadConfig(SingularProperties.get());
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

            AttachmentKey attachmentKey = keyFactory.makeFromRequestPathOrNull(req);
            if (attachmentKey == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unidentifiable upload");
                return;
            }

            FileUploadManager    fileUploadManager  = uploadManagerFactory.getFileUploadManagerFromSessionOrMakeAndAttach(req.getSession());
            Optional<UploadInfo> uploadInfoOptional = fileUploadManager.findUploadInfoByAttachmentKey(attachmentKey);

            if (!uploadInfoOptional.isPresent()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unregistered upload");
                return;
            }
            UploadInfo               uploadInfo = uploadInfoOptional.get();
            List<UploadResponseInfo> responses  = new ArrayList<>();

            try {
                Map<String, List<FileItem>> params = servletFileUploadFactory.makeServletFileUpload(uploadInfo).parseParameterMap(req);
                for (FileItem item : params.get(PARAM_NAME)) {
                    responses.addAll(upProcessor.process(item, uploadInfo, fileUploadManager));
                }
            } finally {
                upResponseWriter.writeJsonArrayResponseTo(resp, responses);
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