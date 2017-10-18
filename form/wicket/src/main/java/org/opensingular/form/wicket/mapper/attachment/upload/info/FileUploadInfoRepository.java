/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.wicket.mapper.attachment.upload.info;

import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.opensingular.lib.commons.util.Loggable;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

public class FileUploadInfoRepository implements Loggable, Serializable {

    private final ConcurrentHashSet<FileUploadInfo> fileUploadInfos;

    public FileUploadInfoRepository() {
        this(new ConcurrentHashSet<>());
    }

    public FileUploadInfoRepository(ConcurrentHashSet<FileUploadInfo> fileUploadInfos) {
        this.fileUploadInfos = fileUploadInfos;
    }

    public synchronized Optional<FileUploadInfo> findByID(String fid) {
        getLogger().debug("findFileInfo({})", fid);
        return fileUploadInfos.stream()
                .filter(it -> it.getAttachmentRef().getId().equals(fid))
                .findAny();
    }

    public boolean add(FileUploadInfo info) {
        return fileUploadInfos.add(info);
    }

    public boolean remove(FileUploadInfo info) {
        return fileUploadInfos.remove(info);
    }

    public Stream<FileUploadInfo> stream() {
        return fileUploadInfos.stream();
    }

}
