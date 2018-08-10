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

package org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy;

import org.apache.commons.fileupload.FileUploadException;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.handlers.InMemoryAttachmentPersistenceHandler;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadItem;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadProcessor;
import org.opensingular.form.wicket.mapper.attachment.upload.UploadResponseWriter;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This strategy retrieves a list of files from a {@link HttpServletRequest}.
 * After storing the files, it sends a JSON response with info about each file created.
 */
public class SimplePostFilesStrategy implements ServletFileUploadStrategy {

    public static final String UPLOAD_URL = "/rest/publicUpload";
    public static final String URL_PATTERN = UPLOAD_URL + "/*";

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, FileUploadException {

        List<UploadResponseInfo> responses = new ArrayList<>();
        FileUploadManager fileUploadManager = makeFileUploadManagerFactory().getFileUploadManagerFromSessionOrMakeAndAttach(request.getSession());
        UploadInfo uploadInfo = fileUploadManager.createUploadInfo(
                null, null, null,
                this::getAttachmentPersistenceTemporaryHandler, makeAttachmentKeyFactory().make());
        FileUploadProcessor uploadProcessor = makeFileUploadProcessor();
        UploadResponseWriter responseWriter = makeUploadResponseWriter();

        try {
            for (Part part : request.getParts()) {
                responses.addAll(uploadProcessor.process(toFileUploadItem(part), uploadInfo, fileUploadManager));
            }
        } finally {
            responseWriter.writeJsonArrayResponseTo(response, responses);
        }
    }

    /**
     * Gets the persistence handler that will be used to store the uploaded file.
     *
     * @return an instance of {@link IAttachmentPersistenceHandler} to store the uploaded file.
     */
    private IAttachmentPersistenceHandler<? extends IAttachmentRef> getAttachmentPersistenceTemporaryHandler() {
        return new InMemoryAttachmentPersistenceHandler();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(HttpServletRequest request) throws IOException, ServletException {
        return !request.getParts().isEmpty();
    }

    /**
     * Creates a wrapper for a file to be processed.
     *
     * @param part part to be wrapped.
     * @return an instance of {@link FileUploadItem} that holds the part.
     */
    private FileUploadItem<Part> toFileUploadItem(final Part part) {
        return new FileUploadItem<Part>() {
            @Override
            public InputStream getInputStream() throws IOException {
                return part.getInputStream();
            }

            @Override
            public boolean isFormField() {
                return false;
            }

            @Override
            public String getContentType() {
                return part.getContentType();
            }

            @Override
            public long getSize() {
                return part.getSize();
            }

            @Override
            public String getName() {
                return part.getName();
            }

            @Override
            public Part getWrapped() {
                return part;
            }
        };
    }
}
