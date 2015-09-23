package br.net.mirante.singular;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
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
}
