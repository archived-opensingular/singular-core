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

import org.opensingular.flow.core.property.MetaDataEnabled;
import org.opensingular.flow.core.property.MetaDataMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings({ "serial", "unchecked" })
public abstract class STask<K extends STask<?>> implements MetaDataEnabled {

    private final FlowMap flowMap;
    private final String name;
    private final String abbreviation;

    private final List<STransition> transitions = new LinkedList<>();
    private final Map<String, STransition> transitionsByName = new HashMap<>();
    private List<IConditionalTaskAction> automaticActions;

    private List<StartedTaskListener> startedTaskListeners;

    private STransition defaultTransition;

    private TaskAccessStrategy<FlowInstance> accessStrategy;

    private transient int order;

    private MetaDataMap metaDataMap;

    public STask(FlowMap flowMap, String name, String abbreviation) {
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

    public abstract TaskType getTaskType();

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
        return getFlowMap().getFlowDefinition().getKey() + '.' + name;
    }

    public final boolean isEnd() {
        return getTaskType() == TaskType.END;
    }

    public final boolean isJava() {
        return getTaskType() == TaskType.JAVA;
    }

    public final boolean isPeople() {
        return getTaskType() == TaskType.HUMAN;
    }

    public final boolean isWait() {
        return getTaskType() == TaskType.WAIT;
    }

    public final boolean is(ITaskDefinition taskDefinition) {
        return getAbbreviation().equalsIgnoreCase(taskDefinition.getKey());
    }

    public IEntityTaskType getEffectiveTaskType() {
        IEntityTaskType type = getTaskType();
        if (type != TaskType.WAIT && (this instanceof STaskJava) && ((STaskJava) this).getScheduleData() != null) {
            type = TaskType.WAIT;
        }
        return type;
    }

    public boolean isExecutable() {
        return false;
    }

    @Override
    @Nonnull
    public Optional<MetaDataMap> getMetaDataOpt() {
        return Optional.ofNullable(metaDataMap);
    }

    @Override
    @Nonnull
    public MetaDataMap getMetaData() {
        if (metaDataMap == null) {
            metaDataMap = new MetaDataMap();
        }
        return metaDataMap;
    }

    public STransition addTransition(String actionName, STask<?> destination, boolean showTransitionInExecution) {
        return addTransition(actionName, destination).withAccessControl(UITransitionAccessStrategyImplUI.enabled(showTransitionInExecution, null));
    }

    public STransition addTransition(String actionName, STask<?> destination) {
        return addTransition(flowMap.newTransition(this, actionName, destination));
    }

    public STransition addTransition(STask<?> destination) {
        return addTransition(flowMap.newTransition(this, destination.getName(), destination));
    }

    public STransition addAutomaticTransition(@Nonnull ITaskPredicate predicate, @Nonnull STask<?> destination) {
        inject(predicate);
        STransition transition = flowMap.newTransition(this, predicate.getName(), destination);
        transition.setPredicate(predicate);
        addAutomaticAction(TaskActions.executeTransition(predicate, transition));
        return addTransition(transition);
    }

    public void setDefaultTransition(STransition defaultTransition) {
        if(this.defaultTransition != null){
            throw new SingularFlowException(createErrorMsg("Default transition already defined"), this).addTransitions(
                    this);
        }
        this.defaultTransition = defaultTransition;
    }
    
    public STransition getDefaultTransition() {
        return defaultTransition;
    }

    private STransition addTransition(STransition transition) {
        if (transitionsByName.containsKey(transition.getName().toLowerCase())) {
            throw new SingularFlowException(
                    createErrorMsg("Transition with name '" + transition.getName() + "' already defined"), this)
                    .addTransitions(this);
        }
        transitions.add(transition);
        transitionsByName.put(transition.getName().toLowerCase(), transition);
        return transition;
    }

