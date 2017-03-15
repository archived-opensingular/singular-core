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

import org.opensingular.lib.commons.base.SingularException;

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
    public SingularFlowException(@Nullable String cause, @Nullable TaskInstance target) {
        super(cause);
        add(target);
    }

    /** Adiciona as informações sobre a task na exception. */
    private void add(@Nullable TaskInstance task) {
        if (task != null) {
            add("task.id", task.getId());
            add("task.fullId", () -> task.getFullId());
            add("task.process", () -> task.getProcessInstance().getFullId());
            add("task.name", task.getName());
            add("task.abbreviation", task.getAbbreviation());
        }
    }
}
