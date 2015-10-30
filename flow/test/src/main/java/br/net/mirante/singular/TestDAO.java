package br.net.mirante.singular;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.net.mirante.singular.persistence.entity.Actor;
import br.net.mirante.singular.persistence.util.Constants;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.persistence.entity.ExecutionVariableEntity;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskInstanceHistoryEntity;
import br.net.mirante.singular.persistence.entity.VariableInstanceEntity;
import br.net.mirante.singular.persistence.entity.VariableTypeInstance;

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
    public <T> T restrieveById(Class<T> clazz, Serializable cod) {
        return (T) getSession().load(clazz, cod);
    }

    public void refresh(Object entity) {
        getSession().refresh(entity);
    }

    public Integer countHistoty() {
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
    public List<br.net.mirante.singular.persistence.entity.ProcessInstanceEntity> findAllProcessInstancesByDefinition(IEntityProcessVersion entity) {
        return getSession().createQuery(
                "select pi from "+ProcessInstanceEntity.class.getSimpleName()+" pi inner join pi.processVersion p where p.cod = :id"
        ).setParameter("id", entity.getCod()).list();
    }

    @SuppressWarnings("unchecked")
    public List<VariableInstanceEntity> retrieveVariablesByInstance(Integer id) {
        return getSession().createQuery(
                "select v from "+VariableInstanceEntity.class.getSimpleName()+" v inner join v.processInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }

    @SuppressWarnings("unchecked")
    public List<ExecutionVariableEntity> retrieveExecutionVariablesByInstance(Integer id) {
        return getSession().createQuery(
                "select v from "+ExecutionVariableEntity.class.getSimpleName()+" v inner join v.processInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }

    @SuppressWarnings("unchecked")
    public List<VariableTypeInstance> retrieveVariablesTypesByInstance(Integer id) {
        return getSession().createQuery(
                "select distinct vt from "+VariableInstanceEntity.class.getSimpleName()+" v inner join v.type vt inner join v.processInstance pi where pi.cod = :id"
        ).setParameter("id", id).list();
    }

    public Actor getSomeUser(int index){
        SQLQuery query = getSession().createSQLQuery("select CO_ATOR as \"cod\", CO_USUARIO as \"codUsuario\", NO_ATOR as \"nome\", DS_EMAIL as \"email\" FROM " + Constants.SCHEMA + ".VW_ATOR ");
        query.setResultTransformer(new AliasToBeanResultTransformer(Actor.class));
        query.addScalar("cod", StandardBasicTypes.INTEGER);
        query.addScalar("nome", StandardBasicTypes.STRING);
        query.addScalar("email", StandardBasicTypes.STRING);
        query.addScalar("codUsuario", StandardBasicTypes.STRING);
        List<Actor> actors = query.list();
        return actors.get(index);
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
