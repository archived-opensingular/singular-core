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

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Métodos utilitários relacionados a arquivos e manipulação de IO.
 * <p>
 * <p>ATENÇÃO: Verifique senão existe a funcionalidade de interesse em {@link org.apache.commons.io.FileUtils} ou {@link
 * org.apache.commons.io.IOUtils}.</p>
 *
 * @author Daniel C. Bordin on 12/02/2017.
 */
public class SingularIOUtils {

    private static final String[] SUFFIXES = {"B", "KB", "MB", "GB", "TB"};
    private static final String[] TIME_SYMBOS = new String[]{"seconds", "minutes", "hours", "days"};

    private SingularIOUtils() {}

    /**
     * Retorna o tamanho de bytes formatado sem casa decimal e fazendo arrendondamento quando necessário. Usa base 1000.
     */
    @Nonnull
    public static String humanReadableByteCountRound(long bytes) {
        return humanReadableByteCountRound(bytes, true);
    }

    /**
     * Retorna o tamanho de bytes formatado sem casa decimal e fazendo arrendondamento quando necessário.
     *
     * @param si True, então usa base internacional (1000), senão usa base binária (1024).
     */
    @Nonnull
    public static String humanReadableByteCountRound(long bytes, boolean si) {
        int posSufix = 0;
        int unit = si ? 1000 : 1024;
        double bytesSize = bytes;

        while (bytesSize > 900 && posSufix < SUFFIXES.length - 1) {
            bytesSize = bytesSize / unit;
            posSufix++;
        }

        return Math.round(bytesSize) + " " + SUFFIXES[posSufix];
    }


    /**
     * Retorna o tamanho de bytes formatado com no máximo uma casa decimal usando base internacional (1000).
     * <pre><code>
     *                               SI     BINARY
     *                    0:        0 B        0 B
     *                   27:       27 B       27 B
     *                  999:      999 B      999 B
     *                 1000:     1.0 kB     1000 B
     *                 1023:     1.0 kB     1023 B
     *                 1024:     1.0 kB    1.0 KiB
     *                 1728:     1.7 kB    1.7 KiB
     *               110592:   110.6 kB  108.0 KiB
     *              7077888:     7.1 MB    6.8 MiB
     *            452984832:   453.0 MB  432.0 MiB
     *          28991029248:    29.0 GB   27.0 GiB
     *        1855425871872:     1.9 TB    1.7 TiB
     *  9223372036854775807:     9.2 EB    8.0 EiB   (Long.MAX_VALUE)
     *    </code></pre>
     */
    @Nonnull
    public static String humanReadableByteCount(long bytes) {
        return humanReadableByteCount(bytes, true);
    }

    /**
     * Retorna o tamanho de bytes formatado com no máximo uma casa decimal.
     * <pre><code>
     *                               SI     BINARY
     *                    0:        0 B        0 B
     *                   27:       27 B       27 B
     *                  999:      999 B      999 B
     *                 1000:     1.0 kB     1000 B
     *                 1023:     1.0 kB     1023 B
     *                 1024:     1.0 kB    1.0 KiB
     *                 1728:     1.7 kB    1.7 KiB
     *               110592:   110.6 kB  108.0 KiB
     *              7077888:     7.1 MB    6.8 MiB
     *            452984832:   453.0 MB  432.0 MiB
     *          28991029248:    29.0 GB   27.0 GiB
     *        1855425871872:     1.9 TB    1.7 TiB
     *  9223372036854775807:     9.2 EB    8.0 EiB   (Long.MAX_VALUE)
     *    </code></pre>
     *
     * @param si True, então usa base internacional (1000), senão usa base binária (1024).
     */
    @Nonnull
    public static String humanReadableByteCount(long bytes, boolean si) {
        //ver http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Formata os milisegundos na aproximação mais interessante para entendimento humando reduzindo um pouco a precisão
     * do resultado.
     */
    @Nonnull
    public static String humanReadableMiliSeconds(long mili) {
        int[] unit = {60, 60, 24, 0};
        if (mili < 1000) return mili + " ms";
        double value = mili / 1000d;
        int pos = 0;
        while (value >= unit[pos] && pos != unit.length - 1) {
            value = value / unit[pos];
            pos++;
        }
        return String.format("%.1f %s", value, TIME_SYMBOS[pos]);
    }

    /**
     * Serializa e deserializa o mesmo objeto retornando o resultado da deserialização. Útil na automação de testes.
     *
     * @param activeJavaDebugInfo Se true, ativa as informações detalhadas de serialização (NÃO USAR EM PRODUÇÃO)
     */
    public static <T> T serializeAndDeserialize(@Nonnull T obj, boolean activeJavaDebugInfo) {
        if (activeJavaDebugInfo) {
            System.setProperty("sun.io.serialization.extendedDebugInfo", "true");
        }
        return serializeAndDeserialize(obj);
    }

    /** Serializa e deserializa o mesmo objeto retornando o resultado da deserialização. Útil na automação de testes.*/
    public static <T> T serializeAndDeserialize(@Nonnull T obj) {
        try {
            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
            ObjectOutputStream out2 = new ObjectOutputStream(out1);
            out2.writeObject(obj);
            out2.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out1.toByteArray()));
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
