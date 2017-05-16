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

package org.opensingular.flow.core;

import org.opensingular.flow.core.variable.ValidationResult;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Representa um ponto de inicialização de um fluxo do processo e as configurações do mesmo.
 *
 * @author Daniel C. Bordin on 19/03/2017.
 */
public class SStart extends SParametersEnabled {

    private final STask<?> task;

    private IStartInitializer startInitializer;

    private IStartValidator startValidator;

    SStart(STask<?> task) {
        this.task = task;
    }

    /** Task a ser executada a partir desse ponto de inicialização. */
    public STask<?> getTask() {
        return task;
    }

    /**
     * Retorna o código de inicialização a ser executado para cada nova instânca criada a partir deste ponto de start
     * antes do processo ser executado.
     */
    @Nullable
    public <I extends ProcessInstance> IStartInitializer<I> getStartInitializer() {
        return startInitializer;
    }

    /**
     * Define o código de inicialização a ser executado para cada nova instânca criada a partir deste ponto de start
     * antes do processo ser executado.
     */
    public <I extends ProcessInstance> void setStartInitializer(IStartInitializer<I> startInitializer) {
        this.startInitializer = startInitializer;
    }

    /**
     * Retorna o validador deste start point a ser executado antes que a instância seja criada. O validador é
     * executado antes do inicializador definido em {@link #setStartInitializer(IStartInitializer)} .
     */
    @Nullable
    public <I extends ProcessInstance> IStartValidator<I> getStartValidator() {
        return startValidator;
    }

    /**
     * Define o validador deste start point a ser executado antes que a instância seja criada. O validador é
     * executado antes do inicializador definido em {@link #setStartInitializer(IStartInitializer)} .
     */
    public <I extends ProcessInstance> void setStartValidator(IStartValidator<I> startValidator) {
        this.startValidator = startValidator;
    }

    @Override
    FlowMap getFlowMap() {
        return task.getFlowMap();
    }

    /**
     * Call back para inicializar os parâmetros de inicialização antes que {@link StartCall} seja disponibilziado para
     * configuração.
     */
    @FunctionalInterface
    public static interface IStartInitializer<I extends ProcessInstance> {
        public void startInstance(I instance, StartCall<I> startCall);
    }

    /** Validador da chamada de start antes que a instancia do processo seja criada. */
    @FunctionalInterface
    public interface IStartValidator<I extends ProcessInstance> extends Serializable {
        public void validate(StartCall<I> startCall, ValidationResult validationResult);
    }
}
