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
import org.opensingular.flow.core.builder.ITaskDefinition;
import org.opensingular.flow.core.entity.*;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.flow.core.service.IProcessDataService;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessDataServiceImpl<I extends ProcessInstance> implements IProcessDataService<I> {

    private final ProcessDefinition<I> processDefinition;

    protected ProcessDataServiceImpl(ProcessDefinition<I> processDefinition) {
        super();
        this.processDefinition = processDefinition;
    }

    @Override
    public final I retrieveInstance(Integer entityCod) {
        return retrieveInstanceOpt(entityCod).orElseThrow(
                () -> new SingularFlowException("Nao foi encontrada a instancia de processo cod=" + entityCod));
    }

    @Override
    @Nonnull
    public final Optional<I> retrieveInstanceOpt(@Nonnull Integer entityCod) {
        return getPersistenceService().retrieveProcessInstanceByCod(entityCod)
                .map(i -> processDefinition.convertToProcessInstance(i));
    }

    @Override
    public final List<I> retrieveActiveInstancesCreatedBy(MUser pessoa) {
        Objects.requireNonNull(pessoa);
        return convertToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntityProcessDefinition(), pessoa, Boolean.TRUE));
    }

    @Override
    public final List<I> retrieveEndedInstances() {
        return convertToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntityProcessDefinition(), null, Boolean.FALSE));
    }

    @Override
    public final List<I> retrieveEndedInstancesCreatedBy(MUser pessoa) {
        Objects.requireNonNull(pessoa);
        return convertToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntityProcessDefinition(), pessoa, Boolean.FALSE));
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
        FlowMap flowMap = getFlowMap();
        if (exibirEncerradas) {
            Set<IEntityTaskDefinition> estadosAlvo = new HashSet<>();
            estadosAlvo.addAll(convertToEntityTask(flowMap.getTasks()));
            estadosAlvo.addAll(convertToEntityTask(flowMap.getEndTasks()));
            return retrieveAllInstancesIn(estadosAlvo);
        } else {
            return retrieveAllInstancesIn(convertToEntityTask(flowMap.getTasks()));
        }
    }

    @Override
    public final List<I> retrieveActiveInstancesWithPeople() {
        return retrieveAllInstancesIn(convertToEntityTask(getFlowMap().getTasks().stream().filter(MTask::isPeople)));
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
