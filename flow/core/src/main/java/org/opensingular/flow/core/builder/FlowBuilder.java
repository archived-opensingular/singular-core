/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.core.builder;

import org.opensingular.flow.core.BusinessRoleStrategy;
import org.opensingular.flow.core.DashboardView;
import org.opensingular.flow.core.FlowDefinition;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.IExecutionDateStrategy;
import org.opensingular.flow.core.IRoleChangeListener;
import org.opensingular.flow.core.ITaskDefinition;
import org.opensingular.flow.core.ITaskPredicate;
import org.opensingular.flow.core.RoleAccessStrategy;
import org.opensingular.flow.core.SBusinessRole;
import org.opensingular.flow.core.SFlowUtil;
import org.opensingular.flow.core.SStart;
import org.opensingular.flow.core.STask;
import org.opensingular.flow.core.STaskEnd;
import org.opensingular.flow.core.STaskHuman;
import org.opensingular.flow.core.STaskJava;
import org.opensingular.flow.core.STaskWait;
import org.opensingular.flow.core.STransition;
import org.opensingular.flow.core.SingularFlowException;
import org.opensingular.flow.core.StartedTaskListener;
import org.opensingular.flow.core.TaskAccessStrategy;
import org.opensingular.lib.commons.base.SingularUtil;

import java.util.function.Consumer;

public abstract class FlowBuilder<DEF extends FlowDefinition<?>, FLOW_MAP extends FlowMap, BUILDER_TASK extends BuilderTask, BUILDER_JAVA extends BuilderJava<?>, BUILDER_PEOPLE extends BuilderHuman<?>, BUILDER_WAIT extends BuilderWait<?>, BUILDER_END extends BuilderEnd<?>, BUILDER_START extends BuilderStart<?>, BUILDER_TRANSITION extends BuilderTransition<?>, BUILDER_PAPEL extends BuilderBusinessRole<?>, TASK_DEF extends ITaskDefinition> {

    private final FLOW_MAP flowMap;

    public FlowBuilder(DEF flowDefinition) {
        flowMap = newFlowMap(flowDefinition);
    }

    protected abstract FLOW_MAP newFlowMap(DEF flowDefinition);

    protected abstract BUILDER_TASK newTask(STask<?> task);

    protected abstract BUILDER_JAVA newJavaTask(STaskJava task);

    protected abstract BUILDER_PEOPLE newHumanTask(STaskHuman task);

    protected abstract BUILDER_WAIT newWaitTask(STaskWait task);

    protected abstract BUILDER_END newEndTask(STaskEnd task);

    protected abstract BUILDER_START newStartTask(SStart start);

    protected abstract BUILDER_TRANSITION newTransition(STransition transition);

    protected abstract BUILDER_PAPEL newProcessRole(SBusinessRole transicao);

    protected final FLOW_MAP getFlowMap() {
        return flowMap;
    }

    public FLOW_MAP build() {
        return flowMap;
    }

    public BUILDER_START setStartTask(TASK_DEF taskDefinition) {
        FLOW_MAP flowMap = getFlowMap();
        return newStartTask(flowMap.setStart(taskDefinition));
    }

    public <T extends FlowInstance> void setRoleChangeListener(IRoleChangeListener<T> roleChangeListener) {
        getFlowMap().setRoleChangeListener(roleChangeListener);
    }

    private BuilderTask toBuilder(STask<?> task) {
        if (task instanceof STaskHuman) {
            return newHumanTask((STaskHuman) task);
        } else if (task instanceof STaskJava) {
            return newJavaTask((STaskJava) task);
        } else if (task instanceof STaskWait) {
            return newWaitTask((STaskWait) task);
        } else if (task instanceof STaskEnd) {
            return newEndTask((STaskEnd) task);
        }
        throw new SingularFlowException("Task type " + task.getClass().getName() + " not supported", build());
    }

    public void forEach(Consumer<BuilderTask> consumer) {
        getFlowMap().getTasks().stream().map(t -> toBuilder(t)).forEach(consumer);
    }

    public BUILDER_PAPEL addRoleDefinition(String description,
        BusinessRoleStrategy<? extends FlowInstance> businessRoleStrategy,
        boolean automaticUserAllocation) {
        return addRoleDefinition(description, SingularUtil.convertToJavaIdentity(description, true), businessRoleStrategy, automaticUserAllocation);
    }

    public BUILDER_PAPEL addRoleDefinition(String description, String abbreviation,
            BusinessRoleStrategy<? extends FlowInstance> businessRoleStrategy,
            boolean automaticUserAllocation) {
        return newProcessRole(getFlowMap().addRoleDefinition(description, abbreviation, businessRoleStrategy, automaticUserAllocation));
    }

