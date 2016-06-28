/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.dao;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.io.HashAndCompressInputStream;
import br.net.mirante.singular.form.persistence.entity.AbstractAttachmentEntity;
import br.net.mirante.singular.form.persistence.entity.Attachment;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.support.persistence.BaseDAO;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

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

    private T createFile(InputStream in, long length) throws IOException {
        HashAndCompressInputStream inHash = new HashAndCompressInputStream(in);
        T fileEntity = createInstance();
        fileEntity.setId(UUID.randomUUID().toString());
        fileEntity.setRawContent(session().getLobHelper().createBlob(inHash, length));
        fileEntity.setHashSha1(inHash.getHashSHA1());
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
    public T addAttachment(File file, long length) {
        try {
            return insert(createFile(new FileInputStream(file), length));
        } catch (IOException e) {
            throw new SingularException(e);
        }
    }

    @Transactional
    @Override
    public T copy(IAttachmentRef toBeCopied) {
        try {
            return insert(createFile(toBeCopied.newInputStream(), toBeCopied.getSize()));
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
    public IAttachmentRef getAttachment(String fileId) {
        IAttachmentRef ref = (IAttachmentRef) session().get(tipo, fileId);
        Hibernate.initialize(ref);
        return ref;
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