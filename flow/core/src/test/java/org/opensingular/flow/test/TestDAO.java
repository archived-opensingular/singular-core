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

package org.opensingular.flow.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.flow.persistence.entity.ExecutionVariableEntity;
import org.opensingular.flow.persistence.entity.FlowInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskInstanceHistoryEntity;
import org.opensingular.flow.persistence.entity.VariableInstanceEntity;
import org.opensingular.flow.persistence.entity.VariableTypeInstance;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@Transactional
public class TestDAO {

    @Inject
    private SessionFactory sessionFactory;

    public void save(Object o) {
        getSession().save(o);
    }

    public void update(Object o) {
        getSession().update(o);
    }

    @SuppressWarnings("unchecked")
    public <T> T retrieveById(Class<T> clazz, Serializable cod) {
        return (T) getSession().load(clazz, cod);
    }

    public void refresh(Object entity) {
        getSession().refresh(entity);
    }

    public Integer countHistory() {
        return ((Number) getSession()
                .createCriteria(TaskInstanceHistoryEntity.class).setProjection(Projections.count("cod"))
                .uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstanceHistoryEntity> retrieveLastHistories(int count) {
        return getSession().createCriteria(TaskInstanceHistoryEntity.class).setMaxResults(count)
                .addOrder(Order.desc("cod")).list();
    }

    @SuppressWarnings("unchecked")
    public List<FlowInstanceEntity> findAllFlowInstancesByDefinition(IEntityFlowVersion entity) {
        return getSession().createQuery(
                "select pi from "+FlowInstanceEntity.class.getSimpleName()+" pi inner join pi.flowVersion p where p.cod = :id"
        ).setParameter("id", entity.getCod()).list();
    }

    @SuppressWarnings("unchecked")
    public List<VariableInstanceEntity> retrieveVariablesByInstance(Integer id) {
        return getSession().createQuery(
                "select v from "+VariableInstanceEntity.class.getSimpleName()+" v inner join v.flowInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }

    @SuppressWarnings("unchecked")
    public List<ExecutionVariableEntity> retrieveExecutionVariablesByInstance(Integer id) {
        return getSession().createQuery(
                "select v from "+ExecutionVariableEntity.class.getSimpleName()+" v inner join v.flowInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }

    @SuppressWarnings("unchecked")
    public List<VariableTypeInstance> retrieveVariablesTypesByInstance(Integer id) {
        return getSession().createQuery(
                "select distinct vt from "+VariableInstanceEntity.class.getSimpleName()+" v inner join v.type vt inner join v.flowInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }

    @SuppressWarnings("unchecked")
    public Actor getSomeUser(int index){
        List<Actor> actors = getSession().createCriteria(Actor.class).list();
        return actors.get(index);
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
