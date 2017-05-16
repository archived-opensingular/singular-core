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

package org.opensingular.internal.lib.commons.test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Oferece métodos utilitários para apoio a contrução de testes.
 *
 * @author Daniel C. Bordin on 16/03/2017.
 */
public final class SingularTestUtil {

    private SingularTestUtil() {}

    /**
     * Executa o código informado e verifica se ocorre uma exception de acordo com o esperado.
     *
     * @param code                     Código a ser executado e que se espera que gere exception
     * @param expectedExceptionMsgPart (pode ser null) Trecho esperado de ser encontrado na mensagem da exception
     */
    public static void assertException(RunnableEx code, String expectedExceptionMsgPart) {
        assertException(code, RuntimeException.class, expectedExceptionMsgPart, null);
    }

    /**
     * Executa o código informado e verifica se ocorre uma exception de acordo com o esperado.
     *
     * @param code                     Código a ser executado e que se espera que gere exception
     * @param expectedExceptionMsgPart (pode ser null) Trecho esperado de ser encontrado na mensagem da exception
     * @param failMsgIfNoException     (pode ser null) Mensage to be attacher to the fail mensage in case of no
     *                                 exception is producted from the executed code
     */
    public static void assertException(RunnableEx code, String expectedExceptionMsgPart, String failMsgIfNoException) {
        assertException(code, RuntimeException.class, expectedExceptionMsgPart, failMsgIfNoException);
    }

    /**
     * Executa o código informado e verifica se ocorre uma exception de acordo com o esperado.
     *
     * @param code                     Código a ser executado e que se espera que gere exception
     * @param expectedException        Classe da exceção esperada de ser disparada
     */
    public static void assertException(RunnableEx code, Class<? extends Exception> expectedException) {
        assertException(code, expectedException, null, null);
    }

    /**
     * Executa o código informado e verifica se ocorre uma exception de acordo com o esperado.
     *
     * @param code                     Código a ser executado e que se espera que gere exception
     * @param expectedException        Classe da exceção esperada de ser disparada
     * @param expectedExceptionMsgPart (pode ser null) Trecho esperado de ser encontrado na mensagem da exception
     */
    public static void assertException(RunnableEx code, Class<? extends Exception> expectedException,
            String expectedExceptionMsgPart) {
        assertException(code, expectedException, expectedExceptionMsgPart, null);
    }

    /**
     * Executa o código informado e verifica se ocorre uma exception de acordo com o esperado.
     *
     * @param code                     Código a ser executado e que se espera que gere exception
     * @param expectedException        Classe da exceção esperada de ser disparada
     * @param expectedExceptionMsgPart (pode ser null) Trecho esperado de ser encontrado na mensagem da exception
     * @param failMsgIfNoException     (pode ser null) Mensage to be attacher to the fail mensage in case of no
     *                                 exception is producted from the executed code
     */
    public static void assertException(@Nonnull RunnableEx code, @Nonnull Class<? extends Exception> expectedException,
            @Nullable String expectedExceptionMsgPart, @Nullable String failMsgIfNoException) {
        try {
            code.run();
            String msg = "Não ocorreu nenhuma Exception. Era esperado " + expectedException.getSimpleName() + "'";
            if (expectedExceptionMsgPart != null) {
                msg += " com mensagem contendo '" + expectedExceptionMsgPart + "'";
            }
            if (failMsgIfNoException != null) {
                msg += ", pois " + failMsgIfNoException;
            }
            throw new AssertionError(msg);
        } catch (Exception e) {
            if (findExpectedException(e, expectedException, expectedExceptionMsgPart)) {
                return;
            } else {
                String msg = "Era esperado " + expectedException.getSimpleName() + "'";
                msg += " no entanto ocorreu a exceção '" + e.getClass().getSimpleName() + "'";
                throw new AssertionError(msg, e);
            }
        }
    }

    /** Verifica se encontra a exception esperada na pilha de erro */
    private static boolean findExpectedException(Throwable e, Class<? extends Exception> expectedException,
            String expectedExceptionMsgPart) {
        if (expectedException.isInstance(e)) {
            if (expectedExceptionMsgPart == null || (e.getMessage() != null && e.getMessage().contains(
                    expectedExceptionMsgPart))) {
                return true;
            }
        }
        if (e.getCause() != null) {
            return findExpectedException(e.getCause(), expectedException, expectedExceptionMsgPart);
        }
        return false;
    }
}
