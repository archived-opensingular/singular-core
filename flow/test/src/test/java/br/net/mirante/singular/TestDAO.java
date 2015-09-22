package br.net.mirante.singular;

import br.net.mirante.singular.persistence.entity.TaskType;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

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

}
