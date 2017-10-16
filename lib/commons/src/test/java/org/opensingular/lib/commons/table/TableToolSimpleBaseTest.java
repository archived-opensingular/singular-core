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
import org.junit.Test;
import org.opensingular.lib.commons.junit.AbstractTestTempFileSupport;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * @author Daniel C. Bordin on 21/07/2017.
 */
public abstract class TableToolSimpleBaseTest extends AbstractTestTempFileSupport {

    @Test
    public abstract void testSimpleTable();

    public TableTool testSimpleTable_build() {
        TableTool table = new TableTool();
        table.addColumn(ColumnType.STRING);
        table.addColumn(ColumnType.INTEGER);
        table.addColumn(ColumnType.INTEGER);
        setSimpleData(table);
        return table;
    }

    public abstract void testSimpleTable_withInvisibleColumn();

    protected TableTool testSimpleTable_withInvisibleColumn_build() {
        TableTool table = createTableToolWith3Columns();
        table.getColumn(1).setVisible(false);
        setSimpleData(table);
        return table;
    }

    public abstract void testSimpleTable_dontShowTitle();

    protected TableTool testSimpleTable_dontShowTitle_build() {
        TableTool table = createTableToolWith3Columns();
        table.setShowTitles(false);
        setSimpleData(table);
        return table;
    }

    public abstract void testSimpleTable_empty1();

    protected TableTool testSimpleTable_empty1_build() {
        TableTool table = createTableToolWith3Columns();
        setSimpleData(table, Collections.emptyList());
        return table;
    }

    public abstract void testSimpleTable_empty2();

    protected TableTool testSimpleTable_empty2_build() {
        TableTool table = createTableToolWith3Columns();
        setSimpleData(table, Collections.emptyList());
        table.setShowTitles(false);
        return table;
    }

    public abstract void testSimpleTable_withSuperTitle();

    protected TableTool testSimpleTable_withSuperTitle_build() {
        TableTool table = createTableToolWith3Columns();
        table.addSuperTitle(1,2,"Super");
        setSimpleData(table);
        return table;
    }

    public abstract void testSimpleTable_withTotalizationLine1();

    protected TableTool testSimpleTable_withTotalizationLine1_build() {
        TableTool table = createTableToolWith3Columns();
        table.setShowTotalLine(true);
        setSimpleData(table);
        return table;
    }

    public abstract void testSimpleTable_withTotalizationLine2();

    protected TableTool testSimpleTable_withTotalizationLine2_build() {
        TableTool table = createTableToolWith3Columns();
        table.setShowTotalLine(true);
        table.getColumn(1).setTotalize(false);
        setSimpleData(table);
        return table;
    }

    public abstract void testSimpleTable_withSuperTitleAndTotalization();

    protected TableTool testSimpleTable_withSuperTitleAndTotalization_build() {
        TableTool table = new TableTool();
        table.addColumn(ColumnType.STRING,"A");
        table.addColumn(ColumnType.INTEGER, "B");
        table.addColumn(ColumnType.NUMBER, "C");
        table.addColumn(ColumnType.STRING, "D").setAlignmentCenter();
        table.addSuperTitle(1,2,"Super");
        table.setShowTotalLine(true);
        table.getColumn(1).setTotalize(false);

        TablePopulator populator = table.createSimpleTablePopulator();
        populator.insertLine("L0", 3, 10.2, "Flavio Almeida");
        populator.insertLine("L1", 10, 20.2, "Paulo");
        populator.insertLine("L2", 5, 1000.21, null);
        populator.insertLine("L3", 100, null, "Andrades");

        return table;
    }


    private void setSimpleData(TableTool table) {
        List<Integer> values = Lists.newArrayList(3, 10);
        setSimpleData(table, values);
    }

    private <T> void setSimpleData(TableTool table, Iterable<? extends T> values) {
        table.setLeitorTabelaSimples(values, (ctx, current, line) -> {
            line.get(0).setValor("P"+current);
            line.get(1).setValor(current);
            line.get(2).setValor(((Number) current).intValue() * 10);
        });
    }

    @Nonnull
    private TableTool createTableToolWith3Columns() {
        TableTool table = new TableTool();
        table.addColumn(ColumnType.STRING,"A");
        table.addColumn(ColumnType.INTEGER, "B");
        table.addColumn(ColumnType.INTEGER, "C");
        return table;
    }

}
