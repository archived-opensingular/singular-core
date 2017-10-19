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

import java.util.Iterator;
import java.util.List;

public final class DataReaderModifier extends DataReader {

    private DataReader original;

    private GenerationModifier nextModifier;

    public DataReaderModifier(DataReader original, GenerationModifier nextModifier) {
        this.original = original;
        this.nextModifier = nextModifier;
    }

    private DataReader getLeitorModificado() {
        if (nextModifier != null) {
            original = nextModifier.apply(original);
            nextModifier = null;
        }
        return original;
    }

    @Override
    public Iterator<LineData> iterator() {
        return getLeitorModificado().iterator();
    }

    @Override
    public boolean isEmpty() {
        return getLeitorModificado().isEmpty();
    }

    @Override
    public List<LineData> preLoadData() {
        return getLeitorModificado().preLoadData();
    }

    @Override
    public DataReader getChildren(LineData lineData) {
        return getLeitorModificado().getChildren(lineData);
    }

    @Override
    public LineInfo retrieveValues(LineReadContext ctx, LineData lineData) {
        return getLeitorModificado().retrieveValues(ctx, lineData);
    }

    @Override
    public List<LineData> preLoadDataAndCells(TableTool tableTool) {
        return getLeitorModificado().preLoadDataAndCells(tableTool);
    }
}