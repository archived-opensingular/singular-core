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
package org.opensingular.form.persistence.dto;

import org.opensingular.form.document.SDocument;
import org.opensingular.form.io.CompressionUtil;
import org.opensingular.form.io.IOUtil;
import org.opensingular.form.persistence.entity.AttachmentContentEntity;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.service.AttachmentPersistenceService;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.lib.commons.io.TempFileInputStream;
import org.opensingular.lib.commons.util.TempFileUtils;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class AttachmentRef implements IAttachmentRef {

    private final String id;

    private final Long codContent;

    private final String hashSha1;

    private final long size;

    private final String name;

    private File file;

    public AttachmentRef(AttachmentEntity attachmentEntity) {
        this(attachmentEntity.getCod().toString(), attachmentEntity.getCodContent(), attachmentEntity.getHashSha1(), attachmentEntity.getSize(), attachmentEntity.getName());
    }

    public AttachmentRef(String id, Long codContent, String hashSha1, long size, String name) {
        super();
        this.id = id;
        this.codContent = codContent;
        this.hashSha1 = hashSha1;
        this.size = size;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getHashSHA1() {
        return hashSha1;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InputStream getContentAsInputStream() {
        try {
            if (file == null || !file.exists()) {

                AttachmentPersistenceService<AttachmentEntity, AttachmentContentEntity> persistenceHandler =
                        ApplicationContextProvider.get().getBean(SDocument.FILE_PERSISTENCE_SERVICE, AttachmentPersistenceService.class);

                file = File.createTempFile(hashSha1, id);
                file.deleteOnExit();

                try (OutputStream fos = IOUtil.newBufferedOutputStream(file)) {
                    persistenceHandler.loadAttachmentContent(codContent, fos);
                }
            }
            return CompressionUtil.inflateToInputStream(new TempFileInputStream(file));
        } catch (Exception e) {
            if (file != null) {
                TempFileUtils.deleteAndFailQuietly(file, getClass());
                file = null;
            }
            throw SingularUtil.propagate(e);
        }
    }

    public int hashCode() {
        String cod = getId();
        return (cod == null) ? super.hashCode() : cod.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AttachmentRef)) {
            return false;
        }
        AttachmentRef other = (AttachmentRef) obj;
        if (!((getId() == other.getId()) || (getId() != null && getId().equals(other.getId())))) {
            return false;
        }
        return true;
    }

}
