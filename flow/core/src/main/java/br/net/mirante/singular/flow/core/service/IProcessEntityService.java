package br.net.mirante.singular.flow.core.service;

import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskTransition;

public interface IProcessEntityService {

    /**
     * Generates a new {@link IEntityProcess} if {@link ProcessDefinition} is new or has changed
     * 
     * @param processDefinition
     * @return
     */
    IEntityProcess generateEntityFor(ProcessDefinition<?> processDefinition);

    default boolean isNewVersion(IEntityProcess oldEntity, IEntityProcess newEntity) {
        if (oldEntity == null || oldEntity.getTasks().size() != newEntity.getTasks().size()) {
            return true;
        }
        for (IEntityTask oldEntityTask : oldEntity.getTasks()) {
            IEntityTask newEntitytask = newEntity.getTask(oldEntityTask.getAbbreviation());
            if (isNewVersion(oldEntityTask, newEntitytask)) {
                return true;
            }
        }
        return false;
    }

    default boolean isNewVersion(IEntityTask oldEntityTask, IEntityTask newEntitytask) {
        if (newEntitytask == null
            || !oldEntityTask.getName().equalsIgnoreCase(newEntitytask.getName())
            || !oldEntityTask.getType().equals(newEntitytask.getType())
            || oldEntityTask.getTransitions().size() != newEntitytask.getTransitions().size()) {
            return true;
        }
        for (IEntityTaskTransition oldEntityTaskTransition : oldEntityTask.getTransitions()) {
            IEntityTaskTransition newEntityTaskTransition = newEntitytask.getTransition(oldEntityTaskTransition.getAbbreviation());
            if (isNewVersion(oldEntityTaskTransition, newEntityTaskTransition)) {
                return true;
            }
        }
        return false;
    }

    default boolean isNewVersion(IEntityTaskTransition oldEntityTaskTransition, IEntityTaskTransition newEntityTaskTransition) {
        if (newEntityTaskTransition == null
            || !oldEntityTaskTransition.getName().equalsIgnoreCase(newEntityTaskTransition.getName())
            || !oldEntityTaskTransition.getAbbreviation().equalsIgnoreCase(newEntityTaskTransition.getAbbreviation())
            || oldEntityTaskTransition.getType() != newEntityTaskTransition.getType()
            || !oldEntityTaskTransition.getDestinationTask().getAbbreviation().equalsIgnoreCase(newEntityTaskTransition.getDestinationTask().getAbbreviation())) {
            return true;
        }
        return false;
    }
}
