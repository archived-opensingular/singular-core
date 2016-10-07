/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.upload;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.IMultipartWebRequest;
import org.apache.wicket.request.Request;

/**
 * Altera o FileUploadField do wicket padrão para suportar arquivos com 0kb
 */
public class SFileUploadField extends FileUploadField {

    public SFileUploadField(String id) {
        super(id);
    }

    public SFileUploadField(String id, IModel<? extends List<FileUpload>> model) {
        super(id, model);
    }

    @Override
    public List<FileUpload> getFileUploads() {
        List<FileUpload> fileUploads = super.getFileUploads();
        if (fileUploads != null && !fileUploads.isEmpty()) {
            return fileUploads;
        } else {
            fileUploads = new ArrayList<>();
            final Request request = getRequest();
            if (request instanceof IMultipartWebRequest) {
                final List<FileItem> fileItems = ((IMultipartWebRequest) request).getFile(getInputName());

                if (fileItems != null) {
                    for (FileItem item : fileItems) {
                        fileUploads.add(new FileUpload(item));
                    }
                }
            }
            return fileUploads;
        }
    }

}
