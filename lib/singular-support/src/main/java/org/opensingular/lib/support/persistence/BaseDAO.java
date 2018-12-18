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

package org.opensingular.lib.support.persistence;


import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.MirrorList;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.query.Query;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

import javax.annotation.Nonnull;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class BaseDAO<T extends BaseEntity, ID extends Serializable> extends SimpleDAO {

    protected Class<T> entityClass;

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public ID save(T newEntity) {
        return (ID) getSession().save(newEntity);
    }

    public void saveOrUpdate(T newEntity) {
        getSession().saveOrUpdate(newEntity);
    }

    @Nonnull
    public Optional<T> get(@Nonnull ID id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable((T) getSession().get(entityClass, id));
    }

    public T getOrException(@Nonnull ID id) {
        Optional<T> result = get(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw SingularException.rethrow("Não foi encontrado a entidade " + entityClass.getName() + " com ID=" + id);
    }

    @Nonnull
    public Optional<T> find(@Nonnull ID id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable(getSession().get(entityClass, id));
    }

    @Nonnull
    public T findOrException(@Nonnull ID id) {
        Optional<T> result = find(Objects.requireNonNull(id));
        if (result.isPresent()) {
            return result.get();
        }
        throw SingularException.rethrow("Não foi encontrado a entidade " + entityClass.getName() + " com ID=" + id);
    }

    public List<T> listAll() {
        return getSession().createQuery("select e from " + entityClass.getName() + " e ", entityClass).list();
    }

    public T merge(T newEntity) {
        return (T) getSession().merge(newEntity);
    }

    public void delete(T obj) {
        getSession().delete(obj);
    }

    public void evict(Object o) {
        getSession().evict(o);
    }

    public List<T> findByProperty(String propertyName, String value) {
        return findByProperty(propertyName, value, null, null);
    }

    public List<T> findByProperty(String propertyName, String value, Integer maxResults) {
        return findByProperty(propertyName, value, null, maxResults);
    }

    public T findByUniqueProperty(String propertyName, Object value) {
        CriteriaBuilder  criteriaBuilder = getSession().getCriteriaBuilder();
        CriteriaQuery<T> query           = criteriaBuilder.createQuery(entityClass);
        Root<T>          root            = query.from(entityClass);
        query.where(criteriaBuilder.equal(root.get(propertyName), value));
        return getSession().createQuery(query).setMaxResults(1).uniqueResult();
    }

    public List<T> findByExample(T filter) {
        return findByExample(filter, null);
    }

    public List<T> findByExample(T filter, Integer maxResults) {
        try {
            CriteriaBuilder  criteriaBuilder = getSession().getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery   = criteriaBuilder.createQuery(entityClass);
            Root<T>          root            = criteriaQuery.from(entityClass);

            MirrorList<Field> properties = new Mirror().on(entityClass).reflectAll().fields();

            for (Field f : properties) {
                f.setAccessible(true);
                if (Modifier.isTransient(f.getModifiers()) || Modifier.isStatic(f.getModifiers()) || f.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                Object value = f.get(filter);
                if (value != null && (!(value instanceof Collection) || !((Collection) value).isEmpty())) {
                    criteriaQuery.where(criteriaBuilder.equal(root.get(f.getName()), value));
                }
            }

            Query<T> query = getSession().createQuery(criteriaQuery);

            if (maxResults != null) {
                query.setMaxResults(maxResults);
            }

            return query.list();
        } catch (IllegalAccessException e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> findByProperty(String propertyName, String value, MatchMode matchMode, Integer maxResults) {
        CriteriaBuilder  criteriaBuilder = getSession().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery   = criteriaBuilder.createQuery(entityClass);
        Root<T>          root            = criteriaQuery.from(entityClass);

        if (value != null && !value.isEmpty()) {
            MatchMode mode = matchMode == null ? MatchMode.EXACT : matchMode;
            criteriaQuery.where(criteriaBuilder.like(root.get(propertyName), mode.toMatchString(value)));
        }

        Query<T> query = getSession().createQuery(criteriaQuery);

        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }

        return query.list();
    }

    /**
     * Executa o critéria buscando apenas um resultado e garante que o resultado seja da classe especificada.
     */
    protected final static <K> Optional<K> findUniqueResult(Class<K> expectedResultClass, Criteria criteria) {
        Object result = criteria.setMaxResults(1).uniqueResult();
        return Optional.ofNullable(expectedResultClass.cast(result));
    }

    /**
     * Executa a consulta buscando apenas um resultado e garante que o resultado seja da classe especificada.
     */
    protected final static <K> Optional<K> findUniqueResult(Class<K> expectedResultClass, Query query) {
        Object result = query.setMaxResults(1).uniqueResult();
        return Optional.ofNullable(expectedResultClass.cast(result));
    }

    public void flush() {
        getSession().flush();
    }
}
