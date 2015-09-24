package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.flow.util.vars.VarType;
import br.net.mirante.singular.persistence.entity.TaskHistoryType;
import br.net.mirante.singular.persistence.entity.VariableType;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.io.Serializable;

public class TaskHistoryTypeDAO extends AbstractHibernateDAO<TaskHistoryType> {


    public TaskHistoryTypeDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    public TaskHistoryType retrieveById(Serializable id) {
        return (TaskHistoryType) getSession().load(TaskHistoryType.class, id);
    }

    public TaskHistoryType retrieveByDescription(String description) {
        return retrieveByUniqueProperty(TaskHistoryType.class, "description", description);
    }

    public TaskHistoryType retrieveOrSave(String taskHistoryTypeDescription) {
        TaskHistoryType taskHistoryType = retrieveByDescription(taskHistoryTypeDescription);
        if (taskHistoryType == null) {
            taskHistoryType = new TaskHistoryType();
            taskHistoryType.setDescription(taskHistoryTypeDescription);
            save(taskHistoryType);
        }
        return taskHistoryType;
    }

}
