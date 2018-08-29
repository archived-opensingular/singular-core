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

import com.google.common.collect.ComparisonChain;
import org.json.JSONObject;
import org.opensingular.form.type.core.attachment.IAttachmentRef;

import java.io.Serializable;
import java.util.Objects;

public class FileUploadInfo implements Serializable, Comparable<FileUploadInfo> {

    public final IAttachmentRef attachmentRef;

    public FileUploadInfo(IAttachmentRef attachmentRef) {
        this.attachmentRef = attachmentRef;
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("fileId", attachmentRef.getId())
                .put("name", attachmentRef.getName())
                .put("hashSHA1", attachmentRef.getHashSHA1())
                .put("size", attachmentRef.getSize());
    }

    public IAttachmentRef getAttachmentRef() {
        return attachmentRef;
    }

    @Override
    public boolean equals(Object o) {
        return this == o
                || !(o == null || getClass() != o.getClass())
                && Objects.equals(attachmentRef, ((FileUploadInfo) o).attachmentRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attachmentRef);
    }

    @Override
    public int compareTo(FileUploadInfo o) {
        if (attachmentRef != null && o.attachmentRef.getId() != null) {
            return ComparisonChain.start()
                    .compare(this.attachmentRef.getId(), o.attachmentRef.getId())
                    .result();
        }
        if (attachmentRef != null && o.attachmentRef.getId() == null) {
            return 1;
        }
        if (attachmentRef == null && o.attachmentRef.getId() != null) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return toJSON().toString(2);
    }
}
