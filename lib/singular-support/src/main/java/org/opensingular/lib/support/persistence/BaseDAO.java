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
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

import javax.annotation.Nonnull;
import javax.persistence.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class BaseDAO<T extends BaseEntity, ID extends Serializable> extends SimpleDAO {

    protected Class<T> tipo;

    public BaseDAO(Class<T> tipo) {
        this.tipo = tipo;
    }

    public ID save(T novoObj) {
        return (ID) getSession().save(novoObj);
    }

    public void saveOrUpdate(T novoObj) {
        getSession().saveOrUpdate(novoObj);
    }

    @Nonnull
    public Optional<T> get(@Nonnull ID id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable((T) getSession().get(tipo, id));
    }

    public T getOrException(@Nonnull ID id) {
        Optional<T> result = get(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw SingularException.rethrow("Não foi encontrado a entidade " + tipo.getName() + " com ID=" + id);
    }

    @Nonnull
    public Optional<T> find(@Nonnull ID id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable((T) getSession().createCriteria(tipo).add(Restrictions.idEq(id)).uniqueResult());
    }

    @Nonnull
    public T findOrException(@Nonnull ID id) {
        Optional<T> result = find(Objects.requireNonNull(id));
        if (result.isPresent()) {
            return result.get();
        }
        throw SingularException.rethrow("Não foi encontrado a entidade " + tipo.getName() + " com ID=" + id);
    }

    public List<T> listAll() {
        return getSession().createCriteria(tipo).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public T merge(T novoObj) {
        return (T) getSession().merge(novoObj);
    }

    public void delete(T obj) {
        getSession().delete(obj);
    }

    public void evict(Object o) {
        getSession().evict(o);
    }

    public <T> List<T> findByProperty(String propertyName, String value) {
        return findByProperty(propertyName, value, null, null);
    }

    public <T> List<T> findByProperty(String propertyName, String value, Integer maxResults) {
        return findByProperty(propertyName, value, null, maxResults);
    }

    public <T> T findByUniqueProperty(String propertyName, Object value) {
        return (T) getSession().createCriteria(tipo).add(Restrictions.eq(propertyName, value)).setMaxResults(1).uniqueResult();
    }

    public <T> List<T> findByExample(T filter) {
        return findByExample(filter, null);
    }


    public <T> List<T> findByExample(T filter, Integer maxResults) {
        try {
            Criteria criteria = getSession().createCriteria(tipo);
            MirrorList<Field> properties = new Mirror().on(tipo).reflectAll().fields();

            for (Field f : properties) {
                f.setAccessible(true);
                if (Modifier.isTransient(f.getModifiers()) || Modifier.isStatic(f.getModifiers()) || f.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                Object value = f.get(filter);
                if (value != null) {
                    criteria.add(Restrictions.eq(f.getName(), value));
                }
            }
            if (maxResults != null) {
                criteria.setMaxResults(maxResults);
            }

            return criteria.list();

        } catch (IllegalAccessException e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findByProperty(String propertyName, String value, MatchMode matchMode, Integer maxResults) {
        Criteria criteria = getSession().createCriteria(tipo);

        if (matchMode == null) {
            matchMode = MatchMode.EXACT;
        }

        if (value != null && !value.isEmpty()) {
            criteria.add(Restrictions.ilike(propertyName, value, matchMode));
        }

        if (maxResults != null) {
            criteria.setMaxResults(maxResults);
        }

        return criteria.list();
    }

    /** Executa o critéria buscando apenas um resultado e garante que o resultado seja da classe especificada. */
    protected final static <K> Optional<K> findUniqueResult(Class<K> expectedResultClass, Criteria criteria) {
        Object result = criteria.setMaxResults(1).uniqueResult();
        return Optional.ofNullable(expectedResultClass.cast(result));
    }

    /** Executa a consulta buscando apenas um resultado e garante que o resultado seja da classe especificada. */
    protected final static <K> Optional<K> findUniqueResult(Class<K> expectedResultClass, Query query) {
        Object result = query.setMaxResults(1).uniqueResult();
        return Optional.ofNullable(expectedResultClass.cast(result));
    }
}
