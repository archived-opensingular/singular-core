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
import java.util.List;

public final class LineData {

    private DataReader parent;
    private final Object value;

    private DataReader children;

    private Integer lines;

    private LineInfo line;

    LineData(LineInfo line) {
        this.value = null;
        this.line = line;
    }

    public LineData(DataReader parent, Object value) {
        this.parent = parent;
        this.value = value;
    }

    LineData(LineData original, DataReader newChildren) {
        parent = null; // Nunca deve ler
        value = original.value;
        line = original.line;
        children = newChildren;
    }

    public Object getValue() {
        return value;
    }

    public DataReader getChildrenReader() {
        if (children == null && parent != null) {
            children = parent.getChildren(this);
        }
        return children;
    }

    public void setParentReader(DataReader newReader) {
        parent = newReader;
    }

    public List<LineData> getChildren() {
        DataReader childrenReader = getChildrenReader();
        if(childrenReader != null) {
            return childrenReader.preLoadData();
        }
        return Collections.emptyList();
    }

    public LineInfo retrieveValues(LineReadContext ctx, int numLevel, boolean cleanContextAndColumns, boolean save) {
        if (line == null) {
            if (cleanContextAndColumns) {
                //ctx.clean(numNivel);
            }
            LineInfo result = parent.retrieveValues(ctx, this);
            if (save) {
                line = result;
            }
            return result;
        } else {
            return line;
        }
    }

    public LineInfo getLine() {
        return line;
    }
    
    public InfoCell getInfoCell(Column c) {
        return line.get(c);
    }
    
    public <K> K getValue(int index) {
        return getValue(index, null);
    }
    
    public <K> K getValue(int index, K defaultValue) {
        InfoCell infoCell = line.get(index);
        K value = infoCell.getValue();
        return value == null ? defaultValue : value;
    }

    public int getLines() {
        if (lines == null) {
            lines = getChildren().stream().mapToInt(p -> p.getLines()).sum();
            if (lines.intValue() == 0) {
                lines = 1;
            }
        }
        return lines;
    }

    public List<LineData[]> normalizeLevels(int max) {
        List<LineData[]> list = new ArrayList<>(getLines());
        LineData[] newLine = new LineData[max];
        newLine[0] = this;
        list.add(newLine);
        normalizeChildren(list, 1, max);
        return list;
    }

    private void normalizeChildren(List<LineData[]> list, int depth, int max) {
        if (depth > max + 2) {
            return;
        }
        for (int i = 0; i < getChildren().size(); i++) {
            LineData child = getChildren().get(i);
            LineData[] currentLine;
            if (i == 0) {
                currentLine = list.get(list.size() - 1);
            } else {
                currentLine = new LineData[max];
                list.add(currentLine);
            }
            currentLine[depth] = child;
            child.normalizeChildren(list, depth + 1, max);
        }
    }
}