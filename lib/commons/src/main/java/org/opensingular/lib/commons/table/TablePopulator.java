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
import java.util.List;

public class TablePopulator {

    private final TableTool tableTool;

    private final List<InfoCell[]> cells = new ArrayList<>();

    private InfoCell last;

    public TablePopulator(TableTool tableTool) {
        this.tableTool = tableTool;
    }

    public TablePopulator insertLine() {
        cells.add(new InfoCell[tableTool.getColumns().size()]);
        last = null;
        return this;
    }

    public TablePopulator insertLine(Object... values) {
        insertLine();
        setValues(values);
        return this;
    }

    public InfoCell last() {
        return last;
    }

    public InfoCell setValue(Column column, Object value) {
        return setValue(column.getIndex(), value);
    }

    public InfoCell setValue(int pos, Object value) {
        return setValue(cells.get(cells.size() - 1), pos, value);
    }

    private InfoCell setValue(InfoCell[] line, int pos, Object value) {
        if (line[pos] == null) {
            line[pos] = new InfoCell(tableTool.getColumn(pos));
        }
        line[pos].setValue(value);
        last = line[pos];
        return line[pos];
    }

    public InfoCell setValues(Object... values) {
        InfoCell[] line = cells.get(cells.size() - 1);
        for (int i = 0; i < values.length; i++) {
            setValue(line, i, values[i]);
        }
        return line[values.length - 1];
    }

    public boolean isEmpty() {
        return cells.isEmpty();
    }

    @SuppressWarnings("serial")
    public TreeLineReader asTreeLineReader() {
        return new TreeLineReader() {

            @Override
            public Object getRoots() {
                return cells;
            }

            @Override
            public Object getChildren(Object item) {
                return null;
            }

            @Override
            public void retrieveValues(LineReadContext ctx, Object current, LineInfo line) {
                InfoCell[] info = (InfoCell[]) current;
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i] != null) {
                            line.setCell(i, info[i]);
                            // Seta de novo o valor pois a coluna pode fazer
                            // algum tratamento sobre o valor
                            line.get(i).setValue(info[i].getValue());
                        }
                    }
                }
            }
        };
    }

}
