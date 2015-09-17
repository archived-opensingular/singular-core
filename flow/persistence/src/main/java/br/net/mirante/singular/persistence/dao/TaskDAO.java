package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.Task;
import br.net.mirante.singular.persistence.entity.TaskInstance;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.io.Serializable;

public class TaskDAO extends AbstractHibernateDAO<Task> {


    public TaskDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    public Task retrieveById(Serializable id) {
        return (Task) getSession().load(Task.class, id);
    }
}
