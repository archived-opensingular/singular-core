/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.flow.core.renderer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.ITaskDefinition;
import org.opensingular.flow.core.STask;
import org.opensingular.flow.core.STransition;
import org.opensingular.flow.core.SingularFlowException;
import org.opensingular.flow.core.TaskInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the history of execution and current state of a particular flow instance so the diagram of the flow
 * may be decorated with this information.
 *
 * @author Daniel C. Bordin on 2017-09-21.
 */
public class ExecutionHistoryForRendering {

    private String current;
    private String lastAddedDestinationByTransition;
    private final Set<String> executedTasks = new HashSet<>();
    private final HashMap<Pair<String, String>, Set<String>> transitions = new HashMap<>();
    private final Set<String> taskWithInTransition = new HashSet<>();
    private final Set<String> taskWithOutTransition = new HashSet<>();
    private List<ExecutionEntry> executionHistory = new ArrayList<>();

    /** Extracts the execution history from a {@link FlowInstance}. */
    @Nonnull
    public static ExecutionHistoryForRendering from(@Nonnull FlowInstance flowInstance) {
        ExecutionHistoryForRendering historyForRendering = new ExecutionHistoryForRendering();
        TaskInstance current = null;
        for (TaskInstance task : flowInstance.getTasksOlderFirst()) {
            historyForRendering.addExecuted(task.getAbbreviation(), task.getBeginDate(), task.getEndDate());
            Optional<STransition> transition = task.getExecutedTransition();
            transition.ifPresent(t -> historyForRendering.addTransition(t));
            current = task;
        }
        if (current != null) {
            historyForRendering.setCurrent(current.getAbbreviation());
        }
        historyForRendering.guessMissingTransitions(flowInstance.getFlowDefinition().getFlowMap());
        return historyForRendering;
    }


    /** Removes all history information. */
    public void clear() {
        current = null;
        executedTasks.clear();
        transitions.clear();
        taskWithOutTransition.clear();
        taskWithInTransition.clear();
        executionHistory.clear();
    }

    /** Marks this task as executed. */
    public void addExecuted(@Nonnull String taskAbbreviation) {
        addExecuted(taskAbbreviation, null, null);
    }

    /** Marks this task as executed. */
    public void addExecuted(@Nonnull String taskAbbreviation, @Nullable Date start, @Nullable Date end) {
        addExecutedInternal(normalizeTask(taskAbbreviation), start, end);
    }

    /** Marks this task as executed. */
    public void addExecuted(@Nonnull ITaskDefinition taskDefinition) {
        addExecuted(taskDefinition, null, null);
    }

    /** Marks this task as executed. */
    public void addExecuted(@Nonnull ITaskDefinition taskDefinition, @Nullable Date start, @Nullable Date end) {
        addExecutedInternal(normalizeTask(taskDefinition), start, end);
    }

    /** Marks a list os tasks as executed. */
    public void addExecuted(@Nonnull ITaskDefinition... taskDefinition) {
        for (ITaskDefinition t : taskDefinition) {
            addExecuted(t);
        }
    }

    private void addExecutedInternal(@Nonnull String taskAbbreviation, @Nullable Date start, @Nullable Date end) {
        executedTasks.add(taskAbbreviation);
        if (start != null) {
            executionHistory.add(new ExecutionEntry(taskAbbreviation, start, end, executionHistory.size()));
        }
    }

    /**
     * Registers a sequence of transitions executed between the informed tasks and marks each task as executed (except
     * the last one). It assumes that there is a executed transition between two consecutive tasks.
     */
    public void addTransition(@Nonnull ITaskDefinition... path) {
        for (int i = 1; i < path.length; i++) {
            addTransition(path[i - 1], path[i]);
        }
    }

    /** Mark all transitions between the two tasks as executed. */
    public void addTransition(@Nonnull ITaskDefinition from, @Nonnull ITaskDefinition to) {
        addTransition(from, to, null);
    }

    /** Mark all transitions between the two tasks as executed. */
    public void addTransition(@Nonnull String fromTaskAbbreviation, @Nonnull String toTaskAbbreviation) {
        addTransition(fromTaskAbbreviation, toTaskAbbreviation, null);
    }

    /** Mark as executed the transaction between the two tasks and with the specified name. */
    public void addTransition(@Nonnull String fromTaskAbbreviation, @Nonnull String toTaskAbbreviation,
            @Nullable String transitionName) {
        addTransitionInternal(normalizeTask(fromTaskAbbreviation), normalizeTask(toTaskAbbreviation), transitionName);
    }

    /** Mark the transaction as executed. */
    public void addTransition(STransition transition) {
        addTransitionInternal(normalizeTask(transition.getOrigin()), normalizeTask(transition.getDestination()),
                transition.getAbbreviation());
    }

