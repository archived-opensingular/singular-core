package br.net.mirante.singular.flow.core.builder;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.IExecutionDateStrategy;
import br.net.mirante.singular.flow.core.IRoleChangeListener;
import br.net.mirante.singular.flow.core.MProcessRole;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTaskEnd;
import br.net.mirante.singular.flow.core.MTaskJava;
import br.net.mirante.singular.flow.core.MTaskPeople;
import br.net.mirante.singular.flow.core.MTaskWait;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.ProcessScheduledJob;
import br.net.mirante.singular.flow.core.RoleAccessStrategy;
import br.net.mirante.singular.flow.core.TaskAccessStrategy;
import br.net.mirante.singular.flow.core.ITaskPredicate;
import br.net.mirante.singular.flow.core.UserRoleSettingStrategy;

public abstract class FlowBuilder<DEF extends ProcessDefinition<?>, MAPA extends FlowMap, BUILDER_JAVA extends BJava<?>,
        BUILDER_PEOPLE extends BPeople<?>, BUILDER_WAIT extends BWait<?>, BUILDER_END extends BEnd<?>, BUILDER_TRANSITION extends BTransition<?>, BUILDER_PAPEL extends BProcessRole<?>> {

    private final MAPA flowMap;

    public FlowBuilder(DEF processDefinition) {
        flowMap = newFlowMap(processDefinition);
    }

    protected abstract MAPA newFlowMap(DEF processDefinition);

    protected abstract BUILDER_JAVA newJavaTask(MTaskJava task);

    protected abstract BUILDER_PEOPLE newPeopleTask(MTaskPeople task);

    protected abstract BUILDER_WAIT newWaitTask(MTaskWait task);

    protected abstract BUILDER_END newEndTask(MTaskEnd task);

    protected abstract BUILDER_TRANSITION newTransition(MTransition transition);

    protected abstract BUILDER_PAPEL newProcessRole(MProcessRole transicao);

    protected final MAPA getFlowMap() {
        return flowMap;
    }

    public MAPA build() {
        return flowMap;
    }

    public void setStartTask(BTask initialTask) {
        getFlowMap().setStartTask(initialTask.getTask());
    }

    public <T extends ProcessInstance> void setRoleChangeListener(IRoleChangeListener<T> roleChangeListener) {
        getFlowMap().setRoleChangeListener(roleChangeListener);
    }

    private BTask toBuilder(MTask<?> task) {
        if (task instanceof MTaskPeople) {
            return newPeopleTask((MTaskPeople) task);
        } else if (task instanceof MTaskJava) {
            return newJavaTask((MTaskJava) task);
        } else if (task instanceof MTaskWait) {
            return newWaitTask((MTaskWait) task);
        } else if (task instanceof MTaskEnd) {
            return newEndTask((MTaskEnd) task);
        }
        throw new RuntimeException("Task type " + task.getClass().getName() + " not supported");
    }

    public void forEach(Consumer<BTask> consumer) {
        getFlowMap().getTasks().stream().map(t -> toBuilder(t)).forEach(consumer);
    }

    public BUILDER_PAPEL addRoleDefinition(String description,
            UserRoleSettingStrategy<? extends ProcessInstance> userRoleSettingStrategy,
            boolean automaticUserAllocation) {
        return newProcessRole(getFlowMap().addRoleDefinition(description, userRoleSettingStrategy, automaticUserAllocation));
    }

    public BUILDER_JAVA addJavaTask(String name) {
        return newJavaTask(getFlowMap().addJavaTask(name));
    }

    public BUILDER_PEOPLE addPeopleTask(String name) {
        return newPeopleTask(getFlowMap().addPeopleTask(name));
    }

    public BUILDER_PEOPLE addPeopleTask(String name, TaskAccessStrategy<?> accessStrategy) {
        BUILDER_PEOPLE task = newPeopleTask(getFlowMap().addPeopleTask(name));
        if (accessStrategy != null) {
            task.addAccessStrategy(accessStrategy);
        }
        return task;
    }

    public BUILDER_PEOPLE addPeopleTask(String name, BProcessRole<?> requiredRole) {
        return addPeopleTask(name, RoleAccessStrategy.of(requiredRole.getProcessRole()));
    }

    public BUILDER_PEOPLE addPeopleTask(String name, BProcessRole<?> requiredExecutionRole, BProcessRole<?> requiredVisualizeRole) {
        return addPeopleTask(name, RoleAccessStrategy.of(requiredExecutionRole.getProcessRole(), requiredVisualizeRole.getProcessRole()));
    }

    public BUILDER_WAIT addWaitTask(String name) {
        return newWaitTask(getFlowMap().addWaitTask(name));
    }

    public <T extends ProcessInstance> BUILDER_WAIT addWaitTask(String name, IExecutionDateStrategy<T> executionDateStrategy) {
        return newWaitTask(getFlowMap().addWaitTask(name, executionDateStrategy));
    }

    public <T extends ProcessInstance> BUILDER_WAIT addWaitTask(String name, IExecutionDateStrategy<T> executionDateStrategy,
            TaskAccessStrategy<?> accessStrategy) {
        BUILDER_WAIT wait = addWaitTask(name, executionDateStrategy);
        wait.addAccessStrategy(accessStrategy);
        return wait;
    }

    public BUILDER_END addEnd() {
        return addEnd("End");
    }

    public BUILDER_END addEnd(String name) {
        return newEndTask(getFlowMap().addFim(name));
    }

    public BUILDER_TRANSITION addTransition(BTask origin, String actionName, BTask destination, boolean showTransitionInExecution) {
        return newTransition(origin.getTask().addTransition(actionName, destination.getTask(), showTransitionInExecution));
    }

    public BUILDER_TRANSITION addTransition(BTask origin, String actionName, BTask destination) {
        return newTransition(origin.getTask().addTransition(actionName, destination.getTask()));
    }

    public BUILDER_TRANSITION addTransition(BTask origin, BTask destination) {
        return newTransition(origin.getTask().addTransition(destination.getTask()));
    }

    public BUILDER_TRANSITION addAutomaticTransition(BTask origin, ITaskPredicate condition, BTask destination) {
        return newTransition(origin.getTask().addAutomaticTransition(condition, destination.getTask()));
    }

    public ProcessScheduledJob addScheduledJob(Supplier<Object> impl, String name) {
        return getFlowMap().addScheduledJob(name).call(impl);
    }

    public ProcessScheduledJob addScheduledJob(Runnable impl, String name) {
        return getFlowMap().addScheduledJob(name).call(impl);
    }

    public void deleteInstancesFinalizedOlderThan(int time, TimeUnit timeUnit) {
        getFlowMap().deleteInstancesFinalizedOlderThan(time, timeUnit);
    }

    public void addTasksVisualizeStrategy(TaskAccessStrategy<?> accessVisualizeStrategy) {
        getFlowMap().getAllTasks().stream().forEach(t -> t.addVisualizeStrategy(accessVisualizeStrategy));
    }

    public void addTasksVisualizeStrategy(TaskAccessStrategy<?> accessVisualizeStrategy, Predicate<MTask<?>> applyToPredicate) {
        getFlowMap().getAllTasks().stream().filter(applyToPredicate).forEach(t -> t.addVisualizeStrategy(accessVisualizeStrategy));
    }
}