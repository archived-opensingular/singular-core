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

package org.opensingular.flow.persistence.entity.util;

import org.hibernate.Criteria;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.hibernate.jdbc.Work;
import org.opensingular.flow.core.SingularFlowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class SessionWrapper {

    public static final Logger LOGGER = LoggerFactory.getLogger(SessionWrapper.class);

    private final Session session;

    public SessionWrapper(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public void flush() {
        session.flush();
    }

    public void commitAndContinue() {
        try {
            session.flush();
            SessionImpl sessionImpl = (SessionImpl) session;
            Connection c = sessionImpl.connection();
            c.commit();
            if (!c.getAutoCommit()) {
                c.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new SingularFlowException(e);
        }
    }

    // -----------------------------------------------------------------------------
    // Métodos facilitadores
    // -----------------------------------------------------------------------------

    /**
     * TODO decidir se chamara automaticamente o método validar
     * HibernateIntegration.validar(obj);
     */
    public void validar(Object obj) {
    }

    public Serializable save(Object newEntity) {
        validar(newEntity);
        Serializable pk = getSession().save(newEntity);
        flush();
        return pk;
    }

    public void saveOrUpdate(Object entity) {
        validar(entity);
        getSession().saveOrUpdate(entity);
        flush();
    }

    public void saveOrUpdate(Stream<? extends Serializable> entities) {
        entities.forEach(o -> {
            validar(o);
            getSession().saveOrUpdate(o);
        });
        flush();
    }

    public void saveOrUpdate(Serializable... entities) {
        for (Object t : entities) {
            validar(t);
            getSession().saveOrUpdate(t);
        }
        flush();
    }

    public void saveOrUpdate(Iterable<?> objs) {
        for (Object t : objs) {
            validar(t);
            getSession().saveOrUpdate(t);
        }
        flush();
    }

    /**
     * Metodo responsavel por excluir uma lista de entidades da base de dados
     *
     * @param ids
     *            Identificadores das entidades a serem removidas da base de
     *            dados
     */
    public void deleteByPk(Class<? extends Serializable> entityClass, Serializable... ids) {
        if (ids != null && ids.length > 0) {
            for (Serializable id : ids) {
                if (id != null) {
                    Optional<? extends Serializable> obj = retrieve(entityClass, id);
                    obj.ifPresent(getSession()::delete);
                }
            }
        }
        flush();
    }

    public void deleteByPk(Class<? extends Serializable> entityClass, Serializable id) {
        Optional<? extends Serializable> obj = retrieve(entityClass, id);
        if (obj.isPresent()) {
            delete(obj.get());
        }
    }

    /**
     * Metodo Responsavel por excluir um conjunto de entidades persistentes da
     * base de dados
     *
     * @param objs
     *            Objetos a serem removidos da base de dados
     */
    public void delete(Serializable objs) {
        Object objMerged = getSession().merge(objs);
        getSession().delete(objMerged);
        flush();
    }

    /**
     * Metodo Responsavel por excluir um conjunto de entidades persistentes da
     * base de dados
     *
     * @param objs
     *            Objetos a serem removidos da base de dados
     */
    public void delete(Iterable<?> objs) {
        for (Object t : objs) {
            Object objMerged = getSession().merge(t);
            getSession().delete(objMerged);
        }
        flush();
    }

    public void update(Object obj) {
        validar(obj);
        getSession().update(obj);
        flush();
    }

    public void update(Serializable... objs) {
        for (Object t : objs) {
            validar(t);
            getSession().update(t);
        }
        flush();
    }

    public void update(Iterable<?> objs) {
        for (Object t : objs) {
            validar(t);
            getSession().update(t);
        }
        flush();
    }

    /**
     * Metodo responsavel por recuperar um objeto da base de dados
     *
     * @param id
     *            Identificador do objeto a ser recuperado
     * @return Objeto referente ao identificador informado
     */
    @Nonnull
    public <T> Optional<T> retrieve(@Nonnull Class<T> entityClass, @Nonnull Serializable id) {
        try {
            Object o = getSession().get(Objects.requireNonNull(entityClass), Objects.requireNonNull(id));
            return Optional.of(entityClass.cast(o));
        } catch (ObjectNotFoundException e) {
            LOGGER.error("", e);
            return Optional.empty();
        }
    }

    @Nonnull
    public <T> T retrieveOrException(@Nonnull Class<T> entityClass, @Nonnull Serializable id) {
        return retrieve(entityClass, id).orElseThrow(
                () -> new SingularFlowException("Não foi encontrado " + entityClass.getName() + " de pk=" + id));
    }

    public <T> List<T> retrieve(Class<T> entityClass, Collection<? extends Serializable> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(ids.size());
        for (Serializable id : ids) {
            T t = retrieveOrException(entityClass, id);
            list.add(t);
        }
        return list;
    }

    /**
     * Carrega todas as instancia do BD (fazendo cache) e retorna a primeira
     * instância que atende a condição informada.
     *
     * @return Null senão encontrada
     */
    public <T> T retrieveFirstFromCachedRetrieveAll(Class<T> entityClass, Predicate<T> filter) {
        return retrieveAll(entityClass, true).stream().filter(filter).findFirst().orElse(null);
    }

    /**
     * Retorna a primeira entidade no BD com a propriedade no valor indicado.
     * Não faz cache da consulta.
     *
     * @return NUll senão encontrada.
     */
    public <T> T retrieveByUniqueProperty(Class<T> entityClass, String prop, Object value) {
        return entityClass.cast(createCriteria(entityClass, false).add(Restrictions.eq(prop, value)).uniqueResult());
    }

    /**
     * Retorna todas as intância do banco sem fazer cache.
     *
     * @return Nunca null
     */
    public <T> List<T> retrieveAll(Class<T> entityClass) {
        return createCriteria(entityClass).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    /**
     * Retorna todas as intância do banco podendo ou não faze cache.
     *
     * @return Nunca null
     */
    public <T> List<T> retrieveAll(Class<T> typeCriteria, boolean cacheResult) {
        return createCriteria(typeCriteria, cacheResult).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setCacheable(cacheResult).list();
    }

    public Criteria createCriteria(Class<?> typeCriteria) {
        return getSession().createCriteria(typeCriteria);
    }

    public Criteria createCriteria(Class<?> typeCriteria, String alias) {
        return getSession().createCriteria(typeCriteria, alias);
    }

    public Criteria createCriteria(Class<?> typeCriteria, boolean cacheResult) {
        return getSession().createCriteria(typeCriteria).setCacheable(cacheResult);
    }

    /**
     * Metodo Responsavel por fazer o merge com um objeto na base de dados, a
     * escolha
     */
    public <T> T merge(T newEntity) {
        return (T) getSession().merge(newEntity);
    }

    /**
     * Metodo responsavel por sincronizar um objeto da sessao com o atual no
     * banco.
     */
    public void refresh(Object entity) {
        getSession().refresh(entity);
    }

    public <T> T refreshByPk(Class<T> entityClass, Serializable id) {
        Object o = getSession().get(entityClass, id);
        if (o != null) {
            getSession().refresh(o);
        }
        return entityClass.cast(o);
    }

    /**
     * Metodo responsavel por remover um objeto da sessao.
     *
     * @param obj
     *            objeto para remocao
     */
    public void evict(Object obj) {
        getSession().evict(obj);
    }

    public void evict(List<Object> entityList) {
        for (Object t : entityList) {
            evict(t);
        }
    }

    public void evictByPk(Class<?> entityClass, Serializable id) {
        Object o = getSession().get(entityClass, id);
        if (o != null) {
            getSession().evict(o);
        }
    }

    public void doWork(Work work) {
        getSession().doWork(work);
    }
}