    private void addTransitionInternal(@Nonnull String fromTaskAbbreviation, @Nonnull String toTaskAbbreviation,
            @Nullable String transitionName) {
        addExecutedInternal(fromTaskAbbreviation, null, null);
        lastAddedDestinationByTransition = toTaskAbbreviation;
        Pair<String, String> key = Pair.of(fromTaskAbbreviation, toTaskAbbreviation);
        taskWithOutTransition.add(fromTaskAbbreviation);
        taskWithInTransition.add(toTaskAbbreviation);
        Set<String> names = transitions.computeIfAbsent(key, x -> new HashSet<String>());
        String s = normalizeTransition(transitionName);
        names.add(s == null ? "*" : s);
    }

    /** Mark as executed the transaction between the two tasks and with the specified name. */
    public void addTransition(@Nonnull ITaskDefinition from, @Nonnull ITaskDefinition to,
            @Nullable String transitionName) {
        addTransitionInternal(normalizeTask(from), normalizeTask(to), transitionName);
    }

    /** Mark the task that is the current state of the instance. */
    public void setCurrent(@Nonnull String currentTaskAbbreviation) {
        this.current = normalizeTask(currentTaskAbbreviation);
    }

    /** Mark the task that is the current state of the instance. */
    public void setCurrent(@Nonnull ITaskDefinition current) {
        setCurrent(normalizeTask(current));
    }

    @Nullable
    public String getCurrent() {return current;}

    /** Verifies if the given task is active at this moment. */
    public boolean isCurrent(@Nonnull STask<?> task) {
        return Objects.equals(current, normalizeTask(task));
    }

    /** Verifies if the task was executed. */
    public boolean isExecuted(@Nonnull STask<?> task) {
        return executedTasks.contains(normalizeTask(task));
    }

    /** Verifies if the transition was executed. */
    public boolean isExecuted(@Nonnull STransition transition) {
        Set<String> names = transitions.get(keyOf(transition.getOrigin(), transition.getDestination()));
        if (names != null) {
            String s = normalizeTransition(transition.getName());
            if (s != null && names.contains(s)) {
                return true;
            }
            return names.contains("*");
        }
        return false;
    }

    @Nullable
    private String normalizeTransition(@Nullable String name) {
        String s = StringUtils.trimToNull(name);
        return s == null ? null : s.toLowerCase();
    }

    /** Verifies if any transition was executed. */
    public boolean hasAnyTransitionExecuted() {
        return !transitions.isEmpty();
    }

    /** Verifies if there is any information about execution os tasks or what is the current task. */
    public boolean isEmpty() {
        return current == null && executedTasks.isEmpty();
    }

    @Nonnull
    public Set<String> getTransitions(@Nonnull ITaskDefinition origin, @Nonnull ITaskDefinition destination) {
        Set<String> names = transitions.get(keyOf(origin, destination));
        return names == null ? Collections.emptySet() : names;
    }

    @Nonnull
    private static Pair<String, String> keyOf(@Nonnull STask<?> origin, @Nonnull STask<?> destination) {
        return Pair.of(normalizeTask(origin), normalizeTask(destination));
    }

    @Nonnull
    private static Pair<String, String> keyOf(@Nonnull ITaskDefinition origin, @Nonnull ITaskDefinition destination) {
        return Pair.of(normalizeTask(origin), normalizeTask(destination));
    }

    @Nonnull
    private static String normalizeTask(STask<?> task) {
        return normalizeTask(task.getAbbreviation());
    }

    @Nonnull
    private static String normalizeTask(@Nonnull ITaskDefinition task) {
        return normalizeTask(task.getKey());
    }

    @Nonnull
    private static String normalizeTask(@Nonnull String name) {
        String s = StringUtils.trimToNull(name);
        if (s == null) {
            throw new SingularFlowException("Invalid name for a task: '" + name + "'");
        }
        return s.toLowerCase();
    }

    /** Counts how many transitions are registered. */
    public int countTransitions() {
        return (int) transitions.values().stream().mapToLong(s -> s.size()).sum();
    }

    /**
     * Tries to guess which transition was used to arrive to a task that is marked as executed and that also don't have
     * a incoming transition. The algorithm doesn't guarantees that will be able to find a answer. If the arriving
     * transition is ambiguous, the algorithm won't mark any transition.
     *
     * @return The number of added transitions. Zero is no change was made.
     */
    public int guessMissingTransitions(@Nonnull FlowMap flow) {
        Map<String, STask<?>> taskByKey = new HashMap<>();
        flow.getAllTasks().forEach(task -> taskByKey.put(normalizeTask(task), task));

        int fixes = tryFixByExecutionHistory(flow, taskByKey);
        fixes += tryFixByGuessingUniqueOfOriginOrDestination(flow, taskByKey);
        if (current == null && lastAddedDestinationByTransition != null) {
            current = lastAddedDestinationByTransition;
        }
        return fixes;
    }

