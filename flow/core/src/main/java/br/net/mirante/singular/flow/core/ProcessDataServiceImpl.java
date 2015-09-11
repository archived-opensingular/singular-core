package br.net.mirante.singular.flow.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.core.service.IProcessDataService;

import com.google.common.collect.Sets;

public class ProcessDataServiceImpl<I extends ProcessInstance> implements IProcessDataService<I> {

    private final ProcessDefinition<I> processDefinition;

    protected ProcessDataServiceImpl(ProcessDefinition<I> processDefinition) {
        super();
        this.processDefinition = processDefinition;
    }

    public final I retrieveInstance(Long entityCod) {
        IEntityProcessInstance entityProcessInstance = getPersistenceService().retrieveProcessInstanceByCod(entityCod);
        if (entityProcessInstance != null) {
            return processDefinition.convertToProcessInstance(entityProcessInstance);
        }
        return null;
    }

    public final List<I> retrieveActiveInstancesCreatedBy(MUser pessoa) {
        Objects.requireNonNull(pessoa);
        return convertToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntityProcess(),
                pessoa, true));
    }

    public final List<I> retrieveEndedInstances() {
        return convertToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntityProcess(),
                null, false));
    }

    public final List<I> retrieveEndedInstancesCreatedBy(MUser pessoa) {
        Objects.requireNonNull(pessoa);
        return convertToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntityProcess(),
                pessoa, false));
    }

    public final List<I> retrieveAllInstancesIn(MTask<?> task) {
        final IEntityTask obterSituacaoPara = getEntityTask(task);
        return retrieveAllInstancesIn(obterSituacaoPara != null ? Sets.newHashSet(obterSituacaoPara) : null);
    }

    public final List<I> retrieveAllInstancesIn(Date minDataInicio, Date maxDataInicio, boolean exibirEncerradas,
            String... situacoesAlvo) {
        Set<IEntityTask> situacoes = convertToEntityTask(situacoesAlvo);
        return retrieveAllInstancesIn(minDataInicio, maxDataInicio, exibirEncerradas,
                situacoes.toArray(new IEntityTask[situacoes.size()]));
    }

    public final List<I> retrieveAllInstancesIn(Date minDataInicio, Date maxDataInicio, boolean exibirEncerradas,
            IEntityTask... situacoesAlvo) {
        final Set<IEntityTaskDefinition> estadosAlvo = new HashSet<>();
        for (final IEntityTask situacao : situacoesAlvo) {
            if (situacao != null) {
                estadosAlvo.add(getEntityTask(getFlowMap().getTaskWithAbbreviation(situacao.getAbbreviation()))
                        .getTaskDefinition());
            }
        }
        if (estadosAlvo.isEmpty()) {
            if (!exibirEncerradas) {
                estadosAlvo.addAll(getEntityProcess().getTasks()
                        .stream().filter(situacao -> !situacao.isEnd())
                        .map(IEntityTask::getTaskDefinition).collect(Collectors.toList()));
            }
        }
        return convertToProcessInstance(getPersistenceService()
                .retrieveProcessInstancesWith(getEntityProcess(), minDataInicio, maxDataInicio, estadosAlvo));
    }

    public final List<I> retrieveAllInstancesIn(String... situacoesAlvo) {
        final Set<IEntityTask> estadosAlvo = convertToEntityTask(situacoesAlvo);
        return retrieveAllInstancesIn(estadosAlvo);
    }

    public final List<I> retrieveAllInstancesIn(Collection<? extends IEntityTask> situacoesAlvo) {
        Set<IEntityTaskDefinition> estados = null;
        if (situacoesAlvo != null) {
            estados = situacoesAlvo.stream().map(IEntityTask::getTaskDefinition).collect(Collectors.toSet());
        }
        return convertToProcessInstance(getPersistenceService()
                .retrieveProcessInstancesWith(getEntityProcess(), null, null, estados));
    }

    public final List<I> retrieveActiveInstancesWithPeopleOrWaiting() {
        final Set<IEntityTask> estadosAlvo = convertToEntityTask(getFlowMap().getTasks()
                .stream().filter(t -> t.isPeople() || t.isWait()));
        return retrieveAllInstancesIn(estadosAlvo);
    }

    public final List<I> retrieveActiveInstances() {
        final Set<IEntityTask> estadosAlvo = convertToEntityTask(getFlowMap().getTasks());
        return retrieveAllInstancesIn(estadosAlvo);
    }

    public final List<I> retrieveAllInstances(boolean exibirEncerradas) {
        final Set<IEntityTask> estadosAlvo = new HashSet<>();
        estadosAlvo.addAll(convertToEntityTask(getFlowMap().getTasks()));
        if (exibirEncerradas) {
            estadosAlvo.addAll(convertToEntityTask(getFlowMap().getEndTasks()));
        }
        return retrieveAllInstancesIn(estadosAlvo);
    }

    public final List<I> retrieveActiveInstancesWithPeople() {
        return retrieveAllInstancesIn(processDefinition.getEntityPeopleTasks());
    }

    protected final IEntityProcess getEntityProcess() {
        return processDefinition.getEntity();
    }

    protected final IEntityTask getEntityTask(MTask<?> task) {
        return processDefinition.getEntityTask(task);
    }

    protected final FlowMap getFlowMap() {
        return processDefinition.getFlowMap();
    }

    protected final Set<IEntityTask> convertToEntityTask(Collection<? extends MTask<?>> collection) {
        return processDefinition.convertToEntityTask(collection);
    }

    protected final <X extends IEntityTask> Set<X> convertToEntityTask(Stream<? extends MTask<?>> stream) {
        return processDefinition.convertToEntityTask(stream);
    }

    protected final Set<IEntityTask> convertToEntityTask(String... tasksNames) {
        return Arrays.stream(tasksNames).map(processDefinition::getEntityTaskWithName).collect(Collectors.toSet());
    }

    protected final List<I> convertToProcessInstance(List<? extends IEntityProcessInstance> entities) {
        return processDefinition.convertToProcessInstance(entities);
    }

    private IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance,
            IEntityTaskDefinition, IEntityTask, IEntityVariableInstance, IEntityProcessRole,
            IEntityRole> getPersistenceService() {
        return processDefinition.getPersistenceService();
    }
}
