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

import org.apache.commons.io.IOUtils;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;
import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.internal.lib.commons.xml.ConversorToolkit;
import org.opensingular.lib.commons.pdf.PDFUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * Oferece métodos utilitários para apoio a contrução de testes.
 *
 * @author Daniel C. Bordin on 16/03/2017.
 */
public final class SingularTestUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingularTestUtil.class);

    private SingularTestUtil() {
    }

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
     * @param code              Código a ser executado e que se espera que gere exception
     * @param expectedException Classe da exceção esperada de ser disparada
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
                String msg = "Era esperado '" + expectedException.getSimpleName() + "'";
                msg += " no entanto ocorreu a exceção '" + e.getClass().getSimpleName() + "'";
                throw new AssertionError(msg, e);
            }
        }
    }

    /**
     * Verifica se encontra a exception esperada na pilha de erro
     */
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

    /**
     * Executa a task informada pelo tempo informado e verifica quantas repetições foram possíveis por segundo, jogando
     * o resultado para o console.
     */
    public static void performance(String testName, int durationInSeconds, Runnable task) {
        int count = 0;
        long time = System.currentTimeMillis();
        long timeEnd = time + durationInSeconds * 1000;
        while (System.currentTimeMillis() < timeEnd) {
            for (int i = 0; i < 100; i++) {
                task.run();
                count++;
            }
        }
        time = System.currentTimeMillis() - time;
        double resultPerSecond = 1000.0 * count / time;
        System.out.println("-------------------------------------------");
        System.out.println("  " + testName + ": T=" + SingularIOUtils.humanReadableMiliSeconds(time) + " R=" + count +
                "  qtd/seg=" + ConversorToolkit.printNumber(resultPerSecond, 0));
    }

    /**
     * Abre o arqivo informado com o aplicativo associado no sistema operacional e espera 5 segundos para
     * prosseguir. Útil para inspecionar visualmente um arquivo que acabou de ser gerado por um teste.
     */
    public static void showFileOnDesktopForUserAndWaitOpening(File arq) {
        showFileOnDesktopForUser(arq, 5000);
    }

    /**
     * Abre o arqivo informado com o aplicativo associado no sistema operacional e espera o tempo indicado para
     * prosseguir. Útil para inspecionar visualmente um arquivo que acabou de ser gerado por um teste.
     *
     * @param waitTimeMilliAfterCall Indica o tempo de espera em milisegundo. Se for negativo, não espera.
     */
    public static void showFileOnDesktopForUser(@Nonnull File arq, int waitTimeMilliAfterCall) {
        showFileOnDesktopForUser(arq);
        waitMilli(waitTimeMilliAfterCall);
    }

    /**
     * Abre o arqivo informado com o aplicativo associado no sistema operacional. Útil para inspecionar visualmente
     * um arquivo que acabou de ser gerado por um teste.
     */
    public static void showFileOnDesktopForUser(@Nonnull File arq) {
        if (!arq.exists() || arq.isDirectory()) {
            throw new SingularTestExpetion("Não existe o arquivo " + arq.getAbsolutePath());
        }
        try {
            if (PDFUtil.isWindows()) {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", arq.getAbsolutePath());//NOSONAR
                processBuilder.start();
            } else {
                throw new SingularTestExpetion("Sistema operacional não suportado");
            }
        } catch (IOException e) {
            throw new SingularTestExpetion(e);
        }
    }

    /**
     * Faz uma pausa segundo o tempo informado em millisegundos.
     */
    public static void waitMilli(int waitTimeMilli) {
        if (waitTimeMilli > 0) {
            try {
                Thread.sleep(waitTimeMilli);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Abre o conteúdo html informado com o aplicativo associado no sistema operacional (browser default) e espera 5
     * segundos para prosseguir. Útil para inspecionar visualmente um arquivo que acabou de ser gerado por um teste.
     */
    public static void showHtmlContentOnDesktopForUserAndWaitOpening(@Nonnull String content) {
        try (TempFileProvider tmpProvider = TempFileProvider.createForUseInTryClause(Objects.class)) {
            File arq = tmpProvider.createTempFile(".html");
            try (Writer out = new FileWriter(arq)) {
                IOUtils.write(content, out);
            } catch (IOException e) {
                throw new SingularTestExpetion(e);
            }
            SingularTestUtil.showFileOnDesktopForUserAndWaitOpening(arq);
        }
    }

    private static class SingularTestExpetion extends RuntimeException {
        public SingularTestExpetion() {
        }

        public SingularTestExpetion(String message) {
            super(message);
        }

        public SingularTestExpetion(String message, Throwable cause) {
            super(message, cause);
        }

        public SingularTestExpetion(Throwable cause) {
            super(cause);
        }

        public SingularTestExpetion(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
