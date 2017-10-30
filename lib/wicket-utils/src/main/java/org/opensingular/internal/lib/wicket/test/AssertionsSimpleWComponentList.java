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

package org.opensingular.internal.lib.wicket.test;

import org.apache.wicket.Component;
import org.opensingular.lib.commons.test.AssertionsBase;

import java.util.List;
import java.util.Objects;

/**
 * Representa um conjunto de asserções voltadas para lista de Componentes Wicket.
 *
 * @author Daniel Bordin on 12/02/2017.
 */
public class AssertionsSimpleWComponentList<T extends Component>
        extends AssertionsBase<List<T>, AssertionsSimpleWComponentList<T>> {

    public AssertionsSimpleWComponentList(List<T> target) {
        super(Objects.requireNonNull(target));
    }

    @Override
    protected String errorMsg(String msg) {
        return msg;
    }

    /** Verifica se a lista é do tamanho esperado. Senão dispara Exception. */
    public AssertionsSimpleWComponentList<T> isSize(int expectedSize) {
        if (expectedSize != getTarget().size()) {
            throw new AssertionError(errorMsg("Tamanho da lista divergente", expectedSize, getTarget().size()));
        }
        return this;
    }

    /** Verifica se a lista é no mínimo do tamanho esperado. Senão dispara exception. */
    public AssertionsSimpleWComponentList<T> isSizeAtLeast(int expectedSize) {
        if (expectedSize > getTarget().size()) {
            throw new AssertionError(
                    errorMsg("Tamanho da menor que o mínimo esperado", expectedSize, getTarget().size()));
        }
        return this;
    }

    /** Retorna uma assertiva para o componente na posição solicitada. Dispara exception se o indice for inválido. */
    public AssertionsSimpleWComponent get(int index) {
        isSizeAtLeast(index + 1);
        return new AssertionsSimpleWComponent(getTarget().get(index));
    }

    public AssertionsSimpleWComponent first() {
        return get(0);
    }

    public AssertionsSimpleWComponent last() {
        return get(getTarget().size() - 1);
    }
}
