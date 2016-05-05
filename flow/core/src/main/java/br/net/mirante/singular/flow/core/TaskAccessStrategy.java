/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.net.mirante.singular.flow.core.entity.AccessStrategyType;

@SuppressWarnings({"serial", "unchecked"})
public abstract class TaskAccessStrategy<K extends ProcessInstance> {

    public abstract boolean canExecute(K instance, MUser user);

    public <T extends TaskInstance> boolean canExecute(T instance, MUser user) {
        return canExecute((K) instance.getProcessInstance(), user);
    }

    public boolean canVisualize(K instancia, MUser user) {
        return canExecute(instancia, user);
    }

    public abstract Set<Integer> getFirstLevelUsersCodWithAccess(K instancia);

    public abstract List<? extends MUser> listAllocableUsers(K instancia);

    public abstract List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, MTask<?> task);

    public List<String> getVisualizeRoleNames(ProcessDefinition<?> definicao, MTask<?> task) {
        return getExecuteRoleNames(definicao, task);
    }

    public MUser getAutomaticAllocatedUser(K instancia, TaskInstance tarefa) {
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

    public TaskAccessStrategy<K> or(TaskAccessStrategy<?> e2) {
        return or(this, e2);
    }

    public AccessStrategyType getType() {
        return AccessStrategyType.E;
    }

    public static <T extends ProcessInstance> TaskAccessStrategy<T> or(TaskAccessStrategy<T> e1, TaskAccessStrategy<?> e2) {
        if (e1 == null && e2 == null) {
            return null;
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
        public boolean canExecute(K instancia, MUser user) {
            return disjunction.stream().anyMatch(e -> e.canExecute(instancia, user));
        }

        @Override
        public boolean canVisualize(K instancia, MUser user) {
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
        public List<MUser> listAllocableUsers(K instancia) {
            Set<MUser> users = new LinkedHashSet<>();
            for (TaskAccessStrategy<K> taskAccessStrategy : disjunction) {
                users.addAll(taskAccessStrategy.listAllocableUsers(instancia));
            }
            return new ArrayList<>(users);
        }

        @Override
        public MUser getAutomaticAllocatedUser(K instancia, TaskInstance tarefa) {
            for (TaskAccessStrategy<K> taskAccessStrategy : disjunction) {
                MUser alocadoAutomatico = taskAccessStrategy.getAutomaticAllocatedUser(instancia, tarefa);
                if (alocadoAutomatico != null) {
                    return alocadoAutomatico;
                }
            }
            return null;
        }

        @Override
        public List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, MTask<?> task) {
            return disjunction.stream().flatMap(p -> p.getExecuteRoleNames(definicao, task).stream()).collect(Collectors.toList());
        }

        @Override
        public List<String> getVisualizeRoleNames(ProcessDefinition<?> definicao, MTask<?> task) {
            return disjunction.stream().flatMap(p -> p.getVisualizeRoleNames(definicao, task).stream()).collect(Collectors.toList());
        }
    }

    private static class VisualizeOnlyTaskAccessStrategy<K extends ProcessInstance> extends TaskAccessStrategy<K> {

        private final TaskAccessStrategy<K> original;

        public VisualizeOnlyTaskAccessStrategy(TaskAccessStrategy<K> original) {
            this.original = original;
        }

        @Override
        public boolean canExecute(K instancia, MUser user) {
            return false;
        }

        @Override
        public boolean canVisualize(K instancia, MUser user) {
            return original.canVisualize(instancia, user);
        }

        @Override
        public Set<Integer> getFirstLevelUsersCodWithAccess(K instancia) {
            return Collections.emptySet();
        }

        @Override
        public List<MUser> listAllocableUsers(K instancia) {
            return Collections.emptyList();
        }

        @Override
        public List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, MTask<?> task) {
            return Collections.emptyList();
        }

        @Override
        public List<String> getVisualizeRoleNames(ProcessDefinition<?> definicao, MTask<?> task) {
            return original.getVisualizeRoleNames(definicao, task);
        }

        @Override
        public boolean isOnlyForVisualize() {
            return true;
        }
    }
}
