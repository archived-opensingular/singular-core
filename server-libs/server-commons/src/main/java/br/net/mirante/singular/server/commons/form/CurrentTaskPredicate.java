package br.net.mirante.singular.server.commons.form;

import br.net.mirante.singular.flow.core.builder.ITaskDefinition;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.server.commons.wicket.view.form.AbstractFormContent;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by nuk on 02/05/16.
 */
public class CurrentTaskPredicate implements Predicate<SInstance>{
    private final ITaskDefinition[] referenceTasks;
    private TaskInstanceEntity currentTask;

    public static CurrentTaskPredicate in(ITaskDefinition ... referenceTask){
        return new CurrentTaskPredicate(referenceTask);
    }

    public CurrentTaskPredicate(ITaskDefinition ... referenceTasks) {
        this.referenceTasks = referenceTasks;

    }

    @Override
    public boolean test(SInstance x) {
        updateCurrentTask(x);
        return Optional.ofNullable(currentTask).map(this::matchesReferenceTask
        ).orElse(false);
    }

    private boolean matchesReferenceTask(TaskInstanceEntity t) {
        for(ITaskDefinition ref : referenceTasks){
            if(ref.getName().equalsIgnoreCase(t.getTask().getName())){
                return true;
            }
        }
        return false;
    }

    private void updateCurrentTask(SInstance x) {
        AbstractFormContent.ProcessFormService s = taskService(x);
        Optional.ofNullable(s).ifPresent((service) -> {
            Optional.ofNullable(service.getProcessInstance()).ifPresent((pInstance) -> {
                Optional.ofNullable(pInstance.getCurrentTask()).ifPresent((task) -> {
                    currentTask = task;
                });
            });
        });
    }

    private AbstractFormContent.ProcessFormService taskService(SInstance x) {
        SDocument d = x.getDocument();
        return d.lookupService(AbstractFormContent.ProcessFormService.class);
    }
}