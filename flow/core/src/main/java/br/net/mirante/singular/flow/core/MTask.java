package br.net.mirante.singular.flow.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import br.net.mirante.singular.flow.core.builder.ITaskDefinition;
import br.net.mirante.singular.flow.core.entity.TransitionType;
import br.net.mirante.singular.flow.util.props.MetaData;
import br.net.mirante.singular.flow.util.props.MetaDataRef;

import com.google.common.base.MoreObjects;

@SuppressWarnings({ "serial", "unchecked" })
public abstract class MTask<K extends MTask<?>> {

    private final FlowMap flowMap;
    private final String name;
    private final String abbreviation;

    private final List<MTransition> transitions = new LinkedList<>();
    private final Map<String, MTransition> transitionsByName = new HashMap<>();
    private List<IConditionalTaskAction> automaticActions;

    private List<StartedTaskListener> startedTaskListeners;

    private MTransition defaultTransition;

    private TaskAccessStrategy<ProcessInstance> accessStrategy;

    private transient int order;

    private MetaData metaData;

    public MTask(FlowMap flowMap, String name, String abbreviation) {
        Objects.requireNonNull(flowMap);
        Objects.requireNonNull(name);
        this.flowMap = flowMap;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public K with(Consumer<K> consumer) {
        consumer.accept((K) this);
        return (K) this;
    }

    public abstract boolean canReallocate();

    public abstract IEntityTaskType getTaskType();

    public boolean isImmediateExecution() {
        return false;
    }

    public String getDescription() {
        return String.format("(%s) %s", getTaskType().getAbbreviation(), getName());
    }

    public final String getName() {
        return name;
    }

    public final String getAbbreviation() {
        return abbreviation;
    }

    public String getCompleteName() {
        return getFlowMap().getProcessDefinition().getKey() + '.' + name;
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

    public final boolean is(ITaskDefinition taskDefinition) {
        return getAbbreviation().equalsIgnoreCase(taskDefinition.getKey());
    }

    public IEntityTaskType getEffectiveTaskType() {
        IEntityTaskType tipo = getTaskType();
        if (tipo != TaskType.Wait && (this instanceof MTaskJava) && ((MTaskJava) this).getScheduleData() != null) {
            tipo = TaskType.Wait;
        }
        return tipo;
    }

    public boolean isExecutable() {
        return false;
    }

    public <T> MTask<K> setMetaDataValue(MetaDataRef<T> propRef, T value) {
        getMetaData().set(propRef, value);
        return this;
    }

    public <T> T getMetaDataValue(MetaDataRef<T> propRef, T defaultValue) {
        return metaData == null ? defaultValue : MoreObjects.firstNonNull(getMetaData().get(propRef), defaultValue);
    }

    public <T> T getMetaDataValue(MetaDataRef<T> propRef) {
        return metaData == null ? null : getMetaData().get(propRef);
    }

    MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    public MTransition addTransition(String actionName, MTask<?> destination, boolean showTransitionInExecution) {
        return addTransition(actionName, destination).withAccessControl(TransitionAccessStrategyImpl.enabled(showTransitionInExecution));
    }

    public MTransition addTransition(String actionName, MTask<?> destination) {
        return addTransition(flowMap.newTransition(this, actionName, destination, TransitionType.H));
    }

    public MTransition addTransition(MTask<?> destination) {
        return addTransition(flowMap.newTransition(this, destination.getName(), destination, TransitionType.H));
    }

    public MTransition addAutomaticTransition(ITaskPredicate predicate, MTask<?> destination) {
        MTransition transition = flowMap.newTransition(this, predicate.getName(), destination, TransitionType.A);
        transition.setPredicate(predicate);
        addAutomaticAction(TaskActions.executeTransition(predicate, transition));
        return addTransition(transition);
    }

    public void setDefaultTransition(MTransition defaultTransition) {
        if(this.defaultTransition != null){
            throw new SingularFlowException(createErrorMsg("Default Transition already defined"));
        }
        this.defaultTransition = defaultTransition;
    }
    
    public MTransition getDefaultTransition() {
        return defaultTransition;
    }

    private MTransition addTransition(MTransition transition) {
        if (transitionsByName.containsKey(transition.getName().toLowerCase())) {
            throw new SingularFlowException(createErrorMsg("Transition with name '" + transition.getName() + "' already defined"));
        }
        transitions.add(transition);
        transitionsByName.put(transition.getName().toLowerCase(), transition);
        return transition;
    }

    public void addAutomaticAction(ITaskPredicate predicate, ITaskAction action) {
        addAutomaticAction(TaskActions.conditionalAction(predicate, action));
    }

    private void addAutomaticAction(IConditionalTaskAction action) {
        if (automaticActions == null) {
            automaticActions = new ArrayList<>(2);
        }
        automaticActions.add(action);
    }

    public List<IConditionalTaskAction> getAutomaticActions() {
        if (automaticActions == null) {
            return Collections.emptyList();
        }
        return automaticActions;
    }

    public void execute(ExecutionContext execucaoTask) {
        throw new SingularFlowException("Operation not supported");
    }

    public List<MTransition> getTransitions() {
        return transitions;
    }

    public MTransition getTransicaoOrException(String transitionName) {
        MTransition transicao = getTransition(transitionName);
        if (transicao == null) {
            throw new SingularFlowException(createErrorMsg("Transition '" + transitionName + "' is not defined"));
        }
        return transicao;
    }

    public MTransition getTransition(String transitionName) {
        return transitionsByName.get(transitionName.toLowerCase());
    }

    public void notifyTaskStart(TaskInstance taskInstance, ExecutionContext execucaoTask) {
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

    final String createErrorMsg(String message) {
        return getFlowMap().getProcessDefinition() + ":" + this + " -> " + message;
    }

    void verifyConsistency() {

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + name + ')';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((flowMap == null) ? 0 : flowMap.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MTask<?> other = (MTask<?>) obj;
        if (flowMap == null) {
            if (other.flowMap != null)
                return false;
        } else if (!flowMap.equals(other.flowMap))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    
}
