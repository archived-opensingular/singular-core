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

package org.opensingular.form.persistence.dao;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Criteria;

import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

@SuppressWarnings("serial")
@Transactional(Transactional.TxType.MANDATORY)
public class AttachmentDao<T extends AttachmentEntity, C extends AttachmentContentEntitty> extends BaseDAO<T, Long> {

    @Inject
    private AttachmentContentDao<C> attachmentContentDao;
    
    public AttachmentDao() {
        super((Class<T>) AttachmentEntity.class);
    }

    public AttachmentDao(Class<T> tipo) {
        super(tipo);
    }

    public T insert(T o) {
        getSession().save(o);
        return o;
    }

    public T insert(InputStream is, long length, String name){
        C content = attachmentContentDao.insert(is, length);
        return insert(createAttachment(content, name));
    }

    public void delete(Long id) {
        T t = get(id);
        Long codContent = t.getCodContent();
        delete(t);
        attachmentContentDao.delete(codContent);
    }

    public List<T> list() {
        Criteria crit = getSession().createCriteria(tipo);
        return crit.list();
    }

    protected T createAttachment(C content, String name) {
        
        T fileEntity = createInstance();
        
        fileEntity.setCodContent(content.getCod());
        fileEntity.setHashSha1(content.getHashSha1());
        fileEntity.setSize(content.getSize());
        fileEntity.setCreationDate(new Date());
        fileEntity.setName(name);
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