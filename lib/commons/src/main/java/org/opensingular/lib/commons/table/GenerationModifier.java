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

import java.io.Serializable;
import java.util.List;

public abstract class GenerationModifier implements Serializable {

    private final TableTool table;

    private GenerationModifier next;

    public GenerationModifier(TableTool table) {
        this.table = table;
    }

    protected TableTool getTable() {
        return table;
    }

    protected List<Column> getColumns() {
        return table.getColumns();
    }

    public DataReader apply(DataReader dataReader) {
        if (next != null) {
            return next.apply(dataReader);
        }
        return dataReader;
    }

    public List<Column> adjustTitles(List<Column> visibleColumns) {
        if (next != null) {
            return next.adjustTitles(visibleColumns);
        }
        return visibleColumns;
    }

    public void addFimCadeia(GenerationModifier newModifier) {
        for (GenerationModifier current = this; ; current = current.next) {
            if (current.next == null) {
                current.next = newModifier;
                return;
            }
        }
    }

    public GenerationModifier getNextModifier() {
        return next;
    }

}
