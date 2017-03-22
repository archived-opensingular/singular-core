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

package org.opensingular.flow.core.builder;

import org.opensingular.flow.core.MParametersEnabled;
import org.opensingular.flow.core.MStart;
import org.opensingular.flow.core.ProcessInstance;

import java.util.function.Consumer;

/**
 * Builder para configuração dos pontos de start (inicialização) de um processo.
 *
 * @author Daniel C. Bordin on 19/03/2017.
 */
public interface BStart<SELF extends BStart<SELF>> extends BParametersEnabled<SELF> {

    public abstract MStart getStart();

    public default MParametersEnabled getParametersEnabled() {
        return getStart();
    }

    public default SELF self() {
        return (SELF) this;
    }

    default SELF with(Consumer<SELF> consumer) {
        SELF self = self();
        consumer.accept(self);
        return self;
    }

    /**
     * Define o código de inicialização a ser executado para cada nova instânca criada a partir deste ponto de start
     * antes do processo ser executado.
     *
     * @see MStart#setStartInitializer(MStart.IStartInitializer)
     */
    default <I extends ProcessInstance> SELF withInitializer(MStart.IStartInitializer<I> startInitializer) {
        getStart().setStartInitializer(startInitializer);
        return self();
    }

    /**
     * Define o validador deste start point a ser executado antes que a instância seja criada. O validador é
     * executado antes do inicializador definido em {@link #setStartInitializer(MStart.IStartInitializer)} .
     *
     * @see MStart#setStartValidator(MStart.IStartValidator)
     */
    default <I extends ProcessInstance> SELF withValidator(MStart.IStartValidator<I> startValidator) {
        getStart().setStartValidator(startValidator);
        return self();
    }
}
