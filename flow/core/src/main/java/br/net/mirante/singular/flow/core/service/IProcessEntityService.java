package br.net.mirante.singular.flow.core.service;

import java.util.List;

import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskTransition;

public interface IProcessEntityService<CATEGORY extends IEntityCategory, PROCESS_DEF extends IEntityProcessDefinition,
        PROCESS extends IEntityProcessVersion, TASK_DEF extends IEntityTaskDefinition, TASK extends IEntityTaskVersion,
        TRANSITION extends IEntityTaskTransition> {

    /**
     * Generates a new {@link IEntityProcessVersion} if {@link ProcessDefinition} is
     * new or has changed
     */
    @SuppressWarnings("unchecked")
    default PROCESS generateEntityFor(ProcessDefinition<?> processDefinition) {
        PROCESS_DEF entityProcessDefinition = retrieveOrcreateEntityProcessDefinitionFor(processDefinition);

        checkRoleDefChanges(processDefinition, entityProcessDefinition);

        PROCESS entityProcess = createEntityProcess(entityProcessDefinition);

        for (MTask<?> task : processDefinition.getFlowMap().getAllTasks()) {
            TASK entityTask = createEntityTask(entityProcess, task);
            ((List<TASK>) entityProcess.getTasks()).add(entityTask);
        }
        for (MTask<?> task : processDefinition.getFlowMap().getAllTasks()) {
            TASK originTask = (TASK) entityProcess.getTask(task.getAbbreviation());
            for (MTransition mTransition : task.getTransitions()) {
                TASK destinationTask = (TASK) entityProcess.getTask(mTransition.getDestination().getAbbreviation());

                TRANSITION entityTransition = createEntityTaskTransition(mTransition, originTask, destinationTask);
                ((List<TRANSITION>) originTask.getTransitions()).add(entityTransition);
            }
        }
        return entityProcess;
    }

    PROCESS_DEF retrieveOrcreateEntityProcessDefinitionFor(ProcessDefinition<?> definicao);

    void checkRoleDefChanges(ProcessDefinition<?> definicao, PROCESS_DEF entityProcessDefinition);

    PROCESS createEntityProcess(PROCESS_DEF entityProcessDefinition);

    CATEGORY retrieveOrCreateCategoryWith(String name);

    TRANSITION createEntityTaskTransition(MTransition mTransition, TASK originTask, TASK destinationTask);

    TASK createEntityTask(PROCESS process, MTask<?> task);

    TASK_DEF retrieveOrCreateEntityDefinitionTask(PROCESS_DEF process, MTask<?> task);

    default boolean isNewVersion(IEntityProcessVersion oldEntity, IEntityProcessVersion newEntity) {
        if (oldEntity == null || oldEntity.getTasks().size() != newEntity.getTasks().size()) {
            return true;
        }
        for (IEntityTaskVersion newEntitytask : newEntity.getTasks()) {
            IEntityTaskVersion oldEntityTask = oldEntity.getTask(newEntitytask.getAbbreviation());
            if (isNewVersion(oldEntityTask, newEntitytask)) {
                return true;
            }
        }
        return false;
    }

    default boolean isNewVersion(IEntityTaskVersion oldEntityTask, IEntityTaskVersion newEntitytask) {
        if (oldEntityTask == null
                || !oldEntityTask.getName().equalsIgnoreCase(newEntitytask.getName())
                || !oldEntityTask.getType().getAbbreviation().equals(newEntitytask.getType().getAbbreviation())
                || oldEntityTask.getTransitions().size() != newEntitytask.getTransitions().size()) {
            return true;
        }
        for (IEntityTaskTransition newEntityTaskTransition : newEntitytask.getTransitions()) {
            IEntityTaskTransition oldEntityTaskTransition = oldEntityTask.getTransition(newEntityTaskTransition.getAbbreviation());
            if (isNewVersion(oldEntityTaskTransition, newEntityTaskTransition)) {
                return true;
            }
        }
        return false;
    }

    default boolean isNewVersion(IEntityTaskTransition oldEntityTaskTransition, IEntityTaskTransition newEntityTaskTransition) {
        return oldEntityTaskTransition == null
                || !oldEntityTaskTransition.getName().equalsIgnoreCase(newEntityTaskTransition.getName())
                || !oldEntityTaskTransition.getAbbreviation().equalsIgnoreCase(newEntityTaskTransition.getAbbreviation())
                || oldEntityTaskTransition.getType() != newEntityTaskTransition.getType()
                || !oldEntityTaskTransition.getDestinationTask().getAbbreviation().equalsIgnoreCase(newEntityTaskTransition.getDestinationTask().getAbbreviation());
    }
}