    public void addAutomaticAction(@Nonnull ITaskPredicate predicate, @Nonnull ITaskAction action) {
        inject(predicate);
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

    public void execute(ExecutionContext executionContext) {
        throw new SingularFlowException("Operation not supported", this);
    }

    /** Lista de transições partindo da task atual. */
    @Nonnull
    public List<STransition> getTransitions() {
        return transitions;
    }

    /** Lists all the transition that arrives to this task. */
    @Nonnull
    public List<STransition> getTransitionsArriving() {
        return getFlowMap().getAllTasks().stream().flatMap(t -> t.transitions.stream()).filter(
                t -> t.getDestination().equals(this)).collect(Collectors.toList());
    }

    /** Recupera a transição com o nome informado ou dispara exception senão encontrar. */
    @Nonnull
    public STransition getTransitionOrException(@Nonnull String transitionName) {
        return getTransition(transitionName).orElseThrow(() -> new SingularFlowTransactionNotFoundException(
                createErrorMsg("Transição '" + transitionName + "' não encontrada em '" + getName() + "'"), this)
                .addTransitions(this));
    }

    /** Descobre qual a transição default ou dispara exception senão encontrar. */
    @Nonnull
    final STransition resolveDefaultTransitionOrException() {
        List<STransition> transitions = getTransitions();
        if (transitions.size() == 1) {
            return transitions.get(0);
        } else if (transitions.isEmpty()) {
            throw new SingularFlowException(createErrorMsg("não definiu nenhuma transicao"), this);
        } else if (defaultTransition != null) {
            return defaultTransition;
        }
        throw new SingularFlowTransactionNotFoundException(createErrorMsg(
                "possui várias transações e não definiu transicao default. Defina a transação default ou explicite " +
                        "qual transação deve ser executada."),
                this).addTransitions(this);
    }

    /** Recupera a transição com o nome informado. */
    @Nonnull
    public Optional<STransition> getTransition(@Nonnull String transitionName) {
        Objects.requireNonNull(transitionName);
        return Optional.ofNullable(transitionsByName.get(transitionName.toLowerCase()));
    }

    /**
     * Returns the transition that connects the current task to the informed task. Throws a exception if there more the
     * one transition to the target task.
     */
    @Nonnull
    public Optional<STransition> getTransitionTo(@Nonnull ITaskDefinition destination) {
        Objects.requireNonNull(destination);
        STransition selected = null;
        for (STransition transition : transitions) {
            if (transition.getDestination().is(destination)) {
                if (selected == null) {
                    selected = transition;
                } else {
                    throw new SingularFlowException(
                            "There is more than one transition to '" + destination.getKey() + "' from '" +
                                    getAbbreviation() + "'");
                }
            }
        }
        return Optional.ofNullable(selected);
    }

    /**
     * Returns the transition that connects the current task to the informed task or throws a exception if there isn't
     * any direct transition or if there more the one transition to the target task.
     */
    @Nonnull
    public STransition getTransitionToOrException(@Nonnull ITaskDefinition destination) {
        return getTransitionTo(destination).orElseThrow(() -> new SingularFlowException(
                "There is no transtion from '" + getAbbreviation() + "' to '" + destination.getKey() + "'"));
    }

    public void notifyTaskStart(TaskInstance taskInstance, ExecutionContext executionContext) {
        if (startedTaskListeners != null) {
            for (StartedTaskListener listener : startedTaskListeners) {
                inject(listener);
                listener.onTaskStart(taskInstance, executionContext);
            }
        }
    }

    @Nonnull
    public K addStartedTaskListener(@Nonnull StartedTaskListener startedTaskListener) {
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

    @Nonnull
    public K addAccessStrategy(@Nonnull TaskAccessStrategy<?> accessStrategy) {
        inject(accessStrategy);
        this.accessStrategy = TaskAccessStrategy.or(this.accessStrategy, (TaskAccessStrategy<FlowInstance>) accessStrategy);
        return (K) this;
    }

    public final <T extends FlowInstance> TaskAccessStrategy<T> getAccessStrategy() {
        return (TaskAccessStrategy<T>) accessStrategy;
    }

    final String createErrorMsg(String message) {
        return "Flow '" + getFlowMap().getFlowDefinition().getName() + "' : Task '" +name + "' -> " + message;
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        STask<?> other = (STask<?>) obj;
        return Objects.equals(flowMap, other.flowMap) && Objects.equals(name, other.name);
    }

    /** Faz a injeção de beans no objeto informado, se o mesmo necessitar. */
    @Nonnull
    final <V> V inject(@Nonnull V target) {
        return getFlowMap().getFlowDefinition().inject(target);
    }
}
