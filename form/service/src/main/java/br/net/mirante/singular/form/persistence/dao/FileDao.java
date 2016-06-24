/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.dao;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.io.HashUtil;
import br.net.mirante.singular.form.persistence.entity.AbstractAttachmentEntity;
import br.net.mirante.singular.form.persistence.entity.Attachment;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("serial")
public class FileDao<T extends AbstractAttachmentEntity> extends BaseDAO<T, Long> implements IAttachmentPersistenceHandler<T> {

    @Inject
    private SessionFactory sessionFactory;

    public FileDao() {
        super((Class<T>) Attachment.class);
    }

    public FileDao(Class<T> tipo) {
        super(tipo);
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(Transactional.TxType.MANDATORY)
    public T insert(T o) {
        session().save(o);
        return o;
    }

    @Transactional
    public void remove(T o) {
        session().delete(o);
    }

    @Transactional
    public T find(String hash) {
        return (T) session().createCriteria(tipo).add(Restrictions.eq("hashSha1", hash)).setMaxResults(1).uniqueResult();
    }


    private T createFile(String sha1, InputStream in, long length) throws IOException {
        T fileEntity = createInstance();
        fileEntity.setId(UUID.randomUUID().toString());
        fileEntity.setHashSha1(sha1);
        fileEntity.setRawContent(session().getLobHelper().createBlob(in, length));
        fileEntity.setSize(length);
        return fileEntity;
    }

    private T createInstance() {
        try {
            return tipo.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SingularException(e);
        }
    }

    @Transactional
    @Override
    public IAttachmentRef addAttachment(File file, long length) {
        try {
            String sha1 = HashUtil.toSHA1Base16(file);
            return insert(createFile(sha1, new FileInputStream(file), length));
        } catch (IOException e) {
            throw new SingularException(e);
        }
    }

    @Transactional
    @Override
    public IAttachmentRef copy(IAttachmentRef toBeCopied) {
        try {
            return insert(createFile(toBeCopied.getHashSHA1(), toBeCopied.newInputStream(), toBeCopied.getSize()));
        } catch (IOException e) {
            throw new SingularException(e);
        }
    }

    @Override
    @Transactional
    public List<T> getAttachments() {
        Criteria crit = session().createCriteria(tipo);
        return crit.list();
    }

    @Override
    @Transactional
    public IAttachmentRef getAttachment(String hashId) {
        return find(hashId);
    }

    @Override
    @Transactional
    public void deleteAttachment(String hashId) {
        T file = createInstance();
        file.setId(hashId);
        remove(file);
        file.deleteTempFile();
    }

}