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

package org.opensingular.lib.commons.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TempFileUtils {

    /** Constante PREFIX. */
    public static final String PREFIX = "stream2file";

    /** Constante SUFFIX. */
    public static final String SUFFIX = ".tmp";

    private TempFileUtils() {
    }

    private static boolean exists(File f) {
        return (f != null) && f.exists();
    }

    /**
     * Tenta apagar o arquivo informado se o mesmo existir. Não conseguindo apagar ou ocorrendo uma exception ao
     * chamar {@link File#delete()}, faz log do erro mas não dispara exception.
     *
     * @param file      Arquivo a ser apagado
     * @param requester Classe junta a qual será gravado o log de erro do delete
     */
    public static void deleteAndFailQuietily(@Nonnull File file, @Nonnull Object requester) {
        delete(file, requester, true);
    }

    /**
     * Tenta apagar o arquivo informado se o mesmo existir. Não conseguindo apagar ou ocorrendo uma exception ao
     * chamar {@link File#delete()}, faz log do erro e dispara exception.
     *
     * @param file      Arquivo a ser apagado
     * @param requester Classe junta a qual será gravado o log de erro do delete
     */
    public static void deleteOrException(@Nonnull File file, @Nonnull Object requester) {
        delete(file, requester, false);
    }

    /**
     * Tenta apagar o arquivo informado se o mesmo existir. Sempre gera log de erro senão conseguri apagar.
     *
     * @param file         Arquivo a ser apagado
     * @param requester    Classe junta a qual será gravado o log de erro do delete
     * @param failQuietly Indica qual o comportamento se não conseguindo apagar ou ocorrendo uma exception ao
     *                     chamar {@link File#delete()}. Se true, engole a exception de erro. Se false, dispara
     *                     exception senão conseguir apagar ou se ocorrer exception na execução.
     */
    private static void delete(@Nonnull File file, @Nonnull Object requester, boolean failQuietly) {
        Objects.requireNonNull(requester);
        if (file.exists()) {
            try {
                if (!file.delete()) {
                    dealWithDeleteError(file, requester, failQuietly, null);
                }
            } catch (Exception e) {
                dealWithDeleteError(file, requester, failQuietly, e);
            }
        }
    }

    /**
     * Faz log do erro do delete e dispara exception se necessário.
     */
    private static void dealWithDeleteError(@Nonnull File file, @Nonnull Object requester, boolean failQuietly,
                                           @Nullable Exception e) {
        Class<?> req = requester instanceof Class ? (Class<?>) requester : requester.getClass();
        String msg = "Nao foi possível apagar o arquivo " + file;
        msg += " (solicitação da classe " + req.getName() + ")";

        if (failQuietly) {
            Logger logger = Logger.getLogger(req.getName());
            logger.log(Level.SEVERE, msg, e);
        } else {
            throw SingularException.rethrow(msg, e);
        }
    }

    /**
     * Converte uma stream para um file.
     *
     * @param in
     *            um in
     * @return um objeto do tipo File
     * @throws IOException
     *             Métodos subjeito a erros de entrada e saída.
     */
    public static File stream2file(InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();

        IOUtils.copy(in, new FileOutputStream(tempFile));

        return tempFile;
    }

    /**
     * Decode string para arquivo binário.
     *
     * @param base64String            uma string em base 64
     * @return um arquivo com o conteúdo
     * @throws IOException quando uma exceção de I/O ocorre.
     */
    public static File decodeToTempFile(String base64String) throws IOException {
        byte[] binaryDate = Base64.decodeBase64(base64String);
        return createTempFile(binaryDate);
    }

    /**
     * Tranfere string para arquivo binário.
     *
     * @param value            uma string
     * @return um arquivo com o conteúdo
     * @throws IOException quando uma exceção de I/O ocorre.
     */
    public static File transferToTempFile(String value) throws IOException {
        byte[] binaryDate = value.getBytes(Charset.forName("UTF-8"));
        return createTempFile(binaryDate);
    }

    /**
     * Criar temp file.
     *
     * @param binaryDate um arquivo binário
     * @return um arquivo com o conteúdo
     * @throws IOException quando uma exceção de I/O ocorre.
     */
    public static File createTempFile(byte[] binaryDate) throws IOException {
        File tempFile = File.createTempFile("file", "tmp");
        writeByteArrayToFile(tempFile, binaryDate);
        return tempFile;
    }

    /**
     * Escreve um array de bytes para um arquivo em disco.
     *
     * @param file
     *            um arquivo
     * @param bytes
     *            um bytes
     * @throws IOException
     *             Métodos subjeito a erros de entrada e saída.
     */
    public static void writeByteArrayToFile(File file, byte[] bytes) throws IOException {
        FileUtils.writeByteArrayToFile(file, bytes);
    }
}
