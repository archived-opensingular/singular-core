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
