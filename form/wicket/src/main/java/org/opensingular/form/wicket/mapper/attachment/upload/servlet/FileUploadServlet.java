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
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.processor.FileUploadProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

/**
 * Servlet responsável pelo upload de arquivos de forma assíncrona.
 */
@WebServlet(urlPatterns = {FileUploadServlet.UPLOAD_URL + "/*"})
public class FileUploadServlet extends HttpServlet {

    public final static String UPLOAD_URL = "/upload";

    public static final String PARAM_NAME = "FILE-UPLOAD";

    public static String getUploadUrl(HttpServletRequest req, AttachmentKey attachmentKey) {
        return req.getServletContext().getContextPath() + UPLOAD_URL + "/" + attachmentKey.asString();
    }

    public static Optional<File> lookupFile(HttpServletRequest req, String fileId) {
        return getFileUploadManager(req).findLocalFile(fileId);
    }

    public static <R> Optional<R> consumeFile(HttpServletRequest req, String fileId, Function<IAttachmentRef, R> callback) {
        return getFileUploadManager(req).consumeFile(fileId, callback);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (!ServletFileUpload.isMultipartContent(req)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request is not multipart, please 'multipart/form-data' enctype for your form.");
            return;
        }

        final AttachmentKey uploadID = getUploadID(req);

        if (uploadID == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unidentifiable upload");
            return;
        }

        //recupera o upload info preconfigurado na sessão (deve ser configurado antes de usar a servlet)
        final Optional<UploadInfo> uploadInfo = getFileUploadManager(req).findUploadInfo(uploadID);

        if (!uploadInfo.isPresent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unregistered upload");
            return;
        }

        new FileUploadProcessor(uploadInfo.get(), req, resp, getFileUploadManager(req)).handleFiles();
    }

    private AttachmentKey getUploadID(HttpServletRequest req) throws IOException {
        return Optional.ofNullable(substringAfterLast(defaultString(req.getPathTranslated()), File.separator))
                .filter(x -> !StringUtils.isBlank(x))
                .map(AttachmentKey::fromString)
                .orElse(null);
    }

    private static FileUploadManager getFileUploadManager(HttpServletRequest req) {
        return FileUploadManager.get(req.getSession());
    }

}