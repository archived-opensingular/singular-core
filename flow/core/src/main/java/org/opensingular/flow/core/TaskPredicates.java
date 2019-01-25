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

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskPredicates {

    private TaskPredicates() {
    }


    /**
     * Cria um predicado que retorna as tarefas com a mesma abreviação (sigla) da definição de task informada.
     */
    @Nonnull
    public static Predicate<TaskInstance> simpleTaskType(@Nonnull ITaskDefinition taskRef) {
        Objects.requireNonNull(taskRef);
        return simpleTaskType(taskRef.getKey());
    }

    /**
     * Cria um predicado que retorna as tarefas com a mesma abreviação (sigla).
     */
    @Nonnull
    static Predicate<TaskInstance> simpleTaskType(@Nonnull String abbreviation) {
        Objects.requireNonNull(abbreviation);
        return t -> t.isAtTask(abbreviation);
    }

    /**
     * Cria um predicado que retorna as tarefas com a mesma abreviação (sigla) da definição de task informada.
     */
    @Nonnull
    public static Predicate<TaskInstance> simpleTaskType(@Nonnull STask<?> type) {
        Objects.requireNonNull(type);
        return simpleTaskType(type.getAbbreviation());
    }

    @Nonnull
    public static Predicate<TaskInstance> simpleTaskType(@Nonnull ITaskDefinition... taskTypes) {
        return simpleTaskType(Stream.of(taskTypes).collect(Collectors.toList()));
    }

    @Nonnull
    public static Predicate<TaskInstance> simpleTaskType(List<ITaskDefinition> tasksTypes) {
        if (tasksTypes.size() == 1) {
            return simpleTaskType(tasksTypes.get(0));
        }
        Set<String> keys = tasksTypes.stream().map(ITaskDefinition::getKey).collect(ImmutableSet.toImmutableSet());
        return t -> keys.contains(t.getAbbreviation());
    }

    /**
     * Cria um predicado que retorna as tarefas encerradas
     */
    public static Predicate<TaskInstance> finished() {
        return t -> t.isFinished();
    }

    public static ITaskPredicate disabledCreator() {
        return new TaskPredicateImpl("Criador Demanda Inativado", (taskInstance) -> !Flow.canBeAllocated(taskInstance.getFlowInstance().getUserCreator()));
    }

    public static ITaskPredicate timeLimitInDays(final int numberOfDays) {
        TaskPredicateImpl taskPredicateImpl = new TaskPredicateImpl("Prazo Extrapolado " + numberOfDays + " dias", (taskInstance) -> {
            Date date = taskInstance.getTargetEndDate();
            if (date != null) {
                return Days.daysBetween(new DateTime(date), DateTime.now()).getDays() > numberOfDays;
            }
            return false;
        });
        taskPredicateImpl.setFullDescription("Prazo Extrapolado em " + numberOfDays + " dias");
        taskPredicateImpl.setEventType(EventType.TIMER);
        return taskPredicateImpl;
    }

    public static Predicate<TaskInstance> typeByTask(@Nonnull TaskType taskType) {
        return t -> {
            Optional<STask<?>> flowTask = t.getFlowTask();
            return flowTask.isPresent() && taskType.equals(flowTask.get().getTaskType());
        };
    }

    public static class TaskPredicateImpl implements ITaskPredicate {

        private final String name;
        private final Predicate<TaskInstance> predicate;

        private EventType eventType = EventType.CONDITIONAL;

        private String description;

        private String fullDescription;

        public TaskPredicateImpl(String name, Predicate<TaskInstance> predicate) {
            this.name = name;
            this.predicate = predicate;
        }

        @Override
        public boolean test(TaskInstance task) {
            return predicate.test(task);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public EventType getDisplayEventType() {
            return eventType;
        }

        @Override
        public String getDescription(TaskInstance taskInstance) {
            return StringUtils.defaultIfBlank(description, ITaskPredicate.super.getDescription(taskInstance));
        }

        @Override
        public String getFullDescription() {
            return StringUtils.defaultIfBlank(fullDescription, ITaskPredicate.super.getFullDescription());
        }

        public TaskPredicateImpl setDescription(String description) {
            this.description = description;
            return this;
        }

        public TaskPredicateImpl setEventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public TaskPredicateImpl setFullDescription(String fullDescription) {
            this.fullDescription = fullDescription;
            return this;
        }

        @Override
        public String toString() {
            return "TaskPredicateImpl [getName()=" + getName() + ", getDisplayEventType()=" + getDisplayEventType() + ", getFullDescription()=" + getFullDescription() + "]";
        }

    }
}
