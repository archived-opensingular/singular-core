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
import java.util.Optional;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.joda.time.DateTime;
import org.opensingular.form.persistence.entity.AbstractFormAttachmentEntity;
import org.opensingular.form.persistence.entity.AttachmentContentEntity;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.support.persistence.BaseDAO;

@SuppressWarnings("serial")
@Transactional(Transactional.TxType.MANDATORY)
public class AttachmentDao<T extends AttachmentEntity, C extends AttachmentContentEntity> extends BaseDAO<T, Long> {

    @Inject
    private AttachmentContentDao<C> attachmentContentDao;

    public AttachmentDao() {
        super((Class<T>) AttachmentEntity.class);
    }

    public AttachmentDao(Class<T> entityClass) {
        super(entityClass);
    }

    public T insert(T o) {
        getSession().save(o);
        return o;
    }

    public T insert(InputStream is, long length, String name, String hashSha1) {
        C content = attachmentContentDao.insert(is, length, hashSha1);
        attachmentContentDao.flush();
        return insert(createAttachment(content, name));
    }

    public void delete(Long id) {
        Optional<T> t = get(id);
        if (t.isPresent()) {
            T    entity     = t.get();
            Long codContent = entity.getCodContent().getCod();
            delete(entity);
            attachmentContentDao.delete(codContent);
        }
    }

    public List<T> list() {
        Criteria crit = getSession().createCriteria(entityClass);
        return crit.list();
    }

    protected T createAttachment(C content, String name) {

        T fileEntity = createInstance();
        fileEntity.setCodContent(content);
        fileEntity.setHashSha1(content.getHashSha1());
        fileEntity.setSize(content.getSize());
        fileEntity.setCreationDate(new Date());
        fileEntity.setName(truncateNameIfNeeded(name));
        return fileEntity;
    }

    /**
     * Truncates file names too long to fit in the underlying database model
     * currently 200 characters.
     *
     * @return truncated file name preserving the original file extension
     */
    private String truncateNameIfNeeded(String s) {
        if (s != null && s.length() > 200) {
            String extension      = "";
            int    lastIndexOfDot = s.lastIndexOf('.');
            if (lastIndexOfDot > 0) {
                extension = s.substring(lastIndexOfDot, s.length());
            }
            String name = s.substring(0, 200 - extension.length());
            return (name + extension).substring(0, 200);
        }
        return s;
    }

    protected T createInstance() {
        try {
            return entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw SingularException.rethrow(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<AttachmentEntity> listOldOrphanAttachments() {
        StringBuilder hql = new StringBuilder();
        hql.append(" SELECT a FROM ").append(AttachmentEntity.class.getName()).append(" as a ");
        hql.append(" WHERE a.creationDate < :ontem ");
        hql.append(" AND NOT EXISTS ( ");
        hql.append("    SELECT 1 FROM ").append(AbstractFormAttachmentEntity.class.getName()).append(" as fa ");
        hql.append("    WHERE fa.cod.attachmentCod = a.cod ");
        hql.append(" ) ");

        Query query = getSession().createQuery(hql.toString());
        Date  ontem = new DateTime().minusDays(1).toDate();
        query.setParameter("ontem", ontem);

        return query.list();
    }

    @Override
    public Optional<T> find(Long aLong) {
        Optional<T> t = super.find(aLong);
        if (t.isPresent()) {
            Hibernate.initialize(t);
        }
        return t;
    }
}