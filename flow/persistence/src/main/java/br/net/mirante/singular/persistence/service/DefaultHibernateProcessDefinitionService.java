package br.net.mirante.singular.persistence.service;

import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.service.IProcessEntityService;
import br.net.mirante.singular.persistence.dao.ProcessDefinitionDAO;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.Process;
import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.Task;
import br.net.mirante.singular.persistence.entity.TaskDefinition;
import br.net.mirante.singular.persistence.entity.Transition;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class DefaultHibernateProcessDefinitionService extends AbstractHibernateService implements IProcessEntityService<Category,
        ProcessDefinition,
        Process,
        TaskDefinition,
        Task,
        Transition
        > {


    private final ProcessDefinitionDAO processDefinitionDAO = new ProcessDefinitionDAO(getSessionLocator());


    public DefaultHibernateProcessDefinitionService(SessionLocator sessionLocator){
        super(sessionLocator);
    }

    @Override
    public ProcessDefinition retrieveOrcreateEntityProcessDefinitionFor(br.net.mirante.singular.flow.core.ProcessDefinition<?> definicao) {
        requireNonNull(definicao);
        ProcessDefinition def = processDefinitionDAO.retrievePorSigla(definicao.getAbbreviation());
        if (def == null) {
            def = new ProcessDefinition();
            def.setCategory(retrieveOrCreateCategoryWith(definicao.getCategory()));
            def.setName(definicao.getName());
            def.setAbbreviation(definicao.getAbbreviation());
            def.setDefinitionClassName(definicao.getClass().getName());

            processDefinitionDAO.save(def);
            processDefinitionDAO.refresh(def);
        } else {
            boolean mudou = false;
            if (!definicao.getName().equals(def.getName())) {
                def.setName(definicao.getName());
                mudou = true;
            }
            Category categoria = retrieveOrCreateCategoryWith(definicao.getCategory());
            if (!Objects.equals(def.getCategory(), categoria)) {
                def.setCategory(retrieveOrCreateCategoryWith(definicao.getCategory()));
                mudou = true;
            }
            if (!definicao.getClass().getName().equals(def.getDefinitionClassName())) {
                def.setDefinitionClassName(definicao.getClass().getName());
                mudou = true;
            }
            if (mudou) {
                processDefinitionDAO.update(def);
            }
        }

        return def;
    }

    @Override
    public void checkRoleDefChanges(br.net.mirante.singular.flow.core.ProcessDefinition<?> definicao, ProcessDefinition entityProcessDefinition) {

    }

    @Override
    public Process createEntityProcess(ProcessDefinition entityProcessDefinition) {
        return null;
    }

    @Override
    public Category retrieveOrCreateCategoryWith(String name) {
        return null;
    }

    @Override
    public Transition createEntityTaskTransition(MTransition mTransition, Task originTask, Task destinationTask) {
        return null;
    }

    @Override
    public Task createEntityTask(Process process, MTask<?> task) {
        return null;
    }

    @Override
    public TaskDefinition retrieveOrCreateEntityDefinitionTask(ProcessDefinition process, MTask<?> task) {
        TaskDefinition taskDefinition = (TaskDefinition) process.getTaskDefinition(task.getAbbreviation());
        if (taskDefinition == null) {
            taskDefinition = new TaskDefinition();
            taskDefinition.setAbbreviation(task.getAbbreviation());
            taskDefinition.setProcessDefinition(process);
        }
        return taskDefinition;
    }
}
