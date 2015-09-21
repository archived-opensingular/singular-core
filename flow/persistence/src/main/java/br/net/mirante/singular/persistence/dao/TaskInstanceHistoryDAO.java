package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.TaskHistoryType;
import br.net.mirante.singular.persistence.entity.TaskInstanceHistory;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.io.Serializable;

public class TaskInstanceHistoryDAO extends AbstractHibernateDAO<TaskInstanceHistory> {


    public TaskInstanceHistoryDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    public TaskInstanceHistory retrieveById(Serializable id) {
        return (TaskInstanceHistory) getSession().load(TaskInstanceHistory.class, id);
    }

}
