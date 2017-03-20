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

import com.google.common.base.Joiner;
import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The base class of all runtime exceptions for Singular-Flow.
 *
 * @see SingularException
 */
public class SingularFlowException extends SingularException{

    /**
     * Constructs a new <code>SingularFlowException</code> without specified
     * detail message.
     */
    public SingularFlowException() {
        super();
    }

    /**
     * Constructs a new <code>SingularFlowException</code> with specified
     * detail message.
     *
     * @param msg the error message
     */
    public SingularFlowException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new <code>SingularFlowException</code> with specified
     * cause <code>Throwable</code>.
     *
     * @param cause the exception or error that caused this exception to be
     * thrown
     */
    public SingularFlowException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new <code>SingularFlowException</code> with specified
     * detail message and cause <code>Throwable</code>.
     *
     * @param msg    the error message
     * @param cause  the exception or error that caused this exception to be
     * thrown
     */
    public SingularFlowException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /** Cria uma nova Exception com complementos de dados da taskInstance informada. */
    public SingularFlowException(@Nullable String cause, @Nullable FlowMap target) {
        this(cause, target != null ? target.getProcessDefinition() : null);
    }

    /** Cria uma nova Exception com complementos de dados da taskInstance informada. */
    public SingularFlowException(@Nullable String cause, @Nullable ProcessDefinition<?> target) {
        super(cause);
        add(target);
    }

    /** Cria uma nova Exception com complementos de dados da instancia de processo informada. */
    public SingularFlowException(@Nullable String cause, @Nullable ProcessInstance target) {
        super(cause);
        add(target);
    }

    /** Cria uma nova Exception com complementos de dados da taskInstance informada. */
    public SingularFlowException(@Nullable String cause, @Nullable TaskInstance target) {
        super(cause);
        add(target);
    }

    /** Cria uma nova Exception com complementos de dados da definição de task informada. */
    public SingularFlowException(@Nullable String cause, @Nullable MTask<?> target) {
        super(cause);
        add(target);
    }

    public SingularFlowException addTransitions(@Nonnull MTask<?> task) {
        add("transições disponíveis na task", Joiner.on(", ").join(task.getTransitions()));
        return this;
    }


    /** Adiciona informações sobre a definição da task relacionada a exception. */
    private void add(@Nullable MTask<?> task) {
        if (task != null) {
            add(task.getFlowMap().getProcessDefinition());
            add("taskDefinition", task.getName() + " (class " + task.getClass().getSimpleName() + ")");
        }
    }

    /** Adiciona informações sobre a definição de processo relacionada a exception. */
    private void add(@Nullable ProcessDefinition<?> processDefinition) {
        if (processDefinition != null) {
            add("processDefinition", processDefinition.getName() + " (" + processDefinition.getClass() + ")");
        }
    }

    /** Adiciona as informações sobre a task na exception. */
    private void add(@Nullable TaskInstance task) {
        if (task != null) {
            add("task.id", task.getId());
            add("task.fullId", () -> task.getFullId());
            add("task.name", task.getName());
            add("task.abbreviation", task.getAbbreviation());
            add(task.getProcessInstance());
        }
    }

    /** Adiciona as informações sobre a task na exception. */
    private void add(@Nullable ProcessInstance processInstance) {
        if (processInstance != null) {
            add("processInstance", () -> processInstance.getFullId());
        }
    }
}
