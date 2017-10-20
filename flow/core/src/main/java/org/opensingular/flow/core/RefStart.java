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

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Representa uma referência serializável a um ponto de inicialização de um processo.
 *
 * @author Daniel C. Bordin on 03/05/2017.
 */
final class RefStart implements Serializable, Supplier<SStart> {

    private final RefFlowDefinition refFlowDefinition;
    private transient SStart start;

    RefStart(@Nonnull SStart start) {
        this.start = Objects.requireNonNull(start);
        this.refFlowDefinition = Objects.requireNonNull(
                start.getFlowMap().getFlowDefinition().getSerializableReference());
    }

    @Override
    public SStart get() {
        if (start == null) {
            start = refFlowDefinition.get().getFlowMap().getStart();
            Objects.requireNonNull(start);
        }
        return start;
    }
}
