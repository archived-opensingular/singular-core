package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.Task;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class TaskDAO extends AbstractHibernateDAO<Task> {


    public TaskDAO(SessionLocator sessionLocator) {
        super(Task.class, sessionLocator);
    }
}
