package br.net.mirante.singular;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.persistence.entity.ExecutionVariable;
import br.net.mirante.singular.persistence.entity.Variable;
import br.net.mirante.singular.persistence.entity.VariableType;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.persistence.entity.TaskInstanceHistory;
import br.net.mirante.singular.persistence.entity.TaskType;

@Named
@Transactional
public class TestDAO {

    @Inject
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<TaskType> listTaskType() {
        return (List<TaskType>) sessionFactory.getCurrentSession().createCriteria(TaskType.class).list();
    }

    public void save(Object o) {
        sessionFactory.getCurrentSession().save(o);
    }

    public void update(Object o) {
        sessionFactory.getCurrentSession().update(o);
    }

    @SuppressWarnings("unchecked")
    public <T> T restrieveById(Class<T> clazz, Serializable cod) {
        return (T) sessionFactory.getCurrentSession().load(clazz, cod);
    }

    public void refresh(Object entity) {
        sessionFactory.getCurrentSession().refresh(entity);
    }

    public Integer countHistoty() {
        return ((Number) sessionFactory.getCurrentSession()
                .createCriteria(TaskInstanceHistory.class).setProjection(Projections.count("cod"))
                .uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstanceHistory> retrieveLastHistories(int count) {
        return sessionFactory.getCurrentSession().createCriteria(TaskInstanceHistory.class).setMaxResults(count)
                .addOrder(Order.desc("cod")).list();
    }

    public List<br.net.mirante.singular.persistence.entity.ProcessInstance> findAllProcessInstancesByDefinition(IEntityProcess entity) {
        return sessionFactory.getCurrentSession().createQuery(
                "select pi from ProcessInstance pi inner join pi.process p where p.cod = :id"
        ).setParameter("id", entity.getCod()).list();
    }

    public List<Variable> retrieveVariablesByInstance(Long id) {
        return sessionFactory.getCurrentSession().createQuery(
                "select v from Variable v inner join v.processInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }

    public List<ExecutionVariable> retrieveExecutionVariablesByInstance(Long id) {
        return sessionFactory.getCurrentSession().createQuery(
                "select v from ExecutionVariable v inner join v.processInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }

    public List<VariableType> retrieveVariablesTypesByInstance(Long id) {
        return sessionFactory.getCurrentSession().createQuery(
                "select distinct vt from Variable v inner join v.type vt inner join v.processInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }
}
