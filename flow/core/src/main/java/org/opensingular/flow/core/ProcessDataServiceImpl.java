/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.opensingular.flow.core.builder.ITaskDefinition;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.service.IProcessDataService;
import org.opensingular.flow.core.entity.IEntityProcessDefinition;
import org.opensingular.flow.core.entity.IEntityProcessVersion;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.service.IPersistenceService;

import com.google.common.collect.Lists;

public class ProcessDataServiceImpl<I extends ProcessInstance> implements IProcessDataService<I> {

    private final ProcessDefinition<I> processDefinition;

    protected ProcessDataServiceImpl(ProcessDefinition<I> processDefinition) {
        super();
        this.processDefinition = processDefinition;
    }

    @Override
    public final I retrieveInstance(Integer entityCod) {
        IEntityProcessInstance entityProcessInstance = getPersistenceService().retrieveProcessInstanceByCod(entityCod);
        if (entityProcessInstance != null) {
            return processDefinition.convertToProcessInstance(entityProcessInstance);
        }
        return null;
    }

    @Override
    public final List<I> retrieveActiveInstancesCreatedBy(MUser pessoa) {
        Objects.requireNonNull(pessoa);
        return convertToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntityProcessDefinition(), pessoa, true));
    }

    @Override
    public final List<I> retrieveEndedInstances() {
        return convertToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntityProcessDefinition(), null, false));
    }

    @Override
    public final List<I> retrieveEndedInstancesCreatedBy(MUser pessoa) {
        Objects.requireNonNull(pessoa);
        return convertToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntityProcessDefinition(), pessoa, false));
    }

    @Override
    public final List<I> retrieveAllInstancesIn(MTask<?> task) {
        IEntityTaskDefinition obterSituacaoPara = getEntityTask(task);
        return retrieveAllInstancesIn(obterSituacaoPara != null ? Lists.newArrayList(obterSituacaoPara) : null);
    }

    @Override
    public final List<I> retrieveAllInstancesIn(Date dataInicio, Date maxDataInicio, boolean exibirEncerradas,
            ITaskDefinition... situacoesAlvo) {
        return retrieveAllInstancesIn(dataInicio, maxDataInicio, exibirEncerradas, convertToEntityTask(situacoesAlvo));
    }

    @Override
    public final List<I> retrieveAllInstancesIn(Date dataInicio, Date dataFim, boolean exibirEncerradas,
            IEntityTaskDefinition... situacoesAlvo) {
        if (situacoesAlvo == null || situacoesAlvo.length == 0 || (situacoesAlvo.length == 1 && situacoesAlvo[0] == null)) {
            return retrieveAllInstancesIn(dataInicio, dataFim, exibirEncerradas, (Collection<IEntityTaskDefinition>) null);
        }
        return retrieveAllInstancesIn(dataInicio, dataFim, exibirEncerradas, Arrays.asList(situacoesAlvo));
    }

    private List<I> retrieveAllInstancesIn(Date dataInicio, Date dataFim, boolean exibirEncerradas,
            Collection<IEntityTaskDefinition> situacoesAlvo) {
        if (!exibirEncerradas && (situacoesAlvo == null || situacoesAlvo.isEmpty())) {
            situacoesAlvo = getEntityProcessDefinition().getTaskDefinitions().stream().filter(t -> !t.getLastVersion().isEnd())
                    .collect(Collectors.toList());
        }
        return convertToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntityProcessDefinition(), dataInicio,
            dataFim, situacoesAlvo));
    }

    @Override
    public List<I> retrieveAllInstancesIn(ITaskDefinition... tasks) {
        return retrieveAllInstancesIn(convertToEntityTask(tasks));
    }

    @Override
    public final List<I> retrieveAllInstancesIn(Collection<? extends IEntityTaskDefinition> situacoesAlvo) {
        return convertToProcessInstance(
                getPersistenceService().retrieveProcessInstancesWith(getEntityProcessDefinition(), null, null, situacoesAlvo));
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
    public final List<I> retrieveAllInstances(boolean exibirEncerradas) {
        if (exibirEncerradas) {
            Set<IEntityTaskDefinition> estadosAlvo = new HashSet<>();
            estadosAlvo.addAll(convertToEntityTask(getFlowMap().getTasks()));
            estadosAlvo.addAll(convertToEntityTask(getFlowMap().getEndTasks()));
            return retrieveAllInstancesIn(estadosAlvo);
        } else {
            return retrieveAllInstancesIn(convertToEntityTask(getFlowMap().getTasks()));
        }
    }

    @Override
    public final List<I> retrieveActiveInstancesWithPeople() {
        return retrieveAllInstancesIn(convertToEntityTask(getFlowMap().getTasks().stream().filter(t -> t.isPeople())));
    }

    protected final IEntityProcessDefinition getEntityProcessDefinition() {
        return processDefinition.getEntityProcessDefinition();

    }

    protected final IEntityTaskDefinition getEntityTask(MTask<?> task) {
        return processDefinition.getEntityTaskDefinition(task);
    }

    protected final FlowMap getFlowMap() {
        return processDefinition.getFlowMap();
    }

    protected final List<IEntityTaskDefinition> convertToEntityTask(Collection<? extends MTask<?>> collection) {
        return convertToEntityTask(collection.stream());
    }

    protected final List<IEntityTaskDefinition> convertToEntityTask(Stream<? extends MTask<?>> stream) {
        return stream.map(t -> processDefinition.getEntityTaskDefinition(t)).collect(Collectors.toList());
    }

    protected final List<IEntityTaskDefinition> convertToEntityTask(ITaskDefinition... tasks) {
        return processDefinition.getEntityTaskDefinition(tasks);
    }

    protected final List<I> convertToProcessInstance(List<? extends IEntityProcessInstance> entities) {
        return (List<I>) processDefinition.convertToProcessInstance(entities);
    }

    private IPersistenceService<IEntityCategory, IEntityProcessDefinition, IEntityProcessVersion, IEntityProcessInstance, IEntityTaskInstance,
            IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition,
            IEntityRoleInstance> getPersistenceService() {
        return processDefinition.getPersistenceService();
    }
    
    protected ProcessDefinition<I> getProcessDefinition() {
        return processDefinition;
    }
}
