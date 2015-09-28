package br.net.mirante.singular;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.persistence.entity.ExecutionVariable;
import br.net.mirante.singular.persistence.entity.TaskInstanceHistory;
import br.net.mirante.singular.persistence.entity.TaskType;
import br.net.mirante.singular.persistence.entity.Variable;
import br.net.mirante.singular.persistence.entity.VariableType;

@SuppressWarnings("JpaQlInspection")
@Named
@Transactional
public class TestDAO {

    @Inject
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<TaskType> listTaskType() {
        return (List<TaskType>) getSession().createCriteria(TaskType.class).list();
    }

    public void save(Object o) {
        getSession().save(o);
    }

    public void update(Object o) {
        getSession().update(o);
    }

    @SuppressWarnings("unchecked")
    public <T> T restrieveById(Class<T> clazz, Serializable cod) {
        return (T) getSession().load(clazz, cod);
    }

    public void refresh(Object entity) {
        getSession().refresh(entity);
    }

    public Integer countHistoty() {
        return ((Number) getSession()
                .createCriteria(TaskInstanceHistory.class).setProjection(Projections.count("cod"))
                .uniqueResult()).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstanceHistory> retrieveLastHistories(int count) {
        return getSession().createCriteria(TaskInstanceHistory.class).setMaxResults(count)
                .addOrder(Order.desc("cod")).list();
    }

    @SuppressWarnings("unchecked")
    public List<br.net.mirante.singular.persistence.entity.ProcessInstance> findAllProcessInstancesByDefinition(IEntityProcess entity) {
        return getSession().createQuery(
                "select pi from ProcessInstance pi inner join pi.process p where p.cod = :id"
        ).setParameter("id", entity.getCod()).list();
    }

    @SuppressWarnings("unchecked")
    public List<Variable> retrieveVariablesByInstance(Integer id) {
        return getSession().createQuery(
                "select v from Variable v inner join v.processInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }

    @SuppressWarnings("unchecked")
    public List<ExecutionVariable> retrieveExecutionVariablesByInstance(Integer id) {
        return getSession().createQuery(
                "select v from ExecutionVariable v inner join v.processInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }

    @SuppressWarnings("unchecked")
    public List<VariableType> retrieveVariablesTypesByInstance(Integer id) {
        return getSession().createQuery(
                "select distinct vt from Variable v inner join v.type vt inner join v.processInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
