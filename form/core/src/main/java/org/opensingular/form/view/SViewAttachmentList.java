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

package org.opensingular.form.view;


import java.util.ArrayList;
import java.util.List;

public class SViewAttachmentList extends SView implements SViewSupportsUploadListener<SViewAttachmentList> {

    private List<FileEventListener> fileUploadedListeners = new ArrayList<>();
    private List<FileEventListener> fileRemovedListeners  = new ArrayList<>();

    private boolean showPageBlockWhileUploading;

    public boolean isShowPageBlockWhileUploading() {
        return showPageBlockWhileUploading;
    }

    public SViewAttachmentList setShowPageBlockWhileUploading(boolean showPageBlockWhileUploading) {
        this.showPageBlockWhileUploading = showPageBlockWhileUploading;
        return this;
    }

    @Override
    public SViewAttachmentList withFileUploadedListener(FileEventListener fileUploadedListener) {
        this.fileUploadedListeners.add(fileUploadedListener);
        return this;
    }

    @Override
    public List<FileEventListener> getFileUploadedListeners() {
        return fileUploadedListeners;
    }

    @Override
    public SViewAttachmentList withFileRemovedListener(FileEventListener fileRemovedListener) {
        this.fileRemovedListeners.add(fileRemovedListener);
        return this;
    }

    @Override
    public List<FileEventListener> getFileRemovedListeners() {
        return fileRemovedListeners;
    }
}
