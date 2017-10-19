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

package org.opensingular.flow.core;

import com.google.common.collect.Lists;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityFlowDefinition;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.flow.core.service.IProcessDataService;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessDataServiceImpl<I extends FlowInstance> implements IProcessDataService<I> {

    private final FlowDefinition<I> flowDefinition;

    protected ProcessDataServiceImpl(FlowDefinition<I> flowDefinition) {
        super();
        this.flowDefinition = flowDefinition;
    }

    @Override
    public final I retrieveInstance(Integer entityCod) {
        return retrieveInstanceOpt(entityCod).orElseThrow(
                () -> new SingularFlowException("Nao foi encontrada a instancia de processo cod=" + entityCod));
    }

    @Override
    @Nonnull
    public final Optional<I> retrieveInstanceOpt(@Nonnull Integer entityCod) {
        return getPersistenceService().retrieveFlowInstanceByCod(entityCod)
                .map(i -> flowDefinition.convertToFlowInstance(i));
    }

    @Override
    public final List<I> retrieveActiveInstancesCreatedBy(SUser user) {
        Objects.requireNonNull(user);
        return convertToFlowInstance(getPersistenceService().retrieveFlowInstancesWith(getEntityFlowDefinition(), user, Boolean.TRUE));
    }

    @Override
    public final List<I> retrieveEndedInstances() {
        return convertToFlowInstance(getPersistenceService().retrieveFlowInstancesWith(getEntityFlowDefinition(), null, Boolean.FALSE));
    }

    @Override
    public final List<I> retrieveEndedInstancesCreatedBy(SUser user) {
        Objects.requireNonNull(user);
        return convertToFlowInstance(getPersistenceService().retrieveFlowInstancesWith(getEntityFlowDefinition(), user, Boolean.FALSE));
    }

    @Override
    public final List<I> retrieveAllInstancesIn(STask<?> task) {
        IEntityTaskDefinition targetTaskDefinition = getEntityTask(task);
        return retrieveAllInstancesIn(targetTaskDefinition != null ? Lists.newArrayList(targetTaskDefinition) : null);
    }

    @Override
    public final List<I> retrieveAllInstancesIn(Date startDate, Date endDate, boolean showEnded,
                                                ITaskDefinition... targetStates) {
        return retrieveAllInstancesIn(startDate, endDate, showEnded, convertToEntityTask(targetStates));
    }

    @Override
    public final List<I> retrieveAllInstancesIn(Date startDate, Date endDate, boolean showEnded,
            IEntityTaskDefinition... targetStates) {
        if (targetStates == null || targetStates.length == 0 || (targetStates.length == 1 && targetStates[0] == null)) {
            return retrieveAllInstancesIn(startDate, endDate, showEnded, (Collection<IEntityTaskDefinition>) null);
        }
        return retrieveAllInstancesIn(startDate, endDate, showEnded, Arrays.asList(targetStates));
    }

    private List<I> retrieveAllInstancesIn(Date startDate, Date endDate, boolean showEnded,
            Collection<IEntityTaskDefinition> targetSituations) {
        Collection<IEntityTaskDefinition> resolvedSituations = targetSituations;
        if (!showEnded && (resolvedSituations == null || resolvedSituations.isEmpty())) {
            resolvedSituations = getEntityFlowDefinition().getTaskDefinitions().stream().filter(
                    t -> !t.getLastVersion().isEnd())
                    .collect(Collectors.toList());
        }
        return convertToFlowInstance(getPersistenceService()
                .retrieveFlowInstancesWith(getEntityFlowDefinition(), startDate, endDate, resolvedSituations));
    }

    @Override
    public List<I> retrieveAllInstancesIn(ITaskDefinition... tasks) {
        return retrieveAllInstancesIn(convertToEntityTask(tasks));
    }

    @Override
    public final List<I> retrieveAllInstancesIn(Collection<? extends IEntityTaskDefinition> targetStates) {
        return convertToFlowInstance(
                getPersistenceService().retrieveFlowInstancesWith(getEntityFlowDefinition(), null, null, targetStates));
    }


    @Override
    public final List<I> retrieveActiveInstancesWithPeopleOrWaiting() {
        return retrieveAllInstancesIn(convertToEntityTask(getFlowMap().getTasks().stream().filter(t -> t.isPeople() || t.isWait())));
    }

    @Override
    public final List<I> retrieveActiveInstances() {
        return retrieveAllInstances(false);
    }

    @Override
    public final List<I> retrieveAllInstances(boolean showEnded) {
        FlowMap flowMap = getFlowMap();
        if (showEnded) {
            Set<IEntityTaskDefinition> targetStates = new HashSet<>();
            targetStates.addAll(convertToEntityTask(flowMap.getTasks()));
            targetStates.addAll(convertToEntityTask(flowMap.getEndTasks()));
            return retrieveAllInstancesIn(targetStates);
        } else {
            return retrieveAllInstancesIn(convertToEntityTask(flowMap.getTasks()));
        }
    }

    @Override
    public final List<I> retrieveActiveInstancesWithPeople() {
        return retrieveAllInstancesIn(convertToEntityTask(getFlowMap().getTasks().stream().filter(STask::isPeople)));
    }

    protected final IEntityFlowDefinition getEntityFlowDefinition() {
        return flowDefinition.getEntityFlowDefinition();

    }

    protected final IEntityTaskDefinition getEntityTask(STask<?> task) {
        return flowDefinition.getEntityTaskDefinition(task);
    }

    protected final FlowMap getFlowMap() {
        return flowDefinition.getFlowMap();
    }

    protected final List<IEntityTaskDefinition> convertToEntityTask(Collection<? extends STask<?>> collection) {
        return convertToEntityTask(collection.stream());
    }

    protected final List<IEntityTaskDefinition> convertToEntityTask(Stream<? extends STask<?>> stream) {
        return stream.map(t -> flowDefinition.getEntityTaskDefinition(t)).collect(Collectors.toList());
    }

    protected final List<IEntityTaskDefinition> convertToEntityTask(ITaskDefinition... tasks) {
        return flowDefinition.getEntityTaskDefinition(tasks);
    }

    protected final List<I> convertToFlowInstance(List<? extends IEntityFlowInstance> entities) {
        return (List<I>) flowDefinition.convertToFlowInstance(entities);
    }

    private IPersistenceService<IEntityCategory, IEntityFlowDefinition, IEntityFlowVersion, IEntityFlowInstance, IEntityTaskInstance,
            IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition,
            IEntityRoleInstance> getPersistenceService() {
        return flowDefinition.getPersistenceService();
    }
    
    protected FlowDefinition<I> getFlowDefinition() {
        return flowDefinition;
    }
}
