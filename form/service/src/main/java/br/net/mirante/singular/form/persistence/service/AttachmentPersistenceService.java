/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.persistence.dao.FileDao;
import br.net.mirante.singular.form.persistence.entity.AbstractAttachmentEntity;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;

public class AttachmentPersistenceService<T extends AbstractAttachmentEntity> implements IAttachmentPersistenceHandler<T> {

    @Inject
    private FileDao<T> fileDao;

    @Override
    @Transactional
    public T addAttachment(File file, long length) {
        try {
            return fileDao.insert(new FileInputStream(file), length);
        } catch (IOException e) {
            throw new SingularException(e);
        }
    }

    @Override
    @Transactional
    public T copy(IAttachmentRef toBeCopied) {
        try {
            return fileDao.insert(toBeCopied.newInputStream(), toBeCopied.getSize());
        } catch (IOException e) {
            throw new SingularException(e);
        }
    }

    @Override
    @Transactional
    public List<T> getAttachments() {
        return fileDao.list();
    }

    @Override
    @Transactional
    public IAttachmentRef getAttachment(String fileId) {
        IAttachmentRef ref = fileDao.find(fileId);
        Hibernate.initialize(ref);
        return ref;
    }

    @Override
    @Transactional
    public void deleteAttachment(String hashId) {
        fileDao.delete(hashId);
    }


}
