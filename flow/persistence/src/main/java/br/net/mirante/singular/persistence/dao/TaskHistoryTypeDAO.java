package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.TaskHistoryType;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class TaskHistoryTypeDAO extends AbstractHibernateDAO<TaskHistoryType> {

    public TaskHistoryTypeDAO(SessionLocator sessionLocator) {
        super(TaskHistoryType.class, sessionLocator);
    }
}
