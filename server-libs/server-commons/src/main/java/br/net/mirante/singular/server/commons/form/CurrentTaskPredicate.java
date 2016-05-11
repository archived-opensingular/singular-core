package br.net.mirante.singular.server.commons.form;

import java.util.Optional;
import java.util.function.Predicate;

import br.net.mirante.singular.flow.core.builder.ITaskDefinition;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.server.commons.wicket.view.form.AbstractFormContent;

/**
 * Used to match Current Task, retrieved from ProcessFormService, and those
 * informed.
 * Helper methods in and notIn are here to help.
 */
public class CurrentTaskPredicate implements Predicate<SInstance>{
    private final ITaskDefinition[] referenceTasks;
    private final boolean negate;
    private TaskInstanceEntity currentTask;

    public static CurrentTaskPredicate  in(ITaskDefinition ... referenceTask){
        return new CurrentTaskPredicate(false, referenceTask);
    }

    public static CurrentTaskPredicate notIn(ITaskDefinition ... referenceTask){
        return new CurrentTaskPredicate(true , referenceTask);
    }

    public static CurrentTaskPredicate hasNoCurrentTask(){
        return new NoCurrentTaskPredicate();
    }

    public CurrentTaskPredicate(boolean negate, ITaskDefinition ... referenceTasks) {
        this.negate = negate;
        this.referenceTasks = referenceTasks;

    }

    @Override
    public boolean test(SInstance x) {
        updateCurrentTask(x);
        Boolean result = getCurrentTask()
                .map(this::matchesReferenceTask).orElse(false);
        if (negate) return !result;
        return result ;
    }

    protected Optional<TaskInstanceEntity> getCurrentTask() {
        return Optional.ofNullable(currentTask);
    }

    private boolean matchesReferenceTask(TaskInstanceEntity t) {
        for(ITaskDefinition ref : referenceTasks){
            if(ref.getName().equalsIgnoreCase(t.getTask().getName())){
                return true;
            }
        }
        return false;
    }

    protected void updateCurrentTask(SInstance x) {
        currentTask = currentTask(x);
    }

    /**
     * If instance have a Task Associated with it, returns it.
     * @param x Instance where document contains task instance
     * @return Task if exists
     */
    //TODO: Fabs: This could be extracted right? Where to?
    public static TaskInstanceEntity currentTask(SInstance x) {
        return currentTask(taskService(x));
    }


    private static TaskInstanceEntity currentTask(AbstractFormContent.ProcessFormService s) {
        return Optional.ofNullable(s)
                .map(AbstractFormContent.ProcessFormService::getProcessInstance)
                .map(ProcessInstanceEntity::getCurrentTask).orElse(null);
    }

    private static AbstractFormContent.ProcessFormService taskService(SInstance x) {
        SDocument d = x.getDocument();
        return d.lookupService(AbstractFormContent.ProcessFormService.class);
    }
}

class NoCurrentTaskPredicate extends CurrentTaskPredicate {

    public NoCurrentTaskPredicate() {
        super(false);
    }

    @Override
    public boolean test(SInstance x) {
        updateCurrentTask(x);
        return !getCurrentTask().isPresent();
    }
}