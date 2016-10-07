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

import static org.opensingular.lib.commons.util.ConversionUtils.*;
import static org.opensingular.form.wicket.mapper.attachment.FileUploadServlet.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.ajax.json.JSONArray;

import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.form.io.IOUtil;

/**
 * Servlet responsável pelo upload de arquivos de forma assíncrona.
 */
@WebServlet(urlPatterns = { FileUploadServlet.UPLOAD_URL + "/*" })
public class FileUploadServlet extends HttpServlet {

    public static final String PARAM_NAME = "FILE-UPLOAD";
    public final static String UPLOAD_URL = "/upload";
    public static File         UPLOAD_WORK_FOLDER;

    static {
        String tempPath = System.getProperty("java.io.tmpdir", "/tmp");
        File f = new File(tempPath + "/singular-servlet-work-dir" + UUID.randomUUID().toString());
        if (!f.exists()) {
            f.mkdirs();
        }
        f.deleteOnExit();
        UPLOAD_WORK_FOLDER = f;
    }

    public final static File lookupFile(String fileId) {
        // validação bem básica para evitar brecha de segurança
        UUID.fromString(fileId);
        return new File(UPLOAD_WORK_FOLDER, fileId);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        validadeMultpart(request);
        FileUploadProcessor processor = new FileUploadProcessor(request, response);
        processor.handleFiles();
    }

    private void validadeMultpart(HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }
    }

}

class FileUploadProcessor {

    private JSONArray           filesJson;
    private HttpServletRequest  request;
    private HttpServletResponse response;

    FileUploadProcessor(
        HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.filesJson = new JSONArray();
    }

    private ServletFileUpload handler() {
        final SingularProperties sp = SingularProperties.get();
        final ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());

        long maxFileSize = toLongHumane(sp.getProperty(SingularProperties.FILEUPLOAD_GLOBAL_MAX_FILE_SIZE), -1);
        long maxRequestSize = toLongHumane(sp.getProperty(SingularProperties.FILEUPLOAD_GLOBAL_MAX_REQUEST_SIZE), -1);
        servletFileUpload.setFileSizeMax(maxFileSize);
        servletFileUpload.setSizeMax(maxRequestSize);
        
        return servletFileUpload;
    }

    public void handleFiles() {
        try {
            Map<String, List<FileItem>> params = handler().parseParameterMap(request);
            addFileToService(params.get(PARAM_NAME));
        } catch (Exception e) {
            throw new SingularException(e);
        } finally {
            DownloadUtil.writeJSONtoResponse(filesJson, response);
        }
    }

    private void addFileToService(List<FileItem> files) throws Exception {
        processFiles(filesJson, files);
    }

    private void processFiles(JSONArray fileGroup, List<FileItem> items) throws Exception {
        for (FileItem item : items) {
            processFileItem(fileGroup, (DiskFileItem) item);
        }
    }

    private void processFileItem(JSONArray fileGroup, FileItem item) throws Exception {
        if (!item.isFormField()) {
            String id = UUID.randomUUID().toString();
            long size = item.getSize();
            String name = item.getName();
            File f = new File(FileUploadServlet.UPLOAD_WORK_FOLDER, id);
            f.deleteOnExit();
            try (
                OutputStream outputStream = IOUtil.newBuffredOutputStream(f);
                InputStream in = item.getInputStream();) {
                IOUtils.copy(in, outputStream);
            }
            fileGroup.put(DownloadUtil.toJSON(id, null, name, size));
        }
    }

}