/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.entity.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.hibernate.Criteria;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.hibernate.jdbc.Work;

@SuppressWarnings("unchecked")
public class SessionWrapper {

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
            throw new RuntimeException(e);
        }
    }

    // -----------------------------------------------------------------------------
    // Métodos facilitadores
    // -----------------------------------------------------------------------------

    public void validar(Object obj) {
        // TODO decidir se chamara automaticamente o método validar
        // HibernateIntegration.validar(obj);
    }

    public Serializable save(Object novoObj) {
        validar(novoObj);
        Serializable pk = getSession().save(novoObj);
        flush();
        return pk;
    }

    public void saveOrUpdate(Object obj) {
        validar(obj);
        getSession().saveOrUpdate(obj);
        flush();
    }

    public void saveOrUpdate(Stream<? extends Serializable> objs) {
        objs.forEach(o -> {
            validar(o);
            getSession().saveOrUpdate(o);
        });
        flush();
    }

    public void saveOrUpdate(Serializable... objs) {
        for (Object t : objs) {
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
    public void deleteByPk(Class<? extends Serializable> tipo, Serializable... ids) {
        if (ids != null && ids.length > 0) {
            for (Serializable id : ids) {
                if (id != null) {
                    Serializable obj = retrieve(tipo, id);
                    if (obj != null) {
                        getSession().delete(obj);
                    }
                }
            }
        }
        flush();
    }

    public void deleteByPk(Class<? extends Serializable> tipo, Serializable id) {
        Serializable obj = retrieve(tipo, id);
        if (obj != null) {
            delete(obj);
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
    public <T> T retrieve(Class<T> classe, Serializable id) {
        try {
            Object o = getSession().get(classe, id);
            return classe.cast(o);
        } catch (ObjectNotFoundException e) {
            return null;
        }
    }

    public <T> T retrieveOrException(Class<T> classe, Serializable id) {
        Object o = getSession().get(classe, id);
        Objects.requireNonNull(o, "Não foi encontrado " + classe.getName() + " de pk=" + id);
        return classe.cast(o);
    }

    public <T> List<T> retrieve(Class<T> classe, Collection<? extends Serializable> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> lista = new ArrayList<>(ids.size());
        for (Serializable id : ids) {
            T t = retrieve(classe, id);
            if (t != null) {
                lista.add(t);
            }
        }
        return lista;
    }

    /**
     * Carrega todas as instancia do BD (fazendo cache) e retorna a primeira
     * instância que atende a condição informada.
     *
     * @return Null senão encontrada
     */
    public <T> T retrieveFirstFromCachedRetriveAll(Class<T> classe, Predicate<T> filtro) {
        return retrieveAll(classe, true).stream().filter(filtro).findFirst().orElse(null);
    }

    /**
     * Retorna a primeira entidade no BD com a propriedade no valor indicado.
     * Não faz cache da consulta.
     *
     * @return NUll senão encontrada.
     */
    public <T> T retrieveByUniqueProperty(Class<T> classe, String prop, Object value) {
        return classe.cast(createCriteria(classe, false).add(Restrictions.eq(prop, value)).uniqueResult());
    }

    /**
     * Retorna todas as intância do banco sem fazer cache.
     *
     * @return Nunca null
     */
    public <T> List<T> retrieveAll(Class<T> classe) {
        return createCriteria(classe).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    /**
     * Retorna todas as intância do banco podendo ou não faze cache.
     *
     * @return Nunca null
     */
    public <T> List<T> retrieveAll(Class<T> classe, boolean cacheResult) {
        return createCriteria(classe, cacheResult).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setCacheable(cacheResult).list();
    }

    public Criteria createCriteria(Class<?> tipoCriteria) {
        return getSession().createCriteria(tipoCriteria);
    }

    public Criteria createCriteria(Class<?> tipoCriteria, String alias) {
        return getSession().createCriteria(tipoCriteria, alias);
    }

    public Criteria createCriteria(Class<?> tipoCriteria, boolean fazerCache) {
        return getSession().createCriteria(tipoCriteria).setCacheable(fazerCache);
    }

    /**
     * Metodo Responsavel por fazer o merge com um objeto na base de dados, a
     * escolha
     */
    public <T> T merge(T novoObj) {
        return (T) getSession().merge(novoObj);
    }

    /**
     * Metodo responsavel por sincronizar um objeto da sessao com o atual no
     * banco.
     */
    public void refresh(Object obj) {
        getSession().refresh(obj);
    }

    public <T> T refreshByPk(Class<T> classe, Serializable id) {
        Object o = getSession().get(classe, id);
        getSession().refresh(o);
        return classe.cast(o);
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

    public void evict(List<Object> lista) {
        for (Object t : lista) {
            evict(t);
        }
    }

    public void evictByPk(Class<?> classe, Serializable id) {
        Object o = getSession().get(classe, id);
        if (o != null) {
            getSession().evict(o);
        }
    }

    public void doWork(Work work) {
        getSession().doWork(work);
    }
}
