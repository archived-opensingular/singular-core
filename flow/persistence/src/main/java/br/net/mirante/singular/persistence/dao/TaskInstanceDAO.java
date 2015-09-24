package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.ProcessInstance;
import br.net.mirante.singular.persistence.entity.TaskInstance;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.io.Serializable;

public class TaskInstanceDAO extends AbstractHibernateDAO<TaskInstance> {


    public TaskInstanceDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    public TaskInstance retrieveById(Serializable id) {
        return (TaskInstance) getSession().load(TaskInstance.class, id);
    }
}
