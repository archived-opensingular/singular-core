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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FilenameUtils;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKeyFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadConfig;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadItem;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadManagerFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.FileUploadProcessor;
import org.opensingular.form.wicket.mapper.attachment.upload.ServletFileUploadFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.UploadResponseWriter;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.servlet.chunkedupload.ChunkedUploadFileStore;
import org.opensingular.lib.commons.base.SingularProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The strategy that uses {@link AttachmentKey} as the main resource.
 */
public class AttachmentKeyStrategy implements ServletFileUploadStrategy {

    protected FileUploadManagerFactory uploadManagerFactory;
    protected AttachmentKeyFactory     keyFactory;
    protected ServletFileUploadFactory servletFileUploadFactory;
    protected FileUploadProcessor      upProcessor;
    protected UploadResponseWriter     upResponseWriter;

    @Override
    public void init() {
        this.uploadManagerFactory = makeFileUploadManagerFactory();
        this.keyFactory = getAttachmentKeyFactory();
        this.servletFileUploadFactory = makeServletFileUploadFactory();
        this.upProcessor = makeFileUploadProcessor();
        this.upResponseWriter = makeUploadResponseWriter();
    }

    public static String getUploadUrl(HttpServletRequest req, AttachmentKey attachmentKey) {
        return req.getServletContext().getContextPath() + UPLOAD_URL + "/" + attachmentKey.asString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(HttpServletRequest req, HttpServletResponse resp) throws IOException, FileUploadException {
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
            ChunkedUploadFileStore chunkedUploadFileStore = ChunkedUploadFileStore.getChunkedUploadFileStoreFromSessionOrMakeAndAttach(req.getSession(), servletFileUploadFactory);
            synchronized (chunkedUploadFileStore) {//SessionVariable
                chunkedUploadFileStore.assemble(uploadInfo, req);
                while (chunkedUploadFileStore.hasDoneItems()) {
                    responses.addAll(upProcessor.process(toFileUploadItem(chunkedUploadFileStore.popDoneItem()), uploadInfo, fileUploadManager));
                }
            }
        } finally {
            upResponseWriter.writeJsonArrayResponseTo(resp, responses);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(HttpServletRequest request) {
        return getAttachmentKeyFactory().isRawKeyPresent(request);
    }

    /**
     * Creates a wrapper for a file to be processed.
     *
     * @param item item to be wrapped.
     * @return an instance of {@link FileUploadItem} that holds the item.
     */
    public FileUploadItem<FileItem> toFileUploadItem(final FileItem item) {
        return new FileUploadItem<FileItem>() {
            @Override
            public InputStream getInputStream() throws IOException {
                return item.getInputStream();
            }

            @Override
            public boolean isFormField() {
                return item.isFormField();
            }

            @Override
            public String getContentType() {
                return item.getContentType();
            }

            @Override
            public long getSize() {
                return item.getSize();
            }

            @Override
            public String getName() {
                return FilenameUtils.getName(item.getName());//NOSONAR
            }

            @Override
            public FileItem getWrapped() {
                return item;
            }
        };
    }

    protected AttachmentKeyFactory getAttachmentKeyFactory() {
        if (keyFactory == null) {
            keyFactory = makeAttachmentKeyFactory();
        }

        return keyFactory;
    }

    protected ServletFileUploadFactory makeServletFileUploadFactory() {
        return new ServletFileUploadFactory(makeFileUploadConfig());
    }

    protected FileUploadConfig makeFileUploadConfig() {
        return new FileUploadConfig(SingularProperties.get());
    }

}
