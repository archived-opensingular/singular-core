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

package org.opensingular.form.wicket.mapper.attachment.upload.info;

import org.apache.wicket.ajax.json.JSONObject;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;

import java.io.Serializable;

public class UploadResponseInfo implements Serializable {

    public static final String FILE_MUST_NOT_HAVE_LENGTH_ZERO = "Arquivo não pode ser de tamanho 0 (zero)";
    public static final String FILE_TYPE_NOT_ALLOWED = "Tipo de arquivo não permitido. <BR> Permitido: ";

    private final AttachmentKey fileId;
    private final String        name;
    private final long          size;
    private final String        hashSHA1;
    private final String        errorMessage;

    public UploadResponseInfo(IAttachmentRef attachmentRef) {
        this(attachmentRef.getId(), attachmentRef.getName(), attachmentRef.getSize(), attachmentRef.getHashSHA1());
    }

    public UploadResponseInfo(SIAttachment attachment) {
        this(attachment.getFileId(), attachment.getFileName(), attachment.getFileSize(), attachment.getFileHashSHA1());
    }

    private UploadResponseInfo(String fileId, String name, long size, String hashSHA1) {
        this.fileId = new AttachmentKey(fileId);
        this.name = name;
        this.size = size;
        this.hashSHA1 = hashSHA1;
        this.errorMessage = null;
    }

    public UploadResponseInfo(String name, String errorMessage) {
        this.fileId = null;
        this.name = name;
        this.size = 0L;
        this.hashSHA1 = null;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    private JSONObject toJson() {
        JSONObject jsonFile = new JSONObject();
        if (errorMessage != null) {
            jsonFile.put("name", name);
            jsonFile.put("errorMessage", errorMessage);
        } else {
            jsonFile.put("fileId", fileId);
            jsonFile.put("name", name);
            jsonFile.put("size", size);
            jsonFile.put("hashSHA1", hashSHA1);
        }
        return jsonFile;
    }

    public AttachmentKey getFileId() {
        return fileId;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getHashSHA1() {
        return hashSHA1;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
