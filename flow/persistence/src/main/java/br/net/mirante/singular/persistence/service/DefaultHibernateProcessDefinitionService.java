package br.net.mirante.singular.persistence.service;

import br.net.mirante.singular.flow.core.MProcessRole;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.service.IProcessEntityService;
import br.net.mirante.singular.persistence.dao.ProcessDefinitionDAO;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.Process;
import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.Task;
import br.net.mirante.singular.persistence.entity.TaskDefinition;
import br.net.mirante.singular.persistence.entity.Transition;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class DefaultHibernateProcessDefinitionService extends AbstractHibernateService implements IProcessEntityService<Category,
        ProcessDefinition,
        Process,
        TaskDefinition,
        Task,
        Transition
        > {


    private final ProcessDefinitionDAO processDefinitionDAO = new ProcessDefinitionDAO(getSessionLocator());


    public DefaultHibernateProcessDefinitionService(SessionLocator sessionLocator) {
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
    public void checkRoleDefChanges(br.net.mirante.singular.flow.core.ProcessDefinition<?> processDefinition, ProcessDefinition entityProcessDefinition) {
        Set<String> abbreviations = new HashSet<>();
        for (Role role : new ArrayList<>(entityProcessDefinition.getRoles())) {
            MProcessRole roleAbbreviation = processDefinition.getFlowMap().getRoleWithAbbreviation(role.getAbbreviation());
            if (roleAbbreviation == null) {
                if(role.getRolesInstances().isEmpty()){
                    entityProcessDefinition.getRoles().remove(role);
                    processDefinitionDAO.delete(role);
                }
            } else {
                if (!role.getName().equals(roleAbbreviation.getName())
                        || !role.getAbbreviation().equals(roleAbbreviation.getAbbreviation())) {
                    role.setName(roleAbbreviation.getName());
                    role.setAbbreviation(roleAbbreviation.getAbbreviation());
                    processDefinitionDAO.update(role);
                }
                abbreviations.add(role.getAbbreviation());
            }
        }

        for (MProcessRole mPapel : processDefinition.getFlowMap().getRoles()) {
            if (!abbreviations.contains(mPapel.getAbbreviation())) {
                final Role role = new Role();
                role.setProcessDefinition(entityProcessDefinition);
                role.setName(mPapel.getName());
                role.setAbbreviation(mPapel.getAbbreviation());
                processDefinitionDAO.save(role);
            }
        }
        processDefinitionDAO.refresh(entityProcessDefinition);
    }

    @Override
    public Process createEntityProcess(ProcessDefinition entityProcessDefinition) {
        Process entityProcess = new Process();
        entityProcess.setProcessDefinition(entityProcessDefinition);
        entityProcess.setVersionDate(new Date());
        return entityProcess;
    }

    @Override
    public Category retrieveOrCreateCategoryWith(String name) {
        requireNonNull(name);
        Category category = processDefinitionDAO.retrieveByUniqueProperty(Category.class, "name", name);
        if (category == null) {
            category = new Category();
            category.setName(name);
            processDefinitionDAO.save(category);
        }
        return category;
    }

    @Override
    public Transition createEntityTaskTransition(MTransition mTransition, Task originTask, Task destinationTask) {
        Transition taskEntity = new Transition();
        taskEntity.setAbbreviation(mTransition.getAbbreviation());
        taskEntity.setName(mTransition.getName());
        taskEntity.setType(mTransition.getType());
        taskEntity.setOriginTask(originTask);
        taskEntity.setDestinationTask(destinationTask);
        return taskEntity;
    }

    @Override
    public Task createEntityTask(Process process, MTask<?> task) {
        Task taskEntity = new Task();
        taskEntity.setName(task.getName());
        taskEntity.setProcess(process);
        taskEntity.setType(task.getEffectiveTaskType());
        taskEntity.setTaskDefinition(retrieveOrCreateEntityDefinitionTask(process.getProcessDefinition(), task));
        return taskEntity;
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
