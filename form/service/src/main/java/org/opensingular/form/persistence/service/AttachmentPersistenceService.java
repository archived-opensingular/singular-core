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

package org.opensingular.form.persistence.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.dao.AttachmentContentDao;
import org.opensingular.form.persistence.dao.AttachmentDao;
import org.opensingular.form.persistence.dto.AttachmentRef;
import org.opensingular.form.persistence.entity.AttachmentContentEntity;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.type.core.attachment.AttachmentCopyContext;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.base.SingularUtil;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class AttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntity> implements IAttachmentPersistenceHandler<AttachmentRef> {

    @Inject
    protected AttachmentDao<T, C> attachmentDao;

    @Inject
    protected AttachmentContentDao<C> attachmentContentDao;

    @Override
    public AttachmentRef addAttachment(File file, long length, String name, String hashSha1) {
        try (FileInputStream fs = new FileInputStream(file)) {
            T attachment = attachmentDao.insert(fs, length, name, hashSha1);
            return createRef(attachment);
        } catch (IOException e) {
            throw SingularException.rethrow(e);
        }
    }

    @Override
    public AttachmentCopyContext<AttachmentRef> copy(IAttachmentRef attachmentRef, SDocument document) {
        try (InputStream is = attachmentRef.getContentAsInputStream()) {
            T file = attachmentDao.insert(is, attachmentRef.getSize(), attachmentRef.getName(), attachmentRef.getHashSHA1());
            return new AttachmentCopyContext<>(createRef(file));
        } catch (IOException e) {
            throw SingularUtil.propagate(e);
        }
    }

    @Override
    public List<AttachmentRef> getAttachments() {
        return attachmentDao.list().stream().map(this::createRef).collect(Collectors.toList());
    }

    @Override
    public AttachmentRef getAttachment(String fileId) {
        if (StringUtils.isNumeric(fileId)) {
            return new AttachmentRef(attachmentDao.findOrException(Long.valueOf(fileId)));
        }
        return null;
    }

    @Override
    public void deleteAttachment(String id, SDocument document) {
        attachmentDao.delete(Long.valueOf(id));
    }

    public void deleteAttachmentAndContent(AttachmentEntity attachment) {
        attachmentDao.delete(attachment.getCod());
    }

    public AttachmentRef createRef(T attachmentEntity) {
        return new AttachmentRef(attachmentEntity);
    }

    @Nonnull
    public T getAttachmentEntity(@Nonnull IAttachmentRef ref) {
        return getAttachmentEntity(ref.getId());
    }

    @Nonnull
    public T getAttachmentEntity(@Nonnull String id) {
        return attachmentDao.findOrException(Long.valueOf(id));
    }

    public void loadAttachmentContent(Long codContent, OutputStream fos) {
        Optional<C> content = attachmentContentDao.find(codContent);
        if (! content.isPresent()) {
            throw SingularException.rethrow("Attachment Content not found id=" + codContent);
        }
        try (InputStream in = content.get().getContent().getBinaryStream()) {
            IOUtils.copy(in, fos);
        } catch (SQLException | IOException e) {
            throw SingularException.rethrow("couldn't copy content to outputstream", e);
        }
    }

    public List<AttachmentEntity> listOldOrphanAttachments() {
        return attachmentDao.listOldOrphanAttachments();
    }
}
