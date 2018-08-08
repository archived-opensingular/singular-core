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

import java.util.List;
import java.util.Objects;

class GenerationModifierAgrupar extends GenerationModifier {

    private final Column column;

    public GenerationModifierAgrupar(TableTool table, Column column) {
        super(table);
        this.column = column;
        for (Column c : getColumns()) {
            if (c != column && c.getDataLevel() >= column.getDataLevel()) {
                c.setDataLevel(c.getDataLevel() + 1);
            }
        }
    }

    @Override
    public DataReader apply(DataReader dataReader) {
        DataReaderFixed group = new DataReaderFixed(null);
        Object currentValue = null;
        DataReaderFixed children = null;
        for (LineData line : dataReader) {
            InfoCell cell = line.getInfoCell(column);
            Object valueCellLine = cell.getValue();
            if (!Objects.equals(currentValue, valueCellLine)) {
                children = new DataReaderFixed(dataReader);
                group.add(new LineData(line, new DataReaderModifier(children, getNextModifier())));
                currentValue = valueCellLine;
            }
            if (children != null) {
                children.add(line);
            }
        }

        return group;
    }

    @Override
    public List<Column> adjustTitles(List<Column> visibleColumns) {
        int posColumn = visibleColumns.indexOf(column);
        if (posColumn != -1) {
            int posNew = posColumn;
            for (int i = posColumn - 1; i >= 0; i--) {
                Column c = visibleColumns.get(i);
                if (c.getDataLevel() > column.getDataLevel()) {
                    posNew = i;
                }
            }
            if (posNew != posColumn) {
                for (int i = posColumn; i > posNew; i--) {
                    visibleColumns.set(i, visibleColumns.get(i - 1));
                }
                visibleColumns.set(posNew, column);
            }
        }
        return super.adjustTitles(visibleColumns);
    }

}
