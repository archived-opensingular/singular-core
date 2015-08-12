package br.net.mirante.singular.flow.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;

@SuppressWarnings({"serial", "unchecked"})
public abstract class MTask<K extends MTask<?>> {

    private final FlowMap flowMap;
    private final String name;
    private String abbreviation;

    private final List<MTransition> transitions = new LinkedList<>();
    private final Map<String, MTransition> transitionsByName = new HashMap<>();
    private List<ConditionalTaskAction> automaticActions;

    private List<StartedTaskListener> startedTaskListeners;

    private MTransition defaultTransition;

    private TaskAccessStrategy<ProcessInstance> accessStrategy;

    private transient int order;

    //TODO
    @Deprecated
    private Boolean apareceNoPainelAtividades;

    public MTask(FlowMap flowMap, String name) {
        Preconditions.checkNotNull(flowMap);
        Preconditions.checkNotNull(name);
        this.flowMap = flowMap;
        this.name = name;
    }

    public K with(Consumer<K> consumer) {
        consumer.accept((K) this);
        return (K) this;
    }

    public abstract boolean canReallocate();

    public abstract TaskType getTaskType();

    public boolean isImmediateExecution() {
        return false;
    }

    public String getDescription() {
        return String.format("(%s) %s", getTaskType().getAbbreviation(), getName());
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        if (abbreviation == null) {
            abbreviation = MBPMUtil.convertToJavaIdentity(name);
        }
        return abbreviation;
    }

    public String getCompleteName() {
        return getFlowMap().getProcessDefinition().getAbbreviation() + '.' + name;
    }

    public final boolean isEnd() {
        return getTaskType() == TaskType.End;
    }

    public final boolean isJava() {
        return getTaskType() == TaskType.Java;
    }

    public final boolean isPeople() {
        return getTaskType() == TaskType.People;
    }

    public final boolean isWait() {
        return getTaskType() == TaskType.Wait;
    }

    public final boolean is(String taskName) {
        return getName().equalsIgnoreCase(taskName);
    }

    public TaskType getEffectiveTaskType() {
        TaskType tipo = getTaskType();
        if (tipo != TaskType.Wait && (this instanceof MTaskJava) && ((MTaskJava) this).getScheduleData() != null) {
            tipo = TaskType.Wait;
        }
        return tipo;
    }

    public boolean isExecutable() {
        return false;
    }

    public MTransition addTransition(String acao, MTask<?> nodeDestino, boolean showTransitionInExecution) {
        return addTransition(acao, nodeDestino).withAccessControl(TransitionAccessStrategy.enabled(showTransitionInExecution));
    }

    public MTransition addTransition(String acao, MTask<?> nodeDestino) {
        return addTransition(flowMap.newTransition(this, acao, nodeDestino, true));
    }

    public MTransition addTransition(MTask<?> destino) {
        defaultTransition = flowMap.newTransition(this, destino.getName(), destino, true);
        return addTransition(defaultTransition);
    }

    public MTransition addTransition(MTask<?> destino, boolean exibirTransicaoNaExecucao) {
        defaultTransition = flowMap.newTransition(this, destino.getName(), destino, true);
        return addTransition(defaultTransition).withAccessControl(TransitionAccessStrategy.enabled(exibirTransicaoNaExecucao));
    }

    public MTransition addAutomaticTransition(ITaskPredicate predicate, MTask<?> destino) {
        MTransition transicao = flowMap.newTransition(this, predicate.getName(), destino, false);
        transicao.setPredicate(predicate);
        addAutomaticAction(AcoesTarefa.transitar(predicate, transicao));
        return addTransition(transicao);
    }

    public MTransition getDefaultTransition() {
        return defaultTransition;
    }

    private MTransition addTransition(MTransition mTransicao) {
        if (transitionsByName.containsKey(mTransicao.getName().toLowerCase())) {
            throw generateError("Transition with name '" + mTransicao.getName() + "' already defined");
        }
        transitions.add(mTransicao);
        transitionsByName.put(mTransicao.getName().toLowerCase(), mTransicao);
        return mTransicao;
    }

    public void addAutomaticAction(ITaskPredicate condicao, TaskAction acao) {
        addAutomaticAction(new AcaoTarefaCondicionadaImpl(condicao, acao));
    }

    private void addAutomaticAction(ConditionalTaskAction action) {
        if (automaticActions == null) {
            automaticActions = new ArrayList<>();
        }
        automaticActions.add(action);
    }

    public List<ConditionalTaskAction> getAutomaticActions() {
        if (automaticActions == null) {
            return Collections.emptyList();
        }
        return automaticActions;
    }

    public void execute(ExecucaoMTask execucaoTask) {
        throw new RuntimeException("Operation not supported");
    }

    public List<MTransition> getTransicoes() {
        return transitions;
    }

    public MTransition getTransicaoOrException(String transitionName) {
        MTransition transicao = getTransition(transitionName);
        if (transicao == null) {
            throw generateError("Transition '" + transitionName + "' is not defined");
        }
        return transicao;
    }

    public MTransition getTransition(String transitionName) {
        return transitionsByName.get(transitionName.toLowerCase());
    }

    public void notifyTaskStart(TaskInstance taskInstance, ExecucaoMTask execucaoTask) {
        if (startedTaskListeners != null) {
            for (StartedTaskListener listener : startedTaskListeners) {
                listener.onTaskStart(taskInstance, execucaoTask);
            }
        }
    }

    public <T extends ProcessInstance> K addStartedTaskListener(StartedTaskListener startedTaskListener) {
        if (this.startedTaskListeners == null) {
            this.startedTaskListeners = new LinkedList<>();
        }
        this.startedTaskListeners.add(startedTaskListener);
        return (K) this;
    }

    public FlowMap getFlowMap() {
        return flowMap;
    }

    public int getOrder() {
        return order;
    }

    final void setOrder(int order) {
        this.order = order;
    }

    public K addAccessStrategy(TaskAccessStrategy<?> accessStrategy) {
        this.accessStrategy = TaskAccessStrategy.or(this.accessStrategy, accessStrategy);
        return (K) this;
    }

    public K addVisualizeStrategy(TaskAccessStrategy<?> accessStrategy) {
        return addAccessStrategy(accessStrategy.getOnlyVisualize());
    }

    public final <T extends ProcessInstance> TaskAccessStrategy<T> getAccessStrategy() {
        return (TaskAccessStrategy<T>) accessStrategy;
    }

    @Deprecated
    public K setApareceNoPainelAtividades(Boolean apareceNoPainelAtividades) {
        this.apareceNoPainelAtividades = apareceNoPainelAtividades;
        return (K) this;
    }

    @Deprecated
    public boolean isApareceNoPainelAtividades(boolean defaultAparece) {
        return apareceNoPainelAtividades == null ? defaultAparece : apareceNoPainelAtividades;
    }

    final RuntimeException generateError(String message) {
        return new RuntimeException(getFlowMap().getProcessDefinition() + ":" + this + " -> " + message);
    }

    void verifyConsistency() {

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + name + ')';
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MTask<?> other = (MTask<?>) obj;
        return name.equals(other.name);
    }
}
