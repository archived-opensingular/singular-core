/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.service;

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

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.commons.base.SingularUtil;
import br.net.mirante.singular.form.persistence.dao.AttachmentContentDao;
import br.net.mirante.singular.form.persistence.dao.AttachmentDao;
import br.net.mirante.singular.form.persistence.dto.AttachmentRef;
import br.net.mirante.singular.form.persistence.entity.AttachmentContentEntitty;
import br.net.mirante.singular.form.persistence.entity.AttachmentEntity;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;

@Transactional
public class AttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntitty> implements IAttachmentPersistenceHandler<AttachmentRef> {

    @Inject
    private AttachmentDao<T, C> attachmentDao;

    @Inject
    private AttachmentContentDao<C> attachmentContentDao;

    @Override
    public AttachmentRef addAttachment(File file, long length, String name) {
        try (FileInputStream fs = new FileInputStream(file)){
            T attachment = attachmentDao.insert(fs, length, name);
            return new AttachmentRef(attachment);
        } catch (IOException e) {
            throw new SingularException(e);
        }
    }

    @Override
    public AttachmentRef copy(IAttachmentRef toBeCopied) {
        try(InputStream is = toBeCopied.getInputStream()){
            T file = attachmentDao.insert(is, toBeCopied.getSize(), toBeCopied.getName());
            return new AttachmentRef(file);
        } catch (IOException e) {
            throw SingularUtil.propagate(e);
        }
    }

    @Override
    public List<AttachmentRef> getAttachments() {
        return attachmentDao.list().stream().map(AttachmentRef::new).collect(Collectors.toList());
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

    public T getAttachmentEntity(IAttachmentRef ref){
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
