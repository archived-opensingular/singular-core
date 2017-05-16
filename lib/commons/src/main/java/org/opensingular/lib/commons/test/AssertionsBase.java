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


import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Classe com implementações padrãos para um objeto de apoio a assertivas, independente do tipo em questão.
 *
 * @author Daniel C. Boridn
 */
public abstract class AssertionsBase<T, SELF extends AssertionsBase<T, SELF>> {


    private final T target;

    public AssertionsBase(T target) {
        this.target = target;
    }

    public AssertionsBase(Optional<? extends T> target) {
        this.target = target.orElse(null);
    }

    /**
     * Objeto alvo das assertivas.
     */
    @Nonnull
    public final T getTarget() {
        isNotNull();
        return target;
    }

    @Nonnull
    public final Optional<T> getTargetOpt() {
        return Optional.ofNullable(target);
    }

    /**
     * Retorna o objeto alvo das assertivas já com cast para o tipo da classe informado ou dá uma exception se o objeto
     * não foi da classe informado. Se for null, também gera exception.
     */
    @Nonnull
    public final <TT> TT getTarget(@Nonnull Class<TT> expectedClass) {
        if (!expectedClass.isInstance(target)) {
            throw new AssertionError(errorMsg("Não é da classe " + expectedClass.getName(), expectedClass,
                    target == null ? null : target.getClass()));
        }
        return expectedClass.cast(target);
    }


    /**
     * Deve ser implementado de modo a colocar na mensagem de erro, que será disparada na exception, informações
     * adicionais sobre o objeto alvo atual {@link #getTarget()} a fim de ajudar o entendimento do erro.
     */
    protected abstract String errorMsg(String msg);

    protected final String errorMsg(String msg, Object expected, Object current) {
        boolean showClass = (expected != null) && (current != null) && expected.getClass() != current.getClass();
        showClass |= "null".equals(expected) || "null".equals(current);
        StringBuilder sb = new StringBuilder();
        sb.append(msg).append(":\n Esperado  : ").append(expected);
        if (showClass && expected != null) {
            sb.append(" (").append(expected.getClass()).append(')');
        }
        sb.append("\n Encontrado: ").append(current);
        if (showClass && current != null) {
            sb.append(" (").append(current.getClass()).append(')');
        }
        return errorMsg(sb.toString());
    }

    /**
     * Verifica se o objeto atual é nulo.
     */
    public final SELF isNull() {
        if (target != null) {
            throw new AssertionError(errorMsg("Era esperado ser null."));
        }
        return (SELF) this;
    }


    /**
     * Verifica se o objeto atual não é nulo.
     */
    public final SELF isNotNull() {
        if (target == null) {
            throw new AssertionError("Resultado está null. Esperado não ser null.");
        }
        return (SELF) this;
    }

    /**
     * Verifica se o objeto atual é da classe informada.
     */
    public final SELF is(Class<?> typeClass) {
        if (! typeClass.isInstance(getTarget())) {
            throw new AssertionError(errorMsg("Não é uma instância da classe " + typeClass.getName(), typeClass,
                    getTarget().getClass()));
        }
        return (SELF) this;
    }

    /**
     * Verifica se o objeto atual é identico ao valor informado (equivalencia usando '==' ).
     */
    public final SELF isSameAs(Object expectedValue) {
        if (target != expectedValue) {
            throw new AssertionError(errorMsg("Não é a mesma instância (not the same) de " + expectedValue,
                    expectedValue, target));
        }
        return (SELF) this;
    }

    /**
     * Verifica se o objeto atual não é identico ao valor informado (equivalencia usando '==' ).
     */
    public final SELF isNotSameAs(Object notExpectedValue) {
        if (target == notExpectedValue) {
            throw new AssertionError(errorMsg(
                    "Era esperado instância diferentes (the same) de " + notExpectedValue + ", mas é igual",
                    "diferente de '" + notExpectedValue + "'", target));
        }
        return (SELF) this;
    }
}
