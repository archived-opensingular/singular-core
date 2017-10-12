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

    //----------------------------------------------------------------------------
    // Cloned methods from org.junit.Assert to avoid direct dependency with JUnit
    //----------------------------------------------------------------------------

    /**
     * Asserts that two objects are equal. If they are not, an
     * {@link AssertionError} without a message is thrown. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param expected expected value
     * @param actual   the value to check against <code>expected</code>
     */
    protected static void assertEquals(Object expected, Object actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two objects are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * <code>expected</code> and <code>actual</code> are <code>null</code>,
     * they are considered equal.
     *
     * @param message  the identifying message for the {@link AssertionError} (<code>null</code>
     *                 okay)
     * @param expected expected value
     * @param actual   actual value
     */
    protected static void assertEquals(String message, Object expected, Object actual) {
        if (equalsRegardingNull(expected, actual)) {
            return;
        } else if (expected instanceof String && actual instanceof String) {
            String cleanMessage = message == null ? "" : message;
            fail(cleanMessage + "\n expected=" + (String) expected + "\n found=" + (String) actual);
        } else {
            failNotEquals(message, expected, actual);
        }
    }

    private static boolean equalsRegardingNull(Object expected, Object actual) {
        if (expected == null) {
            return actual == null;
        }

        return isEquals(expected, actual);
    }

    static private void failNotEquals(String message, Object expected, Object actual) {
        fail(format(message, expected, actual));
    }

    static String format(String message, Object expected, Object actual) {
        String formatted = "";
        if (message != null && !message.equals("")) {
            formatted = message + " ";
        }
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        if (expectedString.equals(actualString)) {
            return formatted + "expected: " + formatClassAndValue(expected, expectedString) + " but was: " +
                    formatClassAndValue(actual, actualString);
        } else {
            return formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">";
        }
    }

    private static String formatClassAndValue(Object value, String valueString) {
        String className = value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
    }

    private static boolean isEquals(Object expected, Object actual) {
        return expected.equals(actual);
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object, an {@link AssertionError} is thrown with the
     * given message.
     *
     * @param message    the identifying message for the {@link AssertionError} (<code>null</code>
     *                   okay)
     * @param unexpected the object you don't expect
     * @param actual     the object to compare to <code>unexpected</code>
     */
    protected static void assertNotSame(String message, Object unexpected, Object actual) {
        if (unexpected == actual) {
            failSame(message);
        }
    }

    static private void failSame(String message) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected not same");
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object, an {@link AssertionError} without a message is
     * thrown.
     *
     * @param unexpected the object you don't expect
     * @param actual     the object to compare to <code>unexpected</code>
     */
    protected static void assertNotSame(Object unexpected, Object actual) {
        assertNotSame(null, unexpected, actual);
    }

    /**
     * Asserts that an object isn't null. If it is an {@link AssertionError} is
     * thrown.
     *
     * @param object Object to check or <code>null</code>
     */
    protected static void assertNotNull(Object object) {
        assertNotNull(null, object);
    }

    /**
     * Asserts that an object isn't null. If it is an {@link AssertionError} is
     * thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     *                okay)
     * @param object  Object to check or <code>null</code>
     */
    protected static void assertNotNull(String message, Object object) {
        assertTrue(message, object != null);
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an
     * {@link AssertionError} with the given message.
     *
     * @param message   the identifying message for the {@link AssertionError} (<code>null</code>
     *                  okay)
     * @param condition condition to be checked
     */
    protected static void assertTrue(String message, boolean condition) {
        if (!condition) {
            fail(message);
        }
    }

    /**
     * Asserts that an object is null. If it is not, an {@link AssertionError}
     * is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     *                okay)
     * @param object  Object to check or <code>null</code>
     */
    protected static void assertNull(String message, Object object) {
        if (object == null) {
            return;
        }
        failNotNull(message, object);
    }

    /**
     * Asserts that an object is null. If it isn't an {@link AssertionError} is
     * thrown.
     *
     * @param object Object to check or <code>null</code>
     */
    protected static void assertNull(Object object) {
        assertNull(null, object);
    }

    static private void failNotNull(String message, Object actual) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected null, but was:<" + actual + ">");
    }

    /**
     * Fails a test with no message.
     */
    protected static void fail() {
        fail(null);
    }

    /**
     * Fails a test with the given message.
     *
     * @param message the identifying message for the {@link AssertionError} (<code>null</code>
     *                okay)
     * @see AssertionError
     */
    protected static void fail(String message) {
        if (message == null) {
            throw new AssertionError();
        }
        throw new AssertionError(message);
    }
}
