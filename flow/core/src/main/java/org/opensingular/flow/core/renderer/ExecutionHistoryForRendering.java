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
import org.apache.commons.lang3.tuple.Pair;
import org.opensingular.flow.core.ITaskDefinition;
import org.opensingular.flow.core.STask;
import org.opensingular.flow.core.STransition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the history of execution and current state of a particular process instance so the diagram of the process
 * may be decorated with this information.
 *
 * @author Daniel C. Bordin on 2017-09-21.
 */
public class ExecutionHistoryForRendering {

    private String current;
    private final Set<String> executedTasks = new HashSet<>();
    private final HashMap<Pair<String, String>, Set<String>> transitions = new HashMap<>();

    /** Marks this task as executed. */
    public void addExecuted(@Nonnull String taskAbbreviation) {
        executedTasks.add(taskAbbreviation);
    }

    /** Marks this task as executed. */
    public void addExecuted(@Nonnull ITaskDefinition taskDefinition) {
        addExecuted(normalize(taskDefinition));
    }

    /** Marks a list os tasks as executed. */
    public void addExecuted(@Nonnull ITaskDefinition... taskDefinition) {
        for (ITaskDefinition t : taskDefinition) {
            addExecuted(t);
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

    /** Mark as executed the transaction beteween the two tasks and with the specified name. */
    public void addTransition(@Nonnull String fromTaskAbbreviation, @Nonnull String toTaskAbbreviation,
            @Nullable String transitionName) {
        addExecuted(fromTaskAbbreviation);
        String s = normalizeName(transitionName);
        Pair<String, String> key = Pair.of(fromTaskAbbreviation, toTaskAbbreviation);
        Set<String> names = transitions.computeIfAbsent(key, x -> new HashSet<String>());
        names.add(s == null ? "*" : s);
    }
    /** Mark as executed the transaction beteween the two tasks and with the specified name. */
    public void addTransition(@Nonnull ITaskDefinition from, @Nonnull ITaskDefinition to,
            @Nullable String transitionName) {
        addTransition(normalize(from), normalize(to), transitionName);
    }

    private String normalizeName(String name) {
        String s = StringUtils.trimToNull(name);
        return s == null ? null : s.toLowerCase();
    }

    /** Mark the task that is the current state of the instance. */
    public void setCurrent(@Nonnull String currentTaskAbbreviation) {
        this.current = currentTaskAbbreviation;
    }

    /** Mark the task that is the current state of the instance. */
    public void setCurrent(@Nonnull ITaskDefinition current) {
        setCurrent(normalize(current));
    }

    /** Verifies if the given task is active at this moment. */
    public boolean isCurrent(@Nonnull STask<?> task) {
        return Objects.equals(current, normalize(task));
    }

    /** Verifies if the task was executed. */
    public boolean isExecuted(@Nonnull STask<?> task) {
        return executedTasks.contains(normalize(task));
    }

    /** Verifies if the transition was executed. */
    public boolean isExecuted(@Nonnull STransition transition) {
        Pair<String, String> key = Pair.of(normalize(transition.getOrigin()), normalize(transition.getDestination()));
        Set<String> names = transitions.get(key);
        if (names != null) {
            String s = normalizeName(transition.getName());
            if (s != null && names.contains(s)) {
                return true;
            }
            return names.contains("*");
        }
        return false;
    }

    /** Verifies if any transition was executed. */
    public boolean hasAnyTransitionExecuted() {
        return !transitions.isEmpty();
    }

    /** Verifies if there is any information about execution os tasks or what is the current task. */
    public boolean isEmpty() {
        return current == null && executedTasks.isEmpty();
    }

    private static String normalize(STask<?> task) {
        return task.getAbbreviation().trim().toLowerCase();
    }

    private static String normalize(ITaskDefinition task) {
        return task.getKey().trim().toLowerCase();
    }
}

