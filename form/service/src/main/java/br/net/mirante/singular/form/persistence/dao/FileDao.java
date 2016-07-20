/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.hibernate.Criteria;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.io.HashAndCompressInputStream;
import br.net.mirante.singular.form.persistence.entity.AbstractAttachmentEntity;
import br.net.mirante.singular.form.persistence.entity.Attachment;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.support.persistence.BaseDAO;

@SuppressWarnings("serial")
@Transactional(Transactional.TxType.MANDATORY)
public class FileDao<T extends AbstractAttachmentEntity> extends BaseDAO<T, Long> {

    public FileDao() {
        super((Class<T>) Attachment.class);
    }

    public FileDao(Class<T> tipo) {
        super(tipo);
    }

    public T insert(T o) {
        getSession().save(o);
        return o;
    }

    public T insert(InputStream is, long length) throws IOException {
        return insert(createFile(is, length));
    }

    public void delete(String hashId) {
        T file = createInstance();
        file.setId(hashId);
        remove(file);
        file.deleteTempFile();
    }

    @Transactional
    public void remove(T o) {
        getSession().delete(o);
    }

    public List<T> list() {
        Criteria crit = getSession().createCriteria(tipo);
        return crit.list();
    }

    public IAttachmentRef find(String fileId) {
        return (IAttachmentRef) getSession().get(tipo, fileId);
    }

    private T createFile(InputStream in, long length) throws IOException {
        HashAndCompressInputStream inHash = new HashAndCompressInputStream(in);
        T fileEntity = createInstance();
        fileEntity.setId(UUID.randomUUID().toString());
        fileEntity.setRawContent(getSession().getLobHelper().createBlob(inHash, length));
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

}