    private int tryFixByExecutionHistory(@Nonnull FlowMap flow, @Nonnull Map<String, STask<?>> taskByKey) {
        int fixes = 0;
        if (executionHistory.isEmpty()) {
            return 0;
        }
        Collections.sort(executionHistory);
        ExecutionEntry last = executionHistory.get(0);
        for (int i = 1; i < executionHistory.size(); last = executionHistory.get(i), i++) {
            fixes += checksIfThereIsATransitionBetween(taskByKey, last, executionHistory.get(i));
        }
        if (current == null) {
            current = executionHistory.get(executionHistory.size() - 1).getTask();
            fixes++;
        }
        return fixes;
    }

    private int checksIfThereIsATransitionBetween(@Nonnull Map<String, STask<?>> taskByKey,
            @Nonnull ExecutionEntry last, @Nonnull ExecutionEntry destiny) {
        STask<?> taskOrigin = taskByKey.get(last.getTask());
        STask<?> taskDestiny = taskByKey.get(destiny.getTask());
        if (taskDestiny == null || taskOrigin == null) {
            return 0;
        }
        List<STransition> transitions = taskOrigin.getTransitions().stream().filter(
                t -> t.getDestination().equals(taskDestiny)).collect(Collectors.toList());
        if (transitions.size() == 1) {
            addTransition(transitions.get(0));
            return 1;
        }
        return 0;
    }

    private int tryFixByGuessingUniqueOfOriginOrDestination(@Nonnull FlowMap flow,
            @Nonnull Map<String, STask<?>> taskByKey) {
        int fixes = 0;
        if (current != null && isTaskMissingInTransition(flow, current)) {
            fixes += tryFixIn(taskByKey, current);
        }
        for (String task : executedTasks) {
            if (isTaskMissingInTransition(flow, task)) {
                fixes += tryFixIn(taskByKey, task);
            }
            if (isTaskMissingOutTransition(task)) {
                fixes += tryFixOut(taskByKey, task);
            }
        }
        return fixes;
    }

    private boolean isTaskMissingOutTransition(@Nonnull String task) {
        return !taskWithOutTransition.contains(task);
    }

    private boolean isTaskMissingInTransition(@Nonnull FlowMap flow, @Nonnull String task) {
        return !taskWithInTransition.contains(task) && !normalizeTask(flow.getStart().getTask()).equals(task);
    }

    private int tryFixOut(@Nonnull Map<String, STask<?>> taskByKey, @Nonnull String taskToFix) {
        STask<?> task = taskByKey.get(taskToFix);
        if (task != null) {
            STransition selected = lookForTaskDestinationExecuted(task);
            if (selected != null) {
                addTransition(selected);
                return 1;
            }
        }
        return 0;
    }

    @Nullable
    private STransition lookForTaskDestinationExecuted(STask<?> task) {
        STransition selected = null;
        for (STransition transition : task.getTransitions()) {
            if (isExecuted(transition.getDestination()) || isCurrent(transition.getDestination())) {
                if (selected == null) {
                    selected = transition;
                } else {
                    return null; //It's ambiguous
                }
            }
        }
        return selected;
    }

    private int tryFixIn(@Nonnull Map<String, STask<?>> taskByKey, @Nonnull String taskToFix) {
        STask<?> task = taskByKey.get(taskToFix);
        if (task != null) {
            STransition selected = lookForTaskOriginExecuted(task);
            if (selected != null) {
                addTransition(selected);
                return 1;
            }
        }
        return 0;
    }

    @Nullable
    private STransition lookForTaskOriginExecuted(@Nonnull STask<?> task) {
        STransition selected = null;
        for (STransition transition : task.getTransitionsArriving()) {
            if (isExecuted(transition.getOrigin())) {
                if (selected == null) {
                    selected = transition;
                } else {
                    return null; //It's ambiguous
                }
            }
        }
        return selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ExecutionHistoryForRendering)) return false;

        ExecutionHistoryForRendering that = (ExecutionHistoryForRendering) o;

        return new EqualsBuilder()
                .append(current, that.current)
                .append(executedTasks, that.executedTasks)
                .append(transitions, that.transitions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(current)
                .append(executedTasks)
                .append(transitions)
                .toHashCode();
    }

    private static class ExecutionEntry implements Comparable<ExecutionEntry> {

        private final String task;
        private final Date start;
        private final Date end;
        private final int sequential;

        private ExecutionEntry(@Nonnull String task, @Nonnull Date start, Date end, int sequential) {
            this.task = task;
            this.start = start;
            this.end = end;
            this.sequential = sequential;
        }

        @Nonnull
        public String getTask() {
            return task;
        }

        @Nonnull
        public Date getStart() {
            return start;
        }

        @Nullable
        public Date getEnd() {
            return end;
        }

        public int getSequential() {
            return sequential;
        }

        @Override
        public int compareTo(ExecutionEntry o) {
            int cmp = start.compareTo(o.start);
            if (cmp == 0) {
                cmp = (sequential < o.sequential ? -1 : (sequential == o.sequential ? 0 : 1));
            }
            return cmp;
        }
    }
}

