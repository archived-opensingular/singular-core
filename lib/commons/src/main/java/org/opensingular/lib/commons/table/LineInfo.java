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

import javax.annotation.Nonnull;

public class LineInfo {

    private final TableTool table;

    private final InfoCell[] cells;

    private Decorator decorator_;

    private boolean showLine = true;

    private int level;

    LineInfo(TableTool table) {
        this.table = table;
        cells = new InfoCell[table.getColumns().size()];
    }

    public Decorator getDecorator() {
        if (decorator_ == null) {
            decorator_ = new Decorator();
        }
        return decorator_;
    }

    public Decorator createTempDecorator() {
        return new Decorator(decorator_);
    }

    final boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }

    void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    final void setCell(int index, InfoCell cell) {
        cells[index] = cell;
    }

    @Nonnull
    public InfoCell get(@Nonnull Column c) {
        return get(c.getIndex());
    }

    @Nonnull
    public InfoCell get(int index) {
        if (cells[index] == null) {
            cells[index] = new InfoCell(table.getColumn(index));
        }
        return cells[index];
    }
}