    public BUILDER_PAPEL addRoleDefinition(String description, String abbreviation,
                                           boolean automaticUserAllocation) {
        return newProcessRole(getFlowMap().addRoleDefinition(description, abbreviation, SFlowUtil.dummyBusinessRoleStrategy(), automaticUserAllocation));
    }

    public BUILDER_PAPEL addRoleDefinition(String description, boolean automaticUserAllocation) {
        return newProcessRole(getFlowMap().addRoleDefinition(description,  SingularUtil.convertToJavaIdentity(description, true),  SFlowUtil.dummyBusinessRoleStrategy(), automaticUserAllocation));
    }

    public BUILDER_JAVA addJavaTask(TASK_DEF taskDefinition) {
        return newJavaTask(getFlowMap().addJavaTask(taskDefinition));
    }

    public BUILDER_PEOPLE addHumanTask(TASK_DEF taskDefinition) {
        return newHumanTask(getFlowMap().addHumanTask(taskDefinition));
    }

    public BUILDER_PEOPLE addHumanTask(TASK_DEF taskDefinition, TaskAccessStrategy<?> accessStrategy) {
        BUILDER_PEOPLE task = newHumanTask(getFlowMap().addHumanTask(taskDefinition));
        if (accessStrategy != null) {
            task.uiAccess(accessStrategy);
        }
        task.withExecutionPage(SFlowUtil.dummyITaskPageStrategy());
        return task;
    }

    public BUILDER_PEOPLE addHumanTask(TASK_DEF taskDefinition, BuilderBusinessRole<?> requiredRole) {
        return addHumanTask(taskDefinition, RoleAccessStrategy.of(requiredRole.getBusinessRole()));
    }

    public BUILDER_PEOPLE addPeople(TASK_DEF taskDefinition, BuilderBusinessRole<?> requiredExecutionRole, BuilderBusinessRole<?> requiredVisualizeRole) {
        return addHumanTask(taskDefinition, RoleAccessStrategy.of(requiredExecutionRole.getBusinessRole(), requiredVisualizeRole.getBusinessRole()));
    }

    public BUILDER_WAIT addWaitTask(TASK_DEF taskDefinition) {
        return newWaitTask(getFlowMap().addWaitTask(taskDefinition));
    }

    public <T extends FlowInstance> BUILDER_WAIT addWaitTask(TASK_DEF taskDefinition, IExecutionDateStrategy<T> executionDateStrategy) {
        return newWaitTask(getFlowMap().addWaitTask(taskDefinition, executionDateStrategy));
    }

    public <T extends FlowInstance> BUILDER_WAIT addWaitTask(TASK_DEF taskDefinition, IExecutionDateStrategy<T> executionDateStrategy,
            TaskAccessStrategy<?> accessStrategy) {
        BUILDER_WAIT wait = addWaitTask(taskDefinition, executionDateStrategy);
        wait.uiAccess(accessStrategy);
        return wait;
    }

    public BUILDER_END addEndTask(TASK_DEF taskDefinition) {
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
    protected STask<?> getTask(TASK_DEF taskRef) {
        return getFlowMap().getTask(taskRef);
    }

    public boolean hasTask(TASK_DEF taskRef) {
        return getFlowMap().getTaskWithName(taskRef.getName()) != null;
    }

    protected BUILDER_TRANSITION addTransition(BuilderTask origin, String actionName, TASK_DEF destination) {
        return newTransition(origin.getTask().addTransition(actionName, getTask(destination)));
    }
    
    public BUILDER_TRANSITION addAutomaticTransition(TASK_DEF origin, ITaskPredicate condition, TASK_DEF destination) {
        FLOW_MAP flowMap = getFlowMap();
        return newTransition(flowMap.getTask(origin).addAutomaticTransition(condition, flowMap.getTask(destination)));
    }

    public FlowBuilder<DEF, FLOW_MAP, BUILDER_TASK, BUILDER_JAVA, BUILDER_PEOPLE, BUILDER_WAIT, BUILDER_END, BUILDER_START, BUILDER_TRANSITION, BUILDER_PAPEL, TASK_DEF> addDashboardView(DashboardView dashboardView) {
        getFlowMap().addDashboardView(dashboardView);
        return this;
    }

    /**
     * Adiciona um StartedTaskListener para todas as tasks
     * incluídas no flow
     *
     * @param listener - listener a ser adicionado
     */
    public void addListenerToAllTasks(StartedTaskListener listener) {
        for (STask<?> sTask : getFlowMap().getAllTasks()) {
            sTask.addStartedTaskListener(listener);
        }
    }
}