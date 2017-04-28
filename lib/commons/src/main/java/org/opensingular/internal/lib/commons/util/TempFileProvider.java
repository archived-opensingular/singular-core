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

package org.opensingular.internal.lib.commons.util;

import org.apache.commons.io.IOUtils;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.lambda.IConsumerEx;
import org.opensingular.lib.commons.util.TempFileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Apoia na criação de arquivos ou diretóriso temporarios garantindo a exclusão dos mesmo ou na chmada do método
 * close() ou no encerramento da execução do código informado. O objetivo é não deixar lixo
 * para trás. Há duas formas de uso:
 * <p>
 * <p>Mais recomendada, por ser mais garantido que não ficarao arquivos para trás:</p>
 * <pre>
 *      TempFileProvider.create(this, tempFileProvider -> {
 *          File dir = tempFileProvider.createTempDir();
 *          File file1 = tempFileProvider.createTempFile();
 *          File file2 = tempFileProvider.createTempFile("content.txt");
 *          byte[] content = ...
 *          File file2 = tempFileProvider.createTempFile(content);
 *          // do something
 *      });
 *      //When the call is fineshed, all temp files and dirs will deleted
 * </pre>
 * <p>
 * <p>Forma alternativa:</p>
 * <pre>
 *      try(TempFileProvider tempFileProvider = TempFileProvider.createForUseInTryClause(this) {
 *          File dir = tempFileProvider.createTempDir();
 *          File file1 = tempFileProvider.createTempFile();
 *          File file2 = tempFileProvider.createTempFile("content.txt");
 *          byte[] content = ...
 *          File file2 = tempFileProvider.createTempFile(content);
 *          // do something
 *      }
 *      //When the call is fineshed, all temp files and dirs will deleted
 * </pre>
 *
 * @author Daniel C. Bordin 01/04/2017
 */
public class TempFileProvider implements Closeable {

    static final String DEFAULT_FILE_SUFFIX = ".tmp";
    static final String DEFAULT_FILE_PREFIX = "Singular_";

    private final Class<?> owner;

    private final List<TempEntry> tempFiles = new ArrayList<>();

    TempFileProvider(Class<?> owner) {
        this.owner = Objects.requireNonNull(owner);
    }

    /**
     * (Use preferencialmente {@link #create(Object, IConsumerEx)}) Cria um provedore de arquivos temporários a ser
     * usado em uma claúsula <code>try(){}</code>. Exemplo:
     * <pre>
     * try(TempFileProvider tempFileProvider = TempFileProvider.createForUseInTryClause(this) {
     *      File file1 = tempFileProvider.createTempFile();
     *      // do something
     * } // temp files will automatically be deleted here
     * </pre>
     * Dispara exception senão conseguir apagar todos os arquivos temporários criados (provavelmente por haver algum
     * stream ainda aberto em cima de um arquivo temporário).
     *
     * @param requester do arquivos temporário. O nome da classe será usado como prefixo no arquivo temporário.
     */
    public static TempFileProvider createForUseInTryClause(@Nonnull Object requester) {
        return new TempFileProvider(resolverRequester(requester));
    }

    /**
     * Cria um provedor de arquivos temporários e executado o código (<code>callback</code>) com esse provedor. Na
     * saída da chamada apaga todos os arquicos temporários. Exemplo:
     * <pre>
     * TempFileProvider.create(this, tempFileProvider -> {
     *      File file1 = tempFileProvider.createTempFile();
     *      // do something
     * } // temp files will automatically be deleted here
     * </pre>
     * Dispara exception senão conseguir apagar todos os arquivos temporários criados (provavelmente por haver algum
     * stream ainda aberto em cima de um arquivo temporário).
     *
     * @param requester do arquivos temporário. O nome da classe será usado como prefixo no arquivo temporário.
     * @param callback  código a ser executado com o provedor de arquivos temporários
     */
    public static void create(@Nonnull Object requester, @Nonnull IConsumerEx<TempFileProvider, IOException> callback) {
        TempFileProvider tmp = createForUseInTryClause(requester);
        boolean hadException = true;
        try {
            callback.accept(tmp);
            hadException = false;
        } catch (IOException e) {
            throw SingularException.rethrow(e);
        } finally {
            if (hadException) {
                tmp.deleteQuietly();
            } else {
                tmp.deleteOrException();
            }
        }
    }

    private static Class<?> resolverRequester(Object requester) {
        Objects.requireNonNull(requester);
        return requester instanceof Class ? (Class<?>) requester : requester.getClass();
    }

    /**
     * Cria uma diretório temporário (incluindo na lista para deleção automática). O diretório e seus arquivos de
     * conteúdo serão apagados juntos.
     */
    public File createTempDir() {
        File f = createTempDir(DEFAULT_FILE_PREFIX);
        tempFiles.add(new TempEntry(f, true));
        return f;
    }

    /** Cria uma diretório temporário com o prefixo informado. */
    private static File createTempDir(@Nonnull String prefix) {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = prefix + System.currentTimeMillis() + "-";

        for (int counter = 0; counter < 10000; ++counter) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }

        throw new IllegalStateException(
                "Failed to create directory within 10000 attempts (tried " + baseName + "0 to " + baseName + 9999 +
                        ')');
    }

    /** Cria um arquivo temporário (incluindo na lista para deleção automática). */
    @Nonnull
    public File createTempFile() {
        return createTempFile((String) null, (String) null);
    }

    /** Cria um arquivo temporário já com o conteúdo informado (incluindo na lista para deleção automática). */
    @Nonnull
    public File createTempFile(@Nonnull byte[] content) {
        return createTempFile(new ByteArrayInputStream(content), null);
    }

    /**
     * Cria um arquivo temporário já com o conteúdo informado (incluindo na lista para deleção automática).
     *
     * @param suffix Texto para ser colocado como sufixo do nome do arquivo criado (em geral é a extensão). Exemplo:
     *               ".txt" ou "result.txt". Senão informado, o defult é ".tmp"
     */
    @Nonnull
    public File createTempFile(@Nonnull byte[] content, @Nullable String suffix) {
        return createTempFile(new ByteArrayInputStream(content), suffix);
    }

    /** Cria um arquivo temporário já com o conteúdo informado (incluindo na lista para deleção automática). */
    @Nonnull
    public File createTempFile(@Nonnull InputStream content) {
        return createTempFile(content, null);
    }

    /**
     * Cria um arquivo temporário já com o conteúdo informado (incluindo na lista para deleção automática).
     *
     * @param suffix Texto para ser colocado como sufixo do nome do arquivo criado (em geral é a extensão). Exemplo:
     *               ".txt" ou "result.txt". Senão informado, o defult é ".tmp"
     */
    @Nonnull
    public File createTempFile(@Nonnull InputStream content, @Nullable String suffix) {
        File f = createTempFile(suffix);
        try (InputStream in = content; FileOutputStream fout = new FileOutputStream(f)) {
            IOUtils.copy(in, fout);
        } catch (IOException e) {
            throw SingularException.rethrow("Erro escrevendo conteúdo no arquivo temporário", e);
        }
        return f;
    }

    /**
     * Cria um arquivo temporário com o sufixo informado (incluindo na lista para deleção automática).
     *
     * @param suffix Texto para ser colocado como sufixo do nome do arquivo criado (em geral é a extensão). Exemplo:
     *               ".txt" ou "result.txt". Senão informado, o defult é ".tmp"
     */
    @Nonnull
    public File createTempFile(@Nullable String suffix) {
        return createTempFile((String) null, suffix);
    }

    /**
     * Cria um arquivo temporário com o prefixo e sufixo informados (incluindo na lista para deleção automática).
     *
     * @param prefix Texto para ser colocado como prefixo do nome do arquivo criado.
     * @param suffix Texto para ser colocado como sufixo do nome do arquivo criado (em geral é a extensão). Exemplo:
     *               ".txt" ou "result.txt". Senão informado, o defult é ".tmp"
     */
    @Nonnull
    public File createTempFile(@Nullable String prefix, @Nullable String suffix) {
        File f = createTempFileInternal(prefix, suffix);
        tempFiles.add(new TempEntry(f, false));
        return f;
    }

    @Nonnull
    private final File createTempFileInternal(@Nullable String prefix, @Nullable String suffix) {
        String p = prefix == null ? owner.getSimpleName() + '_' : prefix;
        String s = suffix == null ? DEFAULT_FILE_SUFFIX : suffix;
        try {
            return File.createTempFile(DEFAULT_FILE_PREFIX + p, s);
        } catch (IOException e) {
            throw SingularException.rethrow("Erro criando arquivo temporário", e);
        }
    }

    /**
     * Cria um arquivo temporário com o sufixo informado mas <b>não coloca na lista para deleção automática</b>. Apenas
     * coloca como {@link File#deleteOnExit()}.
     *
     * @param suffix Texto para ser colocado como sufixo do nome do arquivo criado (em geral é a extensão). Exemplo:
     *               ".txt" ou "result.txt". Senão informado, o defult é ".tmp"
     */
    public final File createTempFileByDontPutOnDeleteList(String suffix) {
        File f = createTempFileInternal(null, suffix);
        f.deleteOnExit();
        return f;
    }

    /** Chama {@link #deleteOrException()}. */
    @Override
    public void close() {
        deleteOrException();
    }

    /**
     * Tenta apagar os arquivos temporários associados. Não conseguindo apagar ou ocorrendo uma exception ao
     * chamar {@link File#delete()}, faz log do erro e dispara exception.
     *
     * @see TempFileUtils#deleteOrException(File, Class)
     */
    public void deleteOrException() {
        delete(false);
    }

    /**
     * Tenta apagar os arquivos temporários associados. . Não conseguindo apagar ou ocorrendo uma exception ao
     * chamar {@link File#delete()}, faz log do erro mas não dispara exception.
     *
     * @see TempFileUtils#deleteAndFailQuietily(File, Class)
     */
    public void deleteQuietly() {
        delete(true);
    }

    private void delete(boolean quietly) {
        Exception error = null;
        for (TempEntry entry : tempFiles) {
            try {
                if (entry.diretory) {
                    recursiveDelete(entry.file, quietly);
                } else {
                    deleteSingleFile(entry.file, quietly);
                }
            } catch (Exception e) {
                error = e;
            }
        }
        tempFiles.clear();
        if (error != null) {
            throw SingularException.rethrow(error);
        }
    }

    private void recursiveDelete(File file, boolean quietly) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File each : files) {
                recursiveDelete(each, quietly);
            }
        }
        deleteSingleFile(file, quietly);
    }

    private void deleteSingleFile(File file, boolean quietly) {
        if (quietly) {
            TempFileUtils.deleteAndFailQuietily(file, owner);
        } else {
            TempFileUtils.deleteOrException(file, owner);
        }
    }

    private static class TempEntry {
        public final boolean diretory;
        public final File file;

        private TempEntry(File file, boolean diretory) {
            this.file = file;
            this.diretory = diretory;
        }
    }
}
