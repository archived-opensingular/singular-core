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

import com.google.common.collect.Lists;
import org.opensingular.lib.commons.base.SingularException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

final class DataReaderByIterator extends DataReader {

    private final TreeLineReader reader;
    private final int numLevel;

    private boolean empty = true;
    private boolean read;
    private Iterator<?> original;

    private LineData current;

    private List<LineData> dataLines;

    public DataReaderByIterator(TreeLineReader reader, Object list, int numLevel) {
        this.reader = reader;
        this.numLevel = numLevel;

        if (list != null) {
            original = GeneratorUtil.obterIterador(list);
            next();
            empty = (current == null);
        }
    }

    @Override
    public boolean isEmpty() {
        return empty;
    }

    private void next() {
        if (original == null) {
            throw new SingularException("No existe nada para ler");
        }
        while (original.hasNext()) {
            Object v = original.next();
            if (v != null) {
                current = new LineData(this, v);
                return;
            }
        }
        current = null;
        original = null;
    }

    @Override
    public Iterator<LineData> iterator() {
        if (dataLines != null) {
            return dataLines.iterator();
        }
        if (read) {
            throw new SingularException("No pode ser chamado iterator() duas vezes");
        }
        read = true;

        // Retorna a lista pulando os nulos
        return new Iterator<LineData>() {
            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public LineData next() {
                if(!hasNext()){
                    throw new NoSuchElementException();
                }
                LineData next = current;
                DataReaderByIterator.this.next();
                return next;
            }
        };
    }

    @Override
    public List<LineData> preLoadData() {
        if (dataLines == null) {
            if (isEmpty()) {
                dataLines = new ArrayList<>(1);
            } else {
                dataLines = Lists.newArrayList(this);
            }
        }
        return dataLines;
    }

    @Override
    public DataReaderByIterator getChildren(LineData lineData) {
        return new DataReaderByIterator(reader, reader.getChildren(lineData.getValue()), numLevel + 1);
    }

    @Override
    public LineInfo retrieveValues(LineReadContext ctx, LineData lineData) {
        LineInfo line = ctx.getTable().newBlankLine();
        reader.retrieveValues(ctx, lineData.getValue(), line);
        return line;
    }

    @Override
    public List<LineData> preLoadDataAndCells(TableTool tableTool) {
        List<LineData> list = preLoadData();
        LineReadContext ctx = new LineReadContext(tableTool);
        ctx.setLevel(numLevel);
        for (LineData data : list) {
            data.retrieveValues(ctx, numLevel, true, true);
        }
        return list;
    }

}
