/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;

import br.net.mirante.singular.form.io.HashUtil;
import br.net.mirante.singular.form.persistence.entity.AbstractAttachmentEntity;
import br.net.mirante.singular.form.persistence.entity.Attachment;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.type.core.attachment.handlers.IdGenerator;
import br.net.mirante.singular.support.persistence.BaseDAO;

@SuppressWarnings("serial")
public class FileDao<T extends AbstractAttachmentEntity> extends BaseDAO<T, Long> implements IAttachmentPersistenceHandler<T> {

    public FileDao() {
        super((Class<T>) Attachment.class);
    }

    public FileDao(Class<T> tipo) {
        super(tipo);
    }

    @Inject
    private SessionFactory sessionFactory;
    private IdGenerator genereator = new IdGenerator();

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional
    public T insert(T o) {
        session().save(o);
        return o;
    }

    @Transactional
    public void remove(T o) {
        session().delete(o);
    }

    @Transactional
    public T find(String hash){
        return (T) session().createCriteria(tipo).add(Restrictions.eq("hashSha1", hash)).setMaxResults(1).uniqueResult();
    }
    
    @Override @Transactional
    public IAttachmentRef addAttachment(byte[] content) {
        return addAttachment(new ByteArrayInputStream(content));
    }

    @Override @Transactional
    public IAttachmentRef addAttachment(InputStream in) {
        try {
            byte[] byteArray = ByteStreams.toByteArray(in);
            String sha1 = HashUtil.toSHA1Base16(byteArray);
            return insert(createFile(byteArray, sha1));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private T createFile(byte[] byteArray, String sha1) {
        T file = createInstance();
        file.setId(genereator.generate(byteArray));
        file.setHashSha1(sha1);
        file.setRawContent(byteArray);
        file.setSize(byteArray.length);
        return file;
    }

    private T createInstance() {
        try {
            return tipo.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    @Override @Transactional
    public List<T> getAttachments() {
        Criteria crit = session().createCriteria(tipo);
        return crit.list();
    }

    @Override @Transactional
    public IAttachmentRef getAttachment(String hashId) {
        return find(hashId);
    }

    @Override @Transactional
    public void deleteAttachment(String hashId) {
        T file = createInstance();
        file.setId(hashId);
        remove(file);
    }

}