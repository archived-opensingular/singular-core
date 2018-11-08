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

package org.opensingular.internal.lib.commons.test;

import com.google.common.base.Throwables;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.opensingular.internal.lib.commons.util.DebugOutputTable;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;
import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.internal.lib.commons.xml.ConversorToolkit;
import org.opensingular.lib.commons.lambda.IConsumerEx;
import org.opensingular.lib.commons.lambda.ITriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Oferece métodos utilitários para apoio a contrução de testes.
 *
 * @author Daniel C. Bordin on 16/03/2017.
 */
public final class SingularTestUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingularTestUtil.class);

    public static final int DEFAULT_WAIT_TIME_MILLI_AFTER_SHOW_ON_DESKTOP = 5000;

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
    public static void assertException(RunnableEx code, Class<? extends Throwable> expectedException) {
        assertException(code, expectedException, null, null);
    }

    /**
     * Executa o código informado e verifica se ocorre uma exception de acordo com o esperado.
     *
     * @param code                     Código a ser executado e que se espera que gere exception
     * @param expectedException        Classe da exceção esperada de ser disparada
     * @param expectedExceptionMsgPart (pode ser null) Trecho esperado de ser encontrado na mensagem da exception
     */
    public static void assertException(RunnableEx code, Class<? extends Throwable> expectedException,
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
    public static void assertException(@Nonnull RunnableEx code, @Nonnull Class<? extends Throwable> expectedException,
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
        } catch (Throwable e) { //NOSONAR
            if (!findExpectedException(e, expectedException, expectedExceptionMsgPart)) {
                if (e instanceof Error) { //NOSONAR
                    throw (Error) e;
                }
                String msg = "Era esperado '" + expectedException.getSimpleName() + "'";
                if (expectedExceptionMsgPart != null) {
                    msg += " com a mensagem contendo o texto [" + expectedExceptionMsgPart + "]";
                }
                msg += " no entanto não ocorreu a exceção conforme esperado (ver pilha abaixo)";
                throw new AssertionError(msg, e);
            }
        }
    }

    /**
     * Verifica se encontra a exception esperada na pilha de erro
     */
    private static boolean findExpectedException(Throwable e, Class<? extends Throwable> expectedException,
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
        System.out.println("  " + testName + ": T=" + SingularIOUtils.humanReadableMilliSeconds(time) + " R=" + count +
                "  qtd/seg=" + ConversorToolkit.printNumber(resultPerSecond, 0));
        return count;
    }

    public static void performance(@Nullable DebugOutputTable table, String testName, int durationInSeconds,
            @Nonnull List<Runnable> tasks) {
        if (table != null) {
            table.addValue(testName);
        }
        for (Runnable task : tasks) {
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
            if (table != null) {
                table.addValue(ConversorToolkit.printNumber(resultPerSecond, 0));
            }
        }
        if (table != null) {
            table.println();
        }
    }


    /**
     * Create a temp file, call the file generator provided to fill the temp file and then opens the file on the
     * developers desktop (calls {@link #showFileOnDesktopForUserAndWaitOpening(File)}). This method guaranties
     * that the
     * file will be deleted.
     * <p>This method may be used to inspect visually a generated file.</p>
     *
     * @param requester     Class or object client of the temp file generation. The name of the class will be
     *                      used as
     *                      prefix of the temp file names.
     * @param fileExtension It doesn't have a dot, it will be added (for example, "png" becomes ".png")
     * @param fileGenerator The code that will called to fill the temp file before the file be show
     * @see TempFileProvider#create(Object, IConsumerEx)
     */
    public static <E extends Exception> void showFileOnDesktopForUserAndWaitOpening(@Nonnull Object requester,
            @Nonnull String fileExtension, @Nonnull IConsumerEx<OutputStream, E> fileGenerator) {
        showFileOnDesktopForUserAndWaitOpening(requester, fileExtension, fileGenerator,
                DEFAULT_WAIT_TIME_MILLI_AFTER_SHOW_ON_DESKTOP);
    }

    /**
     * Create a temp file, call the file generator provided to fill the temp file and then opens the file on the
     * developers desktop (calls {@link #showFileOnDesktopForUserAndWaitOpening(File)}). This method guaranties
     * that the
     * file will be deleted.
     * <p>This method may be used to inspect visually a generated file.</p>
     *
     * @param requester              Class or object client of the temp file generation. The name of the class
     *                               will be
     *                               used as
     *                               prefix of the temp file names.
     * @param fileExtension          It doesn't have a dot, it will be added (for example, "png" becomes ".png")
     * @param fileGenerator          The code that will called to fill the temp file before the file be show
     * @param waitTimeMilliAfterCall Indica o tempo de espera em milisegundo. Se for negativo, não espera.
     * @see TempFileProvider#create(Object, IConsumerEx)
     */
    public static <E extends Exception> void showFileOnDesktopForUserAndWaitOpening(@Nonnull Object requester,
            @Nonnull String fileExtension, @Nonnull IConsumerEx<OutputStream, E> fileGenerator,
            int waitTimeMilliAfterCall) {
        TempFileProvider.create(requester, tmpProvider -> {
            String ext = fileExtension.indexOf('.') == -1 ? '.' + fileExtension : fileExtension;
            File arq = tmpProvider.createTempFile(ext);
            try (FileOutputStream out = new FileOutputStream(arq)) {
                fileGenerator.accept(out);
            } catch (Exception e) {
                Throwables.throwIfUnchecked(e);
                throw new SingularTestException(e);
            }
            showFileOnDesktopForUser(arq, waitTimeMilliAfterCall);
        });
    }

    /**
     * Abre o arqivo informado com o aplicativo associado no sistema operacional e espera 5 segundos para
     * prosseguir. Útil para inspecionar visualmente um arquivo que acabou de ser gerado por um teste.
     */
    public static void showFileOnDesktopForUserAndWaitOpening(File arq) {
        showFileOnDesktopForUser(arq, DEFAULT_WAIT_TIME_MILLI_AFTER_SHOW_ON_DESKTOP);
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
     * Opens the html content on the programmer desktop with the associated application of the operating system
     * (default browser) e waits 5 seconds until resume the flow of execution. Useful to visually inspect the content
     * just created by a test.
     */
    public static void showHtmlContentOnDesktopForUserAndWaitOpening(@Nonnull String content) {
        showFileOnDesktopForUserAndWaitOpening(SingularTestUtil.class, "html",
                out -> IOUtils.write(content, out, StandardCharsets.UTF_8));
    }

    static class SingularTestException extends RuntimeException {
        SingularTestException() {
        }

        SingularTestException(String message) {
            super(message);
        }

        SingularTestException(String message, Throwable cause) {
            super(message, cause);
        }

        SingularTestException(Throwable cause) {
            super(cause);
        }

        SingularTestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }


    /**
     * Unzips the resource (relative to the informed class) from the class path if it's name ends with '.zip' extension
     * then look for a entry with same file name of zip but with the indicated file extension. With the resource
     * isn't a
     * zip, returns it as the InputStream.
     */
    @Nonnull
    public static InputStream unzipFromResourceIfNecessary(@Nonnull Class<?> ref, @Nonnull String resourceName,
            @Nonnull String expectedExtension) {
        Objects.requireNonNull(ref);
        Objects.requireNonNull(resourceName);
        Objects.requireNonNull(expectedExtension);
        InputStream in;
        if (resourceName.endsWith(".zip")) {
            String name = resourceName.substring(0, resourceName.length() - 3) + expectedExtension;
            int pos = name.lastIndexOf('/');
            if (pos != -1) {
                name = name.substring(pos + 1);
            }
            in = SingularTestUtil.unzipFromResource(ref, resourceName, name);
        } else {
            in = ref.getResourceAsStream(resourceName);
        }
        if (in == null) {
            throw new SingularTestException(
                    "Resource '" + resourceName + "' not found in reference to " + ref.getName());
        }
        return in;
    }

    /**
     * Unzips the resource from the classpath relative to the informed class then return de unzip file entry with the
     * informed entryName.
     */
    @Nonnull
    public static InputStream unzipFromResource(@Nonnull Class<?> ref, @Nonnull String resourceName,
            @Nonnull String entryName) {
        Objects.requireNonNull(ref);
        Objects.requireNonNull(entryName);
        InputStream in = ref.getResourceAsStream(Objects.requireNonNull(resourceName));
        if (in == null) {
            throw new SingularTestException(
                    "Resource '" + resourceName + "' not found in reference to " + ref.getName());
        }
        ZipInputStream in2 = new ZipInputStream(in);
        try {
            ZipEntry entry;
            while ((entry = in2.getNextEntry()) != null) {
                if (entry.getName().equals(entryName)) {
                    break;
                }
            }
            if (entry == null) {
                in2.close();
                throw new SingularTestException("Wasn't found the entry " + entryName + " in " + resourceName);
            }
            return in2;
        } catch (Exception e) {
            IOUtils.closeQuietly(in2);
            Throwables.throwIfInstanceOf(e, SingularTestException.class);
            throw new SingularTestException(
                    "Fail to unzip file '" + entryName + "' from resource '" + resourceName + "' relative to " +
                            "class " + ref.getName(), e);
        }
    }

    /**
     * Compare the two map checking if they have the same entry (by key) and for each matching entry, verifies if they
     * are equals.
     *
     * @throws AssertionError If the maps have mismatched keys or two matching key doesn't equals values.
     */
    public static <K, V> void matchAndCompare(@Nonnull Map<K, V> expected, @Nonnull Map<K, V> current) {
        matchAndCompare(expected, current, (k, e, c) -> Assertions.assertThat(c).describedAs("for key={%s}", k)
                .isEqualTo(e));
    }

    /**
     * Compare the two map checking if they have the same entry (by key) and for each matching entry, calls the
     * supplied
     * consumer.
     *
     * @throws AssertionError If the maps have mismatched keys.
     */
    public static <K, V> void matchAndCompare(@Nonnull Map<K, V> expected, @Nonnull Map<K, V> current,
            @Nonnull ITriConsumer<K, V, V> consumer) {
        Set<K> currentKeys = new HashSet<>(current.keySet());
        Set<K> missingKey = new HashSet<>();
        for (Map.Entry<K, V> expectedEntry : expected.entrySet()) {
            V currentValue = current.get(expectedEntry.getKey());
            if (currentValue == null) {
                if (expectedEntry.getValue() != null) {
                    missingKey.add(expectedEntry.getKey());
                }
            } else {
                if (expectedEntry.getValue() == null) {
                    continue;
                } else {
                    consumer.accept(expectedEntry.getKey(), expectedEntry.getValue(), currentValue);
                }
            }
            currentKeys.remove(expectedEntry.getKey());
        }
        if (!currentKeys.isEmpty() || !missingKey.isEmpty()) {
            String msg = null;
            if (!currentKeys.isEmpty()) {
                msg = "the following keys in current map weren't expected: " + currentKeys;
            }
            if (!missingKey.isEmpty()) {
                msg = ((msg == null) ? "" : msg + "\n and ") + "the following expected key weren't found in current: " +
                        missingKey;
            }
            throw new AssertionError(msg);
        }
    }

}
