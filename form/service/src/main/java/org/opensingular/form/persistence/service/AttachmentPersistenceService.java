/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.persistence.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;

import org.opensingular.form.persistence.dao.AttachmentDao;
import org.opensingular.form.persistence.dto.AttachmentRef;
import org.opensingular.singular.commons.base.SingularException;
import org.opensingular.singular.commons.base.SingularUtil;
import org.opensingular.form.persistence.dao.AttachmentContentDao;
import org.opensingular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;

@Transactional
public class AttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntitty> implements IAttachmentPersistenceHandler<AttachmentRef> {

    @Inject
    private AttachmentDao<T, C> attachmentDao;

    @Inject
    private AttachmentContentDao<C> attachmentContentDao;

    @Override
    public AttachmentRef addAttachment(File file, long length, String name) {
        try (FileInputStream fs = new FileInputStream(file)) {
            T attachment = attachmentDao.insert(fs, length, name);
            return createRef(attachment);
        } catch (IOException e) {
            throw new SingularException(e);
        }
    }

    @Override
    public AttachmentRef copy(IAttachmentRef toBeCopied) {
        try (InputStream is = toBeCopied.getInputStream()) {
            T file = attachmentDao.insert(is, toBeCopied.getSize(), toBeCopied.getName());
            return createRef(file);
        } catch (IOException e) {
            throw SingularUtil.propagate(e);
        }
    }

    @Override
    public List<AttachmentRef> getAttachments() {
        return attachmentDao.list().stream().map(this::createRef).collect(Collectors.toList());
    }

    @Override
    public IAttachmentRef getAttachment(String fileId) {
        AttachmentEntity ref = attachmentDao.find(Long.valueOf(fileId));
        Hibernate.initialize(ref);
        return new AttachmentRef(ref);
    }

    @Override
    public void deleteAttachment(String id) {
        attachmentDao.delete(Long.valueOf(id));
    }

    public AttachmentRef createRef(T attachmentEntity) {
        return new AttachmentRef(attachmentEntity);
    }

    public T getAttachmentEntity(IAttachmentRef ref) {
        T entity = attachmentDao.find(Long.valueOf(ref.getId()));
        Hibernate.initialize(entity);
        return entity;
    }
    
    public Blob loadAttachmentContent(Long codContent) {
        C content = attachmentContentDao.find(codContent);
        if(content == null){
            throw new SingularException("Attachment Content not found id="+codContent);
        }
        return content.getContent();
    }

}
