/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.builder;

import java.util.function.Consumer;
import java.util.function.Predicate;

import br.net.mirante.singular.commons.base.SingularUtil;
import br.net.mirante.singular.flow.core.DashboardView;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.IExecutionDateStrategy;
import br.net.mirante.singular.flow.core.IRoleChangeListener;
import br.net.mirante.singular.flow.core.ITaskPredicate;
import br.net.mirante.singular.flow.core.MProcessRole;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTaskEnd;
import br.net.mirante.singular.flow.core.MTaskJava;
import br.net.mirante.singular.flow.core.MTaskPeople;
import br.net.mirante.singular.flow.core.MTaskWait;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.RoleAccessStrategy;
import br.net.mirante.singular.flow.core.SingularFlowException;
import br.net.mirante.singular.flow.core.TaskAccessStrategy;
import br.net.mirante.singular.flow.core.UserRoleSettingStrategy;
import br.net.mirante.singular.flow.core.defaults.EmptyUserRoleSettingStrategy;
import br.net.mirante.singular.flow.core.defaults.NullPageStrategy;

public abstract class FlowBuilder<DEF extends ProcessDefinition<?>, MAPA extends FlowMap, BUILDER_TASK extends BTask, BUILDER_JAVA extends BJava<?>, BUILDER_PEOPLE extends BPeople<?>, BUILDER_WAIT extends BWait<?>, BUILDER_END extends BEnd<?>, BUILDER_TRANSITION extends BTransition<?>, BUILDER_PAPEL extends BProcessRole<?>, TASK_DEF extends ITaskDefinition> {

    private final MAPA flowMap;

    public FlowBuilder(DEF processDefinition) {
        flowMap = newFlowMap(processDefinition);
    }

    protected abstract MAPA newFlowMap(DEF processDefinition);

    protected abstract BUILDER_TASK newTask(MTask<?> task);

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

    public void setStartTask(TASK_DEF taskDefinition) {
        getFlowMap().setStartTask(getFlowMap().getTask(taskDefinition));
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
        throw new SingularFlowException("Task type " + task.getClass().getName() + " not supported");
    }

    public void forEach(Consumer<BTask> consumer) {
        getFlowMap().getTasks().stream().map(t -> toBuilder(t)).forEach(consumer);
    }

    public BUILDER_PAPEL addRoleDefinition(String description,
        UserRoleSettingStrategy<? extends ProcessInstance> userRoleSettingStrategy,
        boolean automaticUserAllocation) {
        return addRoleDefinition(description, SingularUtil.convertToJavaIdentity(description, true), userRoleSettingStrategy, automaticUserAllocation);
    }

    public BUILDER_PAPEL addRoleDefinition(String description, String abbreviation,
            UserRoleSettingStrategy<? extends ProcessInstance> userRoleSettingStrategy,
            boolean automaticUserAllocation) {
        return newProcessRole(getFlowMap().addRoleDefinition(description, abbreviation, userRoleSettingStrategy, automaticUserAllocation));
    }

    public BUILDER_PAPEL addRoleDefinition(String description, String abbreviation,
                                           boolean automaticUserAllocation) {
        return newProcessRole(getFlowMap().addRoleDefinition(description, abbreviation, new EmptyUserRoleSettingStrategy(), automaticUserAllocation));
    }

    public BUILDER_JAVA addJavaTask(TASK_DEF taskDefinition) {
        return newJavaTask(getFlowMap().addJavaTask(taskDefinition));
    }

    public BUILDER_PEOPLE addPeopleTask(TASK_DEF taskDefinition) {
        return newPeopleTask(getFlowMap().addPeopleTask(taskDefinition));
    }

    public BUILDER_PEOPLE addPeopleTask(TASK_DEF taskDefinition, TaskAccessStrategy<?> accessStrategy) {
        BUILDER_PEOPLE task = newPeopleTask(getFlowMap().addPeopleTask(taskDefinition));
        if (accessStrategy != null) {
            task.addAccessStrategy(accessStrategy);
        }
        task.withExecutionPage(new NullPageStrategy());
        return task;
    }

    public BUILDER_PEOPLE addPeopleTask(TASK_DEF taskDefinition, BProcessRole<?> requiredRole) {
        return addPeopleTask(taskDefinition, RoleAccessStrategy.of(requiredRole.getProcessRole()));
    }

    public BUILDER_PEOPLE addPeople(TASK_DEF taskDefinition, BProcessRole<?> requiredExecutionRole, BProcessRole<?> requiredVisualizeRole) {
        return addPeopleTask(taskDefinition, RoleAccessStrategy.of(requiredExecutionRole.getProcessRole(), requiredVisualizeRole.getProcessRole()));
    }

    public BUILDER_WAIT addWaitTask(TASK_DEF taskDefinition) {
        return newWaitTask(getFlowMap().addWaitTask(taskDefinition));
    }

    public <T extends ProcessInstance> BUILDER_WAIT addWaitTask(TASK_DEF taskDefinition, IExecutionDateStrategy<T> executionDateStrategy) {
        return newWaitTask(getFlowMap().addWaitTask(taskDefinition, executionDateStrategy));
    }

    public <T extends ProcessInstance> BUILDER_WAIT addWaitTask(TASK_DEF taskDefinition, IExecutionDateStrategy<T> executionDateStrategy,
            TaskAccessStrategy<?> accessStrategy) {
        BUILDER_WAIT wait = addWaitTask(taskDefinition, executionDateStrategy);
        wait.addAccessStrategy(accessStrategy);
        return wait;
    }

    public BUILDER_END addEnd(TASK_DEF taskDefinition) {
        return newEndTask(getFlowMap().addEnd(taskDefinition));
    }

    /**
     * Retorna um builder de task para uma tarefa já adicionada anteriormente ou
     * exception senão encontrar.
     */
    public BUILDER_TASK from(TASK_DEF taskRef) {
        return newTask(getTask(taskRef));
    }

    /**
     * Encontra a definição da task informada ou dispara exception senão
     * encontrada.
     *
     * @return Sempre diferente de null
     */
    protected MTask<?> getTask(TASK_DEF taskRef) {
        return getFlowMap().getTask(taskRef);
    }

    protected BUILDER_TRANSITION addTransition(BTask origin, String actionName, TASK_DEF destination) {
        return newTransition(origin.getTask().addTransition(actionName, getTask(destination)));
    }
    
    public BUILDER_TRANSITION addAutomaticTransition(TASK_DEF origin, ITaskPredicate condition, TASK_DEF destination) {
        return newTransition(getFlowMap().getTask(origin).addAutomaticTransition(condition, getFlowMap().getTask(destination)));
    }

    public void addTasksVisualizeStrategy(TaskAccessStrategy<?> accessVisualizeStrategy) {
        getFlowMap().getAllTasks().stream().forEach(t -> t.addVisualizeStrategy(accessVisualizeStrategy));
    }

    public void addTasksVisualizeStrategy(TaskAccessStrategy<?> accessVisualizeStrategy, Predicate<MTask<?>> applyToPredicate) {
        getFlowMap().getAllTasks().stream().filter(applyToPredicate).forEach(t -> t.addVisualizeStrategy(accessVisualizeStrategy));
    }

    public FlowBuilder<DEF, MAPA, BUILDER_TASK, BUILDER_JAVA, BUILDER_PEOPLE, BUILDER_WAIT, BUILDER_END, BUILDER_TRANSITION, BUILDER_PAPEL, TASK_DEF> addDashboardView(DashboardView dashboardView) {
        getFlowMap().addDashboardView(dashboardView);
        return this;
    }
}