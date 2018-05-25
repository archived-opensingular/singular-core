/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.persistence.service;

import net.vidageek.mirror.dsl.Mirror;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.persistence.exception.HibernateFormRepositoryException;
import org.opensingular.lib.support.persistence.entity.BaseEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public abstract class HibernateFormRepository<T extends BaseEntity
        , ID extends Serializable
        , TYPE extends SType<INSTANCE>
        , INSTANCE extends SInstance>
        implements FormRespository<TYPE, INSTANCE> {

    protected Class<T>    entityClass;
    protected Class<TYPE> sTypeClass;
    protected Class<ID>   idClass;

    @Inject
    private SDocumentFactory sDocumentFactory;

    @Inject
    private SessionFactory sessionFactory;

    public HibernateFormRepository(Class<T> entityClass, Class<TYPE> sTypeClass, Class<ID> idClass) {
        this.entityClass = entityClass;
        this.sTypeClass = sTypeClass;
        this.idClass = idClass;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Nonnull
    @Override
    public FormKey keyFromObject(@Nonnull Object objectValueToBeConverted) {
        if (objectValueToBeConverted instanceof BaseEntity) {
            return formKeyfromEntityCod((ID) ((BaseEntity) (objectValueToBeConverted)).getCod());
        } else {
            return formKeyfromEntityCod((ID) objectValueToBeConverted);
        }
    }

    public FormKey formKeyfromEntityCod(@Nonnull ID entityCod) {
        return new HibernateFormKey<>(entityCod);
    }

    public ID entityCodFromFormKey(@Nonnull FormKey formKey) {
        if (formKey instanceof HibernateFormKey) {
            return ((HibernateFormKey<ID>) formKey).getEntityCod();
        }
        throw new RuntimeException("A FormKey informada não é do tipo HibernateFormKey");
    }

    @Nonnull
    @Override
    public FormKey insert(@Nonnull INSTANCE instance, Integer inclusionActor) {
        final T entity = toEntity(instance, null);
        getSession().save(entity);
        final FormKey formKey = keyFromObject(entity);
        FormKey.setOnInstance(instance, formKey);
        return formKey;
    }

    @Override
    public void delete(@Nonnull FormKey key) {
        getSession().delete(getSession().get(entityClass, entityCodFromFormKey(key)));
    }

    @Override
    public void update(@Nonnull INSTANCE instance, Integer inclusionActor) {
        final T entity = toEntity(instance, entityCodFromFormKey(FormKey.fromInstance(instance)));
        getSession().update(entity);
    }

    @Nonnull
    @Override
    public FormKey insertOrUpdate(@Nonnull INSTANCE instance, Integer inclusionActor) {
        final T entity = toEntity(instance, FormKey.fromInstanceOpt(instance)
                .map(this::entityCodFromFormKey).orElse(null));
        getSession().saveOrUpdate(entity);
        FormKey.setOnInstance(instance, keyFromObject(entity));
        return FormKey.from(instance);
    }

    @Override
    public boolean isPersistent(@Nonnull INSTANCE instance) {
        return FormKey.fromInstanceOpt(instance).isPresent();
    }

    @Nonnull
    @Override
    public FormKey newVersion(@Nonnull INSTANCE instance, Integer inclusionActor, boolean keepAnnotations) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public INSTANCE load(@Nonnull FormKey key) {
        return Optional
                .ofNullable((T) getSession().get(entityClass, entityCodFromFormKey(key)))
                .map(this::toSinstanceSettingFormKey).orElseThrow(RuntimeException::new);
    }

    @Nonnull
    @Override
    public Optional<INSTANCE> loadOpt(@Nonnull FormKey key) {
        return Optional
                .ofNullable((T) getSession().get(entityClass, entityCodFromFormKey(key)))
                .map(this::toSinstanceSettingFormKey);
    }

    @Nonnull
    @Override
    public List<INSTANCE> loadAll(long first, long max) {
        return (List<INSTANCE>) getSession()
                .createCriteria(entityClass)
                .setFirstResult((int) first)
                .setMaxResults((int) max)
                .list()
                .stream()
                .map(i -> toSinstanceSettingFormKey((T) i))
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public List<INSTANCE> loadAll() {
        return (List<INSTANCE>) getSession()
                .createCriteria(entityClass)
                .list()
                .stream()
                .map(i -> toSinstanceSettingFormKey((T) i))
                .collect(Collectors.toList());
    }

    @Override
    public long countAll() {
        return (long) getSession().createQuery(" select count(*) from " + entityClass.getName()).uniqueResult();
    }

    @Override
    public INSTANCE createInstance() {
        return (INSTANCE) sDocumentFactory.createInstance(RefType.of(sTypeClass));
    }

    public INSTANCE toSinstanceSettingFormKey(T entity) {
        final INSTANCE instance = toSinstance(entity);
        FormKey.setOnInstance(instance, keyFromObject(entity));
        return instance;
    }

    private INSTANCE toSinstance(T entity) {
        final INSTANCE instance = createInstance();
        fillSInstance(instance, entity);
        return instance;
    }

    private T toEntity(INSTANCE instance, ID key) {
        final T entity;
        if (key == null) {
            try {
                entity = new Mirror().on(entityClass).reflect().constructor().withoutArgs().newInstance();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new HibernateFormRepositoryException(e);
            }
        } else {
            entity = (T) getSession().load(entityClass, key);
        }
        fillEntity(instance, entity);
        return entity;
    }

    public ID entityCodFromInstance(INSTANCE sinstance) {
        return FormKey.fromInstanceOpt(sinstance).map(this::entityCodFromFormKey).orElse(null);
    }

    public abstract void fillEntity(INSTANCE sinstance, T entity);

    public abstract void fillSInstance(INSTANCE sinstance, T entity);


    public T load(ID id) {
        return (T) getSession().load(entityClass, id);
    }

    public T get(ID id) {
        return (T) getSession().get(entityClass, id);
    }
}