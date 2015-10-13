package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.TaskInstance;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class TaskInstanceDAO extends AbstractHibernateDAO<TaskInstance> {

    public TaskInstanceDAO(SessionLocator sessionLocator) {
        super(TaskInstance.class, sessionLocator);
    }
}
