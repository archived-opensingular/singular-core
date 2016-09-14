/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.persistence.dao.AttachmentContentDao;
import br.net.mirante.singular.form.persistence.dao.AttachmentDao;
import br.net.mirante.singular.form.persistence.dto.AttachmentRef;
import br.net.mirante.singular.form.persistence.entity.AttachmentContentEntitty;
import br.net.mirante.singular.form.persistence.entity.AttachmentEntity;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;

public class AttachmentPersistenceService<T extends AttachmentEntity, C extends AttachmentContentEntitty> implements IAttachmentPersistenceHandler<AttachmentRef> {

    @Inject
    private AttachmentDao<T, C> fileDao;

    @Inject
    private AttachmentContentDao<C> attachmentContentDao;

    @Override
    @Transactional
    public AttachmentRef addAttachment(File file, long length, String name) {
        try (FileInputStream fs = new FileInputStream(file)){
            T attachment = fileDao.insert(fs, length, name);
            return new AttachmentRef(attachment);
        } catch (IOException e) {
            throw new SingularException(e);
        }
    }

    @Override
    @Transactional
    public AttachmentRef copy(IAttachmentRef toBeCopied) {
        T file = fileDao.insert(toBeCopied.newInputStream(), toBeCopied.getSize(), toBeCopied.getName());
        return new AttachmentRef(file);
    }

    @Override
    @Transactional
    public List<AttachmentRef> getAttachments() {
        return fileDao.list().stream().map(AttachmentRef::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IAttachmentRef getAttachment(String fileId) {
        AttachmentEntity ref = fileDao.find(Long.valueOf(fileId));
        Hibernate.initialize(ref);
        return new AttachmentRef(ref);
    }

    @Override
    @Transactional
    public void deleteAttachment(String id) {
        fileDao.delete(Long.valueOf(id));
    }

    @Transactional
    public Blob loadAttachmentContent(Long codContent) {
        C content = attachmentContentDao.find(codContent);
        if(content == null){
            throw new SingularException("Attachment Content not found id="+codContent);
        }
        return content.getContent();
    }


}
