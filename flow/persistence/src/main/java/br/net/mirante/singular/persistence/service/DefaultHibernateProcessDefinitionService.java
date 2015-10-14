package br.net.mirante.singular.persistence.service;

import java.util.ArrayList;

import br.net.mirante.singular.flow.core.IEntityTaskType;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.Process;
import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.RoleInstance;
import br.net.mirante.singular.persistence.entity.Task;
import br.net.mirante.singular.persistence.entity.TaskDefinition;
import br.net.mirante.singular.persistence.entity.TaskType;
import br.net.mirante.singular.persistence.entity.Transition;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class DefaultHibernateProcessDefinitionService
        extends AbstractHibernateProcessDefinitionService<Category, ProcessDefinition, Process, TaskDefinition, Task, Transition, Role, RoleInstance> {

    public DefaultHibernateProcessDefinitionService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    @Override
    protected Class<ProcessDefinition> getClassProcessDefinition() {
        return ProcessDefinition.class;
    }

    @Override
    protected Class<? extends Role> getClassProcessRoleDef() {
        return Role.class;
    }
    
    @Override
    protected Class<RoleInstance> getClassProcessRole() {
        return RoleInstance.class;
    }

    @Override
    protected Class<Category> getClassCategory() {
        return Category.class;
    }

    @Override
    public Process createEntityProcessVersion(ProcessDefinition entityProcessDefinition) {
        Process entityProcess = new Process();
        entityProcess.setProcessDefinition(entityProcessDefinition);
        entityProcess.setTasks(new ArrayList<>());
        return entityProcess;
    }

    @Override
    public Transition createEntityTaskTransition(Task originTask, Task destinationTask) {
        Transition taskEntity = new Transition();
        taskEntity.setOriginTask(originTask);
        taskEntity.setDestinationTask(destinationTask);
        return taskEntity;
    }

    @Override
    public Task createEntityTaskVersion(Process process, TaskDefinition entityTaskDefinition, MTask<?> task) {
        Task taskEntity = new Task();
        taskEntity.setProcess(process);
        taskEntity.setTaskDefinition(entityTaskDefinition);
        // TODO Daniel: essa solução do createType deveria ser unificada entre
        // as implementações
        taskEntity.setType(createTaskType(task.getEffectiveTaskType()));
        taskEntity.setTransitions(new ArrayList<>());
        return taskEntity;
    }

    private TaskType createTaskType(IEntityTaskType entityTaskType) {
        br.net.mirante.singular.flow.core.TaskType effectiveTaskType = ((br.net.mirante.singular.flow.core.TaskType) entityTaskType);
        TaskType taskType = new TaskType();
        taskType.setCod((long)effectiveTaskType.ordinal() + 1L);
        return taskType;
    }

    @Override
    protected TaskDefinition createEntityDefinitionTask(ProcessDefinition process) {
        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setProcessDefinition(process);
        return taskDefinition;
    }
}
