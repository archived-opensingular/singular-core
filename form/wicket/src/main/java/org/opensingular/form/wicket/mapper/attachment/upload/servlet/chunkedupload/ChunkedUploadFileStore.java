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

package org.opensingular.form.wicket.mapper.attachment.upload.servlet.chunkedupload;

import com.google.common.io.Files;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.wicket.mapper.attachment.upload.ServletFileUploadFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy.ServletFileUploadStrategy.PARAM_NAME;

public class ChunkedUploadFileStore implements HttpSessionBindingListener {

    private static final String SESSION_KEY = ChunkedUploadFileStore.class.getName();
    private static       Logger LOGGER      = LoggerFactory.getLogger(ChunkedUploadFileStore.class);

    private       Map<String, List<FileItem>>     requestFileAssembly = new HashMap<>();
    private final ServletFileUploadFactory        servletFileUploadFactory;
    private       ConcurrentLinkedQueue<FileItem> doneItems           = new ConcurrentLinkedQueue<>();


    ChunkedUploadFileStore(ServletFileUploadFactory servletFileUploadFactory) {
        this.servletFileUploadFactory = servletFileUploadFactory;
    }

    public static synchronized ChunkedUploadFileStore getChunkedUploadFileStoreFromSessionOrMakeAndAttach(HttpSession session, ServletFileUploadFactory servletFileUploadFactory) {
        ChunkedUploadFileStore chunkedUploadFileStore = (ChunkedUploadFileStore) session.getAttribute(ChunkedUploadFileStore.SESSION_KEY);
        if (chunkedUploadFileStore == null) {
            chunkedUploadFileStore = new ChunkedUploadFileStore(servletFileUploadFactory);
            session.setAttribute(ChunkedUploadFileStore.SESSION_KEY, chunkedUploadFileStore);
            LOGGER.debug("ChunkedUploadFileStore created: SESSION_ID = {}", session.getId());
        }
        return chunkedUploadFileStore;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {

    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        for (Map.Entry<String, List<FileItem>> stringListEntry : requestFileAssembly.entrySet()) {
            for (FileItem fileItem : stringListEntry.getValue()) {
                fileItem.delete();
            }
        }
        for (FileItem doneIten : doneItems) {
            doneIten.delete();
        }
    }

    public void assemble(UploadInfo uploadInfo, HttpServletRequest req) throws FileUploadException, IOException {
        ContentRangeHeaderParser       contentRangeHeaderParser       = new ContentRangeHeaderParser(req);
        ContentDispositionHeaderParser contentDispositionHeaderParser = new ContentDispositionHeaderParser(req);
        Map<String, List<FileItem>>    params                         = servletFileUploadFactory.makeServletFileUpload(uploadInfo).parseParameterMap(req);
        if (contentRangeHeaderParser.exists()) {
            assembleChunked(params.get(PARAM_NAME), contentRangeHeaderParser.isLastChunk(), contentDispositionHeaderParser.getFileName());
        } else {
            assembleSinglePost(params.get(PARAM_NAME));
        }
    }

    private void assembleChunked(List<FileItem> fileItems, boolean lastChunk, String fileName) throws IOException {
        List<FileItem> fileItemList = requestFileAssembly.computeIfAbsent(fileName, k -> new ArrayList<>());
        if (fileItems != null) {
            fileItemList.addAll(fileItems);
            if (lastChunk) {
                long size = 0;
                File f    = File.createTempFile(UUID.randomUUID().toString(), UUID.randomUUID().toString());
                try (FileOutputStream fos = new FileOutputStream(f)) {
                    for (FileItem fileItem : fileItemList) {
                        IOUtils.copy(fileItem.getInputStream(), fos);
                        size += fileItem.getSize();
                    }
                }
                FileItem delegate  = fileItemList.get(fileItemList.size() - 1);
                long     finalSize = size;
                doneItems.offer(wrapFileItem(f, delegate, finalSize));
            }
        }
    }

    private FileItem wrapFileItem(File f, FileItem delegate, long finalSize) {
        return new FileItem() {
            @Override
            public InputStream getInputStream() throws IOException {
                return new FileInputStream(f);
            }

            @Override
            public String getContentType() {
                return delegate.getContentType();
            }

            @Override
            public String getName() {
                return delegate.getName();
            }

            @Override
            public boolean isInMemory() {
                return false;
            }

            @Override
            public long getSize() {
                return finalSize;
            }

            @Override
            public byte[] get() {
                try {
                    return IOUtils.toByteArray(getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new SingularFormException(e.getMessage(), e);
                }
            }

            @Override
            public String getString(String encoding) throws UnsupportedEncodingException {
                return new String(get(), encoding);
            }

            @Override
            public String getString() {
                return new String(get());
            }

            @Override
            public void write(File file) throws Exception {
                Files.copy(f, file);
            }

            @Override
            public void delete() {
                f.delete();
            }

            @Override
            public String getFieldName() {
                return delegate.getFieldName();
            }

            @Override
            public void setFieldName(String name) {
                delegate.setFieldName(name);
            }

            @Override
            public boolean isFormField() {
                return delegate.isFormField();
            }

            @Override
            public void setFormField(boolean state) {
                delegate.setFormField(state);
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return new FileOutputStream(f);
            }

            @Override
            public FileItemHeaders getHeaders() {
                return delegate.getHeaders();
            }

            @Override
            public void setHeaders(FileItemHeaders headers) {
                delegate.setHeaders(headers);
            }
        };
    }

    private void assembleSinglePost(List<FileItem> fileItems) {
        fileItems.forEach(doneItems::offer);
    }

    public FileItem popDoneItem() {
        return doneItems.poll();
    }

    public boolean hasDoneItems() {
        return !doneItems.isEmpty();
    }


}
