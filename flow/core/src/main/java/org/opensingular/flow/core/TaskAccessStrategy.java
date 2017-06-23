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

import org.opensingular.flow.core.entity.AccessStrategyType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"serial", "unchecked"})
public abstract class TaskAccessStrategy<K extends ProcessInstance> {

    public abstract boolean canExecute(K instance, SUser user);

    public <T extends TaskInstance> boolean canExecute(T instance, SUser user) {
        return canExecute((K) instance.getProcessInstance(), user);
    }

    public boolean canVisualize(K instancia, SUser user) {
        return canExecute(instancia, user);
    }

    public abstract Set<Integer> getFirstLevelUsersCodWithAccess(K instancia);

    public abstract List<? extends SUser> listAllocableUsers(K instancia);

    @Nonnull
    public abstract List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, STask<?> task);

    @Nonnull
    public List<String> getVisualizeRoleNames(ProcessDefinition<?> definicao, STask<?> task) {
        return getExecuteRoleNames(definicao, task);
    }

    public SUser getAutomaticAllocatedUser(K instancia, TaskInstance tarefa) {
        return null;
    }

    public boolean isNotifyAutomaticAllocation(K instancia, TaskInstance tarefa) {
        return true;
    }

    public boolean isOnlyForVisualize() {
        return false;
    }

    public TaskAccessStrategy<K> getOnlyVisualize() {
        if (isOnlyForVisualize()) {
            return this;
        }
        return new VisualizeOnlyTaskAccessStrategy<>(this);
    }

    public TaskAccessStrategy<K> or(TaskAccessStrategy<K> e2) {
        return or(this, e2);
    }

    public AccessStrategyType getType() {
        return AccessStrategyType.E;
    }

    public static <T extends ProcessInstance> TaskAccessStrategy<T> or(TaskAccessStrategy<T> e1, TaskAccessStrategy<T> e2) {
        if (e1 == null) {
            return e2;
        } else if (e2 == null) {
            return e1;
        }
        return new DisjunctionTaskAccessStrategy<>(e1, e2);
    }

    private static class DisjunctionTaskAccessStrategy<K extends ProcessInstance> extends TaskAccessStrategy<K> {

        private final List<TaskAccessStrategy<K>> disjunction = new ArrayList<>();

        public DisjunctionTaskAccessStrategy(TaskAccessStrategy<K> e1, TaskAccessStrategy<?> e2) {
            add(e1);
            add(e2);
            if(disjunction.isEmpty()){
                throw new SingularFlowException();
            }
        }

        private void add(TaskAccessStrategy<?> e1) {
            if (e1 != null) {
                if (e1 instanceof DisjunctionTaskAccessStrategy) {
                    disjunction.addAll(((DisjunctionTaskAccessStrategy<K>) e1).disjunction);
                } else {
                    disjunction.add((TaskAccessStrategy<K>) e1);
                }
            }
        }

        @Override
        public boolean canExecute(K instancia, SUser user) {
            return disjunction.stream().anyMatch(e -> e.canExecute(instancia, user));
        }

        @Override
        public boolean canVisualize(K instancia, SUser user) {
            return disjunction.stream().anyMatch(e -> e.canVisualize(instancia, user));
        }

        @Override
        public Set<Integer> getFirstLevelUsersCodWithAccess(K instancia) {
            Set<Integer> cods = new HashSet<>();
            for (TaskAccessStrategy<K> taskAccessStrategy : disjunction) {
                cods.addAll(taskAccessStrategy.getFirstLevelUsersCodWithAccess(instancia));
            }
            return cods;
        }

        @Override
        public List<SUser> listAllocableUsers(K instancia) {
            Set<SUser> users = new LinkedHashSet<>();
            for (TaskAccessStrategy<K> taskAccessStrategy : disjunction) {
                users.addAll(taskAccessStrategy.listAllocableUsers(instancia));
            }
            return new ArrayList<>(users);
        }

        @Override
        public SUser getAutomaticAllocatedUser(K instancia, TaskInstance tarefa) {
            for (TaskAccessStrategy<K> taskAccessStrategy : disjunction) {
                SUser alocadoAutomatico = taskAccessStrategy.getAutomaticAllocatedUser(instancia, tarefa);
                if (alocadoAutomatico != null) {
                    return alocadoAutomatico;
                }
            }
            return null;
        }

        @Override
        public List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, STask<?> task) {
            return disjunction.stream().flatMap(p -> p.getExecuteRoleNames(definicao, task).stream()).collect(Collectors.toList());
        }

        @Override
        public List<String> getVisualizeRoleNames(ProcessDefinition<?> definicao, STask<?> task) {
            return disjunction.stream().flatMap(p -> p.getVisualizeRoleNames(definicao, task).stream()).collect(Collectors.toList());
        }
    }

    private static class VisualizeOnlyTaskAccessStrategy<K extends ProcessInstance> extends TaskAccessStrategy<K> {

        private final TaskAccessStrategy<K> original;

        public VisualizeOnlyTaskAccessStrategy(TaskAccessStrategy<K> original) {
            this.original = original;
        }

        @Override
        public boolean canExecute(K instancia, SUser user) {
            return false;
        }

        @Override
        public boolean canVisualize(K instancia, SUser user) {
            return original.canVisualize(instancia, user);
        }

        @Override
        public Set<Integer> getFirstLevelUsersCodWithAccess(K instancia) {
            return Collections.emptySet();
        }

        @Override
        public List<SUser> listAllocableUsers(K instancia) {
            return Collections.emptyList();
        }

        @Override
        public List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, STask<?> task) {
            return Collections.emptyList();
        }

        @Override
        public List<String> getVisualizeRoleNames(ProcessDefinition<?> definicao, STask<?> task) {
            return original.getVisualizeRoleNames(definicao, task);
        }

        @Override
        public boolean isOnlyForVisualize() {
            return true;
        }
    }
}
