package br.net.mirante.singular.flow.core;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

public class TaskActions {

    public static IConditionalTaskAction executeTransition(final ITaskPredicate predicate, final MTransition transicao) {
        return executeTransition(predicate, transicao.getName());
    }

    public static IConditionalTaskAction executeTransition(final ITaskPredicate predicate, final String destinationName) {
        TaskActionImpl executeTransition = new TaskActionImpl("Executar Transicao", taskInstance ->{
            taskInstance.log("Transição Automática", "motivo: " + predicate.getDescription(taskInstance));
            taskInstance.getProcessInstance().executeTransition(destinationName);
        });
        executeTransition.setCompleteDescription("Executar Transicao '" + destinationName + "'");
        
        return conditionalAction(predicate, executeTransition);
    }

    public static IConditionalTaskAction conditionalAction(ITaskPredicate predicate, ITaskAction action){
        return new ConditionalTaskActionImpl(predicate, action);
    }
    
    static class TaskActionImpl implements ITaskAction{
        private final String name;
        private final Consumer<TaskInstance> action;
        private String completeDescription;
        
        public TaskActionImpl(String name, Consumer<TaskInstance> action) {
            super();
            this.name = name;
            this.action = action;
        }

        @Override
        public void execute(TaskInstance taskInstance) {
            action.accept(taskInstance);
        }

        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public String getCompleteDescription() {
            return StringUtils.defaultIfBlank(completeDescription, ITaskAction.super.getCompleteDescription());
        }
        
        public void setCompleteDescription(String completeDescription) {
            this.completeDescription = completeDescription;
        }
    }
    
    static class ConditionalTaskActionImpl implements IConditionalTaskAction {

        private final ITaskPredicate predicate;
        private final ITaskAction action;

        ConditionalTaskActionImpl(ITaskPredicate predicate, ITaskAction action) {
            this.predicate = predicate;
            this.action = action;
        }

        @Override
        public ITaskPredicate getPredicate() {
            return predicate;
        }

        @Override
        public void execute(TaskInstance instanciaTarefa) {
            action.execute(instanciaTarefa);
        }

        @Override
        public String getName() {
            return action.getName();
        }

        @Override
        public String getCompleteDescription() {
            return action.getCompleteDescription();
        }

    }
}
