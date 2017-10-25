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

import com.google.common.base.Throwables;
import org.apache.commons.io.IOUtils;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;
import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.internal.lib.commons.xml.ConversorToolkit;
import org.opensingular.lib.commons.lambda.IConsumerEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
    public static long performance(String testName, int durationInSeconds, Runnable task) {
        long count = 0;
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
        return count;
    }

    /**
     * Create a temp file, call the file generator provided to fill the temp file and then opens the file on the
     * developers desktop (calls {@link #showFileOnDesktopForUserAndWaitOpening(File)}). This method guaranties that the
     * file will be deleted.
     * <p>This method may be used to inspect visually a generated file.</p>
     *
     * @param requester     Class or object client of the temp file generation. The name of the class will be used as
     *                      prefix of the temp file names.
     * @param fileExtension It doesn't have a dot, it will be added (for example, "png" becomes ".png")
     * @param fileGenerator The code that will called to fill the temp file before the file be show
     * @see {@link TempFileProvider#create(Object, IConsumerEx)}
     */
    public static <EX extends Exception> void showFileOnDesktopForUserAndWaitOpening(@Nonnull Object requester,
            @Nonnull String fileExtension, @Nonnull IConsumerEx<OutputStream, EX> fileGenerator) {
        TempFileProvider.create(requester, tmpProvider -> {
            String ext = fileExtension.indexOf('.') == -1 ? '.' + fileExtension : fileExtension;
            File arq = tmpProvider.createTempFile(ext);
            try (FileOutputStream out = new FileOutputStream(arq)) {
                fileGenerator.accept(out);
            } catch (Exception e) {
                Throwables.throwIfUnchecked(e);
                throw new SingularTestException(e);
            }
            showFileOnDesktopForUserAndWaitOpening(arq);
        });
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
            throw new SingularTestException("Não existe o arquivo " + arq.getAbsolutePath());
        }
        try {
            Desktop.getDesktop().browse(arq.toURI());
        } catch (IOException e) {
            throw new SingularTestException(e);
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
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Abre o conteúdo html informado com o aplicativo associado no sistema operacional (browser default) e espera 5
     * segundos para prosseguir. Útil para inspecionar visualmente um arquivo que acabou de ser gerado por um teste.
     */
    public static void showHtmlContentOnDesktopForUserAndWaitOpening(@Nonnull String content) {
        showFileOnDesktopForUserAndWaitOpening(SingularTestUtil.class, "html", out -> {
            IOUtils.write(content, out);
        });
    }

    private static class SingularTestException extends RuntimeException {
        public SingularTestException() {
        }

        public SingularTestException(String message) {
            super(message);
        }

        public SingularTestException(String message, Throwable cause) {
            super(message, cause);
        }

        public SingularTestException(Throwable cause) {
            super(cause);
        }

        public SingularTestException(String message, Throwable cause, boolean enableSuppression,
                boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
