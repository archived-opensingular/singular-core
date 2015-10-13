package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.TaskInstanceHistory;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class TaskInstanceHistoryDAO extends AbstractHibernateDAO<TaskInstanceHistory> {


    public TaskInstanceHistoryDAO(SessionLocator sessionLocator) {
        super(TaskInstanceHistory.class, sessionLocator);
    }
}
