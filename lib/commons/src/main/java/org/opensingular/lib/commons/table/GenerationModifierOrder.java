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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class GenerationModifierOrder extends GenerationModifier {

    private final List<Column> columnsOrder = new ArrayList<>();

    private final boolean descending;

    public GenerationModifierOrder(TableTool table, Column c, boolean descending) {
        super(table);
        addColumn(c);
        this.descending = descending;
    }

    public void addColumn(Column column) {
        columnsOrder.add(column);
    }

    @Override
    public DataReader apply(DataReader original) {
        List<LineData> lines = original.preLoadDataAndCells(getTable());

        Column[] order = columnsOrder.toArray(new Column[columnsOrder.size()]);
        Comparator<LineData> comp = new Comparator<LineData>() {
            @Override
            public int compare(LineData d1, LineData d2) {
                for (int i = 0; i < order.length; i++) {
                    InfoCell c1 = d1.getInfoCell(order[i]);
                    InfoCell c2 = d2.getInfoCell(order[i]);
                    int r = descending ? order[i].compare(c2, c1) : order[i].compare(c1, c2);
                    if (r != 0) {
                        return r;
                    }
                }
                return 0;
            }
        };

        Collections.sort(lines, comp);

        return super.apply(new DataReaderFixed(original, lines));
    }
}
