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

package org.opensingular.form.wicket.mapper.attachment;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.wicket.ajax.json.JSONArray;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.base.SingularProperties;

/**
 * Servlet responsável pelo upload de arquivos de forma assíncrona.
 */
@WebServlet(urlPatterns = { FileUploadServlet.UPLOAD_URL + "/*" })
public class FileUploadServlet extends HttpServlet {

    public final static String UPLOAD_URL = "/upload";

    public static final String PARAM_NAME = "FILE-UPLOAD";

    public static String getUploadUrl(HttpServletRequest req, UUID uploadId) {
        return req.getServletContext().getContextPath() + UPLOAD_URL + "/" + uploadId;
    }

    public final static Optional<File> lookupFile(HttpServletRequest req, String fileId) {
        return FileUploadManager.get(req.getSession())
            .findLocalFile(UUID.fromString(fileId));
    }

    public final static boolean consumeFile(HttpServletRequest req, String fileId, Consumer<File> callback) {
        FileUploadManager manager = FileUploadManager.get(req.getSession());
        return manager.consumeFile(UUID.fromString(fileId), callback);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent(req)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request is not multipart, please 'multipart/form-data' enctype for your form.");
            return;
        }

        final String uploadIdParam = substringAfterLast(defaultString(req.getPathTranslated()), File.separator);
        if (isBlank(uploadIdParam)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unidentifiable upload");
            return;
        }

        final UUID uploadId = UUID.fromString(uploadIdParam);

        final FileUploadManager manager = FileUploadManager.get(req.getSession());
        final Optional<UploadInfo> uploadInfo = manager.findUploadInfo(uploadId);
        if (!uploadInfo.isPresent()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unregistered upload");
            return;
        }

        final FileUploadProcessor processor = new FileUploadProcessor(uploadInfo.get(), req, resp);
        processor.handleFiles();
    }

    private static class FileUploadProcessor {

        private final JSONArray           filesJson;
        private final HttpServletRequest  request;
        private final HttpServletResponse response;
        private final UploadInfo          uploadInfo;
        private final FileUploadManager   manager;
        private final FileUploadConfig    config;

        private FileUploadProcessor(UploadInfo uploadInfo, HttpServletRequest request, HttpServletResponse response) {
            this.uploadInfo = uploadInfo;
            this.request = request;
            this.response = response;
            this.filesJson = new JSONArray();
            this.manager = FileUploadManager.get(request.getSession());
            this.config = new FileUploadConfig(SingularProperties.get());
        }

        private ServletFileUpload createHandler(FileUploadConfig config) {
            final ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());

            servletFileUpload.setFileSizeMax(resolveMax(
                uploadInfo.maxFileSize,
                config.defaultMaxFileSize,
                config.globalMaxFileSize));

            servletFileUpload.setSizeMax(resolveMax(
                uploadInfo.maxFileSize * uploadInfo.maxFileCount,
                config.defaultMaxRequestSize,
                config.globalMaxRequestSize));

            return servletFileUpload;
        }

        private static long resolveMax(long specifiedMax, long defaultMax, long globalMax) {
            return Math.min((specifiedMax > 0) ? specifiedMax : defaultMax, globalMax);
        }

        public void handleFiles() {
            try {
                Map<String, List<FileItem>> params = createHandler(config).parseParameterMap(request);
                for (FileItem item : params.get(PARAM_NAME))
                    processFileItem(filesJson, item);
            } catch (Exception e) {
                throw new SingularException(e);
            } finally {
                DownloadUtil.writeJSONtoResponse(filesJson, response);
            }
        }

        private void processFileItem(JSONArray fileGroup, FileItem item) throws Exception {
            if (!item.isFormField()) {
                final long size = item.getSize();
                final String originalFilename = item.getName();
                final String contentType = lowerCase(item.getContentType());
                final String extension = lowerCase(substringAfterLast(originalFilename, "."));

                if (!(uploadInfo.isMimeTypeAllowed(contentType) || uploadInfo.isExtensionAllowed(extension))) {
                    // TODO retorn error
                }

                try (InputStream in = item.getInputStream()) {
                    final FileUploadInfo fileInfo = manager.createFile(uploadInfo.uploadId, originalFilename, in);
                    fileGroup.put(DownloadUtil.toJSON(fileInfo.fileId.toString(), fileInfo.hash, originalFilename, size));
                }
            }
        }
    }
}