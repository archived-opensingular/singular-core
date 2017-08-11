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

package org.opensingular.lib.commons.table;

import com.google.common.collect.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public final class GeradorUtil implements Serializable {

    private static final Logger LOGGER  = LoggerFactory.getLogger(GeradorUtil.class);

    /**
     * Conver o objeto (que pode ser um array, collection, iterator ou objeto
     * simples) em um iterador.
     *
     * @param filhos
     * @return Sempre diferente null.
     */
    public static Iterator<?> obterIterador(final Object filhos) {
        if (filhos == null) {
            return Collections.emptyIterator();
        } else if (filhos instanceof Iterator) {
            return (Iterator<?>) filhos;
        } else if (filhos instanceof Iterable) {
            return ((Iterable<?>) filhos).iterator();
        } else if (filhos instanceof Map) {
            return ((Map<?, ?>) filhos).entrySet().iterator();
        } else if (filhos instanceof Collection) {
            return ((Collection<?>) filhos).iterator();
        } else if (filhos.getClass().isArray()) {
            try {
                // If we're lucky, it is an array of objects
                // that we can iterate over with no copying
                return Arrays.asList((Object[]) filhos).iterator();
            } catch (ClassCastException e) {
                LOGGER.warn(e.getMessage(), e);
                // Rats -- it is an array of primitives
                int length = Array.getLength(filhos);
                ArrayList<Object> c = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    c.add(Array.get(filhos, i));
                }
                return c.iterator();
            }
        }

        return Iterators.forArray(filhos);
    }

    /** Verifica se a columna é de um tipo de ações da tabela. */
    private static boolean isAction(Column column) {
        return column.getProcessor() == ColumnTypeProcessor.ACTION;
    }

    /** Verifica se a celula de um columa de ações da tabela. */
    public static boolean isAction(InfoCelula cell) {
        return isAction(cell.getColumn());
    }

    private static abstract class LeitorArvore_ implements LeitorArvore, Serializable {
    }

    public static LeitorArvore toLeitorArvore(final Object lista, final LeitorLinha<Object> leitor) {
        return new LeitorArvore_() {

            @Override
            public Object getRaizes() {
                return lista;
            }

            @Override
            public Object getFilhos(Object item) {
                return null;
            }

            @Override
            public void recuperarValores(LineReadContext ctx, Object current, InfoLinha line) {
                leitor.recuperarValores(ctx, current, line);
            }
        };
    }
}
