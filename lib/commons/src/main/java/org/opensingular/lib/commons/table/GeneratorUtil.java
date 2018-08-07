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

public final class GeneratorUtil implements Serializable {

    private static final Logger LOGGER  = LoggerFactory.getLogger(GeneratorUtil.class);

    /**
     * Conver o objeto (que pode ser um array, collection, iterator ou objeto
     * simples) em um iterador.
     *
     * @param children
     * @return Sempre diferente null.
     */
    public static Iterator<?> obterIterador(final Object children) {
        if (children == null) {
            return Collections.emptyIterator();
        } else if (children instanceof Iterator) {
            return (Iterator<?>) children;
        } else if (children instanceof Iterable) {
            return ((Iterable<?>) children).iterator();
        } else if (children instanceof Map) {
            return ((Map<?, ?>) children).entrySet().iterator();
        } else if (children instanceof Collection) {
            return ((Collection<?>) children).iterator();
        } else if (children.getClass().isArray()) {
            try {
                // If we're lucky, it is an array of objects
                // that we can iterate over with no copying
                return Arrays.asList((Object[]) children).iterator();
            } catch (ClassCastException e) {
                LOGGER.warn(e.getMessage(), e);
                // Rats -- it is an array of primitives
                int length = Array.getLength(children);
                ArrayList<Object> c = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    c.add(Array.get(children, i));
                }
                return c.iterator();
            }
        }

        return Iterators.forArray(children);
    }

    /** Verifica se a columna é de um tipo de ações da tabela. */
    private static boolean isAction(Column column) {
        return column.getProcessor() == ColumnTypeProcessor.ACTION;
    }

    /** Verifica se a celula de um columa de ações da tabela. */
    public static boolean isAction(InfoCell cell) {
        return isAction(cell.getColumn());
    }

    private static abstract class TreeLineReader_ implements TreeLineReader, Serializable {
    }

    public static TreeLineReader toTreeLineReader(final Object list, final LineReader<Object> reader) {
        return new TreeLineReader_() {

            @Override
            public Object getRoots() {
                return list;
            }

            @Override
            public Object getChildren(Object item) {
                return null;
            }

            @Override
            public void retrieveValues(LineReadContext ctx, Object current, LineInfo line) {
                reader.retrieveValues(ctx, current, line);
            }
        };
    }
}
