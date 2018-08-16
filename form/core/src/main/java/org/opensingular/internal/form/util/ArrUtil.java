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

package org.opensingular.internal.form.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Métodos utilitário para permitir trabalhar com a escrita e leitura de dados a partir de um array de dados cuja a
 * criação é lazy. Esse métodos adiarão a criação do array o máximo possível. O array manipulado, também será expandido
 * sob demanda.
 *
 * @author Daniel C. Bordin on 01/05/2017.
 */
public final class ArrUtil {

    private ArrUtil() {}

    /**
     * Seta o valor informado na posição indicada. Criará um novo array (copiando os dados anteriores),
     * se o array for null ou se a posição a ser setada extrapolar o tamanho atual do array.
     *
     * @param classArray     Tipo do array (para o caso de precisar criar um novo array)
     * @param defaultMaxSize Tamanho do array, caso tenha que ser criado do zero ou necessário fazer copia.
     * @return O array original ou um novo array (se necessário expadir/criar o mesmo)
     */
    @Nullable
    public final static <T> T[] arraySet(@Nullable T[] original, int index, @Nullable T value, Class<T> classArray,
            int defaultMaxSize) {
        T[] content = original;
        if (content == null) {
            if (value == null) {
                return null;
            }
            content = (T[]) Array.newInstance(classArray, Math.max(index + 1, defaultMaxSize));
        } else if (content.length <= index) {
            if (value == null) {
                return content;
            }
            content = Arrays.copyOf(original, Math.max(index + 1, defaultMaxSize));
        }
        content[index] = value;
        return content;
    }

    /**
     * Retorna o valor do elemento na posição indicada do array. Não dispara exception, mesmo se o array for null ou se
     * a posição extrapolar o tamanho do array. Nesses últimos casos, retorna null.
     */
    @Nullable
    public final static <T> T arrayGet(@Nullable T[] original, int index) {
        return (original == null || original.length <= index) ? null : original[index];
    }

    /** Retorna uma lista apenas com os elementos não nulos do array informado, mesmo se o array for null. */
    @Nonnull
    public final static <T> List<T> arrayAsCollection(@Nullable T[] original) {
        if (original == null) {
            return Collections.emptyList();
        }
        int count = 0;
        for (int i = 0; i != original.length; i++) {
            if (original[i] != null) {
                count++;
            }
        }
        ArrayList<T> list = new ArrayList<>(count);
        for (int i = 0; i != original.length; i++) {
            if (original[i] != null) {
                list.add(original[i]);
            }
        }
        return list;
    }

    /**
     * Retorna um interador para o array informado, que devolve apenas o elementos não null. Se o array for null,
     * retorna um interador vazio.
     */
    @Nonnull
    public static <T> Iterator<T> arrayAsIterator(@Nullable T[] original) {
        if (original == null) {
            return Collections.emptyListIterator();
        }

        return new Iterator<T>() {
            private final T[] values = original;
            private int pos = findNext(0);

            private int findNext(int current) {
                for (int i = current; i < values.length; i++) {
                    if (values[i] != null) {
                        return i;
                    }
                }
                return -1;
            }

            @Override
            public boolean hasNext() {
                return pos != -1;
            }

            @Override
            public T next() {
                if (pos == -1) {
                    throw new NoSuchElementException();
                }
                T value = values[pos];
                pos = findNext(pos + 1);
                return value;
            }
        };
    }
}
