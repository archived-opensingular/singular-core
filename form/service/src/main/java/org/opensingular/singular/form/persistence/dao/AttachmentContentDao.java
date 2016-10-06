/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.persistence.dao;

import java.io.InputStream;
import java.util.Date;

import javax.transaction.Transactional;

import org.opensingular.singular.commons.base.SingularException;
import org.opensingular.form.io.HashAndCompressInputStream;
import org.opensingular.singular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.singular.support.persistence.BaseDAO;

@SuppressWarnings({"serial","unchecked"})
@Transactional(Transactional.TxType.MANDATORY)
public class AttachmentContentDao<T extends AttachmentContentEntitty> extends BaseDAO<T, Long> {

    public AttachmentContentDao() {
        super((Class<T>) AttachmentContentEntitty.class);
    }
    
    protected AttachmentContentDao(Class<T> tipo) {
        super(tipo);
    }
    
    public T insert(T o) {
        getSession().save(o);
        return o;
    }

    public T insert(InputStream is, long length) {
        return insert(createContent(is, length));
    }
    
    public void delete(Long codContent) {
        T contentEntitty = find(codContent);
        delete(contentEntitty);
    }

    protected T createContent(InputStream in, long length) {
        HashAndCompressInputStream inHash = new HashAndCompressInputStream(in);
        T fileEntity = createInstance();
        fileEntity.setContent(getSession().getLobHelper().createBlob(inHash, length));
        fileEntity.setHashSha1(inHash.getHashSHA1());
        fileEntity.setSize(length);
        fileEntity.setInclusionDate(new Date());
        return fileEntity;
    }
    
    protected T createInstance() {
        try {
            return tipo.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SingularException(e);
        }
    }
}