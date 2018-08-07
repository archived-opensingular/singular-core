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

import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.lib.commons.util.Loggable;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

public class UploadInfoRepository implements Loggable, Serializable {

    private final ConcurrentHashSet<UploadInfo> uploadInfos;

    public UploadInfoRepository() {
        this(new ConcurrentHashSet<>());
    }

    public UploadInfoRepository(ConcurrentHashSet<UploadInfo> uploadInfos) {
        this.uploadInfos = uploadInfos;
    }

    public synchronized Optional<UploadInfo> findByAttachmentKey(AttachmentKey attachmentKey) {
        getLogger().debug("findFileInfo({})", attachmentKey);
        return uploadInfos.stream()
                .filter(it -> it.getUploadId().equals(attachmentKey))
                .findAny();
    }

    public boolean add(UploadInfo info) {
        return uploadInfos.add(info);
    }

    public boolean remove(UploadInfo info) {
        return uploadInfos.remove(info);
    }

    public Stream<UploadInfo> stream() {
        return uploadInfos.stream();
    }
}
