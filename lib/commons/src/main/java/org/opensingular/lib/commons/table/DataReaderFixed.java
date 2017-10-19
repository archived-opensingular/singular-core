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

import org.opensingular.lib.commons.base.SingularException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class DataReaderFixed extends DataReader {

    private final DataReader original;

    private final List<LineData> lines;

    public DataReaderFixed(DataReader original) {
        this.original = original;
        lines = new ArrayList<>();
    }

    public DataReaderFixed(DataReader original, List<LineData> lines) {
        this.original = original;
        this.lines = lines;
    }

    public void add(LineData line) {
        lines.add(line);
    }

    @Override
    public Iterator<LineData> iterator() {
        return lines.iterator();
    }

    @Override
    public boolean isEmpty() {
        return lines.isEmpty();
    }

    @Override
    public List<LineData> preLoadData() {
        return lines;
    }

    @Override
    public DataReader getChildren(LineData lineData) {
        return original.getChildren(lineData);
    }

    @Override
    public LineInfo retrieveValues(LineReadContext ctx, LineData lineData) {
        throw new SingularException("Nunca deveria ter chamado essa linha. Pois tudo est em memoria");
    }

    @Override
    public List<LineData> preLoadDataAndCells(TableTool tableTool) {
        return lines;
    }
}