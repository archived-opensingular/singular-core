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

package org.opensingular.lib.commons.test;

import org.assertj.core.api.AssertFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Assertions for a a list of values.
 *
 * @author Daniel C. Bordin
 * @since 2017-10-30
 */
public class BaseAssertionsForList<SELF extends BaseAssertionsForList<SELF, ELEMENT,ELEMENT_ASSERT>, ELEMENT , ELEMENT_ASSERT extends AssertionsBase<ELEMENT_ASSERT, ELEMENT>>
        extends AssertionsBase<SELF, List<ELEMENT>> {

    private final AssertFactory<ELEMENT, ELEMENT_ASSERT> assertFactory;

    public BaseAssertionsForList(List<ELEMENT> target,
            @Nonnull AssertFactory<ELEMENT, ELEMENT_ASSERT> assertFactory) {
        super(target);
        this.assertFactory = assertFactory;
    }

    /** Converts a element to it's correspondent Assertions help class. */
    @Nonnull
    protected final ELEMENT_ASSERT toElementAssert(@Nullable ELEMENT element) {
        return assertFactory.createAssert(element);
    }

    /** Verifica se a lista é do tamanho esperado. Senão dispara Exception. */
    public SELF hasSize(int expectedSize) {
        if (expectedSize != getTarget().size()) {
            throw new AssertionError(errorMsg("Tamanho da lista divergente", expectedSize, getTarget().size()));
        }
        return (SELF) this;
    }

    /** Verifica se a lista é no mínimo do tamanho esperado. Senão dispara exception. */
    private SELF hasSizeAtLeast(int expectedSize) {
        if (expectedSize > getTarget().size()) {
            throw new AssertionError(
                    errorMsg("Tamanho da menor que o mínimo esperado", expectedSize, getTarget().size()));
        }
        return (SELF) this;
    }

    /** Retorna uma assertiva para o componente na posição solicitada. Dispara exception se o indice for inválido. */
    public ELEMENT_ASSERT element(int index) {
        hasSizeAtLeast(index + 1);
        return toElementAssert(getTarget().get(index));
    }

    public ELEMENT_ASSERT first() {
        return element(0);
    }

    public ELEMENT_ASSERT last() {
        return element(getTarget().size() - 1);
    }
}
