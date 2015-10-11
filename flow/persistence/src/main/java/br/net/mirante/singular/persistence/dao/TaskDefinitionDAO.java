package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.TaskDefinition;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class TaskDefinitionDAO extends AbstractHibernateDAO<TaskDefinition> {


    public TaskDefinitionDAO(SessionLocator sessionLocator) {
        super(TaskDefinition.class, sessionLocator);
    }

}
