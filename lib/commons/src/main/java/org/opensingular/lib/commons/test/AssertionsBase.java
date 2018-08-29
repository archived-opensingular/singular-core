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

package org.opensingular.lib.commons.test;


import org.assertj.core.api.AbstractAssert;
import org.assertj.core.description.Description;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Classe com implementações padrãos para um objeto de apoio a assertivas, independente do tipo em questão.
 *
 * @author Daniel C. Boridn
 */
public abstract class AssertionsBase<SELF extends AssertionsBase<SELF, T>, T> extends AbstractAssert<SELF, T> {

    @SuppressWarnings("CheckReturnValue")
    public AssertionsBase(T target) {
        super(target, AssertionsBase.class);
        as(new DescriptionForTarget(this));//NOSONAR
    }

    public AssertionsBase(Optional<? extends T> target) {
        this(target.orElse(null));
    }

    /**
     * Objeto alvo das assertivas.
     */
    @Nonnull
    public final T getTarget() {
        isNotNull();
        return super.actual;
    }

    @Nonnull
    public final Optional<T> getTargetOpt() {
        return Optional.ofNullable(super.actual);
    }

    /**
     * Retorna o objeto alvo das assertivas já com cast para o tipo da classe informado ou dá uma exception se o objeto
     * não foi da classe informado. Se for null, também gera exception.
     */
    @Nonnull
    public final <TT> TT getTarget(@Nonnull Class<TT> expectedClass) {
        isInstanceOf(expectedClass);
        return expectedClass.cast(getTarget());
    }

    /**
     * May be overridden to add text to the exception describing the current value bean asserted. It should help the
     * developer to understand the failed assertion.
     */
    @Nonnull
    protected Optional<String> generateDescriptionForCurrentTarget(@Nonnull Optional<T> current) {
        return Optional.empty();
    }

    protected final String errorMsg(String msg) {
        return errorMsg(msg, null, null);
    }

    protected final String errorMsg(String msg, Object expected, Object current) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        if(expected != null || current != null) {
            boolean showClass = (expected != null) && (current != null) && expected.getClass() != current.getClass();
            showClass |= "null".equals(expected) || "null".equals(current);
            sb.append(":\n Esperado  : ").append(expected);
            if (showClass && expected != null) {
                sb.append(" (").append(expected.getClass()).append(')');
            }
            sb.append("\n Encontrado: ").append(current);
            if (showClass && current != null) {
                sb.append(" (").append(current.getClass()).append(')');
            }
        }
        Optional<String> targetDescription = generateDescriptionForCurrentTarget(getTargetOpt());
        return targetDescription.map(s -> "[" + s + "]: " + sb).orElseGet(sb::toString);
    }

    private static class DescriptionForTarget<T> extends Description {

        private final AssertionsBase<?, T> assertionsBase;

        private DescriptionForTarget(@Nonnull AssertionsBase<?, T> assertionsBase) {
            this.assertionsBase = assertionsBase;
        }

        @Override
        public String value() {
            return assertionsBase.generateDescriptionForCurrentTarget(assertionsBase.getTargetOpt()).orElse("");
        }
    }
}
