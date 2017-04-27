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
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.opensingular.lib.commons.table.Column.TipoColuna;

import java.util.Collections;
import java.util.List;

/**
 * @author Daniel C. Bordin on 18/04/2017.
 */
public class TableToolSimpleTest {

    @Test
    public void testSimpleTable() {
        TableTool table = new TableTool();
        table.addColumn(TipoColuna.tpString);
        table.addColumn(TipoColuna.tpInteger);
        table.addColumn(TipoColuna.tpInteger);
        TableOutputSimulated output = generateWithSimpleData(table);

        output.getResult().assertLinesSize(3);
        output.getResult().assertLine(0, null, null, null);
        output.getResult().assertLine(1, "P3", 3, 30);
        output.getResult().assertLine(2, "P10", 10, 100);
    }

    @Test
    public void testSimpleTable_withInvisibleColumn() {
        TableTool table = createTableToolWith3Columns();
        table.getColumn(1).setVisible(false);
        TableOutputSimulated output = generateWithSimpleData(table);

        output.getResult().assertLinesSize(3);
        output.getResult().assertLine(0, "A", "C");
        output.getResult().assertLine(1, "P3", 30);
        output.getResult().assertLine(2, "P10", 100);
    }

    @Test
    public void testSimpleTable_dontShowTitle() {
        TableTool table = createTableToolWith3Columns();
        table.setShowTitles(false);
        TableOutputSimulated output = generateWithSimpleData(table);

        output.getResult().assertLinesSize(2);
        output.getResult().assertLine(0, "P3", 3, 30);
        output.getResult().assertLine(1, "P10", 10, 100);
    }

    @Test
    public void testSimpleTable_empty() {
        TableTool table = createTableToolWith3Columns();
        TableOutputSimulated output = generateWithSimpleData(table, Collections.emptyList());
        output.getResult().debug();
        output.getResult().assertLinesSize(1);
        output.getResult().assertLine(0, "A", "B", "C");

        table.setShowTitles(false);
        output = generateWithSimpleData(table, Collections.emptyList());
        output.getResult().assertLinesSize(0);
    }

    @Test
    public void testSimpleTable_withSuperTitle() {
        TableTool table = createTableToolWith3Columns();
        table.addSuperTitulo(1,2,"Super");
        TableOutputSimulated output = generateWithSimpleData(table);

        output.getResult().assertLinesSize(4);
        output.getResult().assertLine(0, "A", "#", "Super", "Super");
        output.getResult().assertLine(1, "A", "#", "B", "C");
        output.getResult().assertLine(2, "P3", "#", 3, 30);
        output.getResult().assertLine(3, "P10", "#", 10, 100);
    }

    @Test
    public void testSimpleTable_withTotalizationLine1() {
        TableTool table = createTableToolWith3Columns();
        table.setTotalizar(true);
        TableOutputSimulated output = generateWithSimpleData(table);

        output.getResult().assertLinesSize(4);
        output.getResult().assertLine(0, "A", "B", "C");
        output.getResult().assertLine(1, "P3", 3, 30);
        output.getResult().assertLine(2, "P10", 10, 100);
        output.getResult().assertLine(3, "Total", 13, 130);
    }

    @Test
    public void testSimpleTable_withTotalizationLine2() {
        TableTool table = createTableToolWith3Columns();
        table.setTotalizar(true);
        table.getColumn(1).setTotalizar(false);
        TableOutputSimulated output = generateWithSimpleData(table);

        output.getResult().assertLinesSize(4);
        output.getResult().assertLine(0, "A", "B", "C");
        output.getResult().assertLine(1, "P3", 3, 30);
        output.getResult().assertLine(2, "P10", 10, 100);
        output.getResult().assertLine(3, "Total", null, 130);
    }

    private TableOutputSimulated generateWithSimpleData(TableTool table) {
        List<Integer> values = Lists.newArrayList(3, 10);
        return generateWithSimpleData(table, values);
    }

    private <T> TableOutputSimulated generateWithSimpleData(TableTool table, Iterable<? extends T> values) {
        table.setLeitorTabelaSimples(values, (ctx, current, line) -> {
            line.get(0).setValor("P"+current);
            line.get(1).setValor(current);
            line.get(2).setValor(((Number) current).intValue() * 10);
        });
        TableOutputSimulated output = new TableOutputSimulated();
        table.gerar(output);
        return output;
    }

    @NotNull
    private TableTool createTableToolWith3Columns() {
        TableTool table = new TableTool();
        table.addColumn(TipoColuna.tpString,"A");
        table.addColumn(TipoColuna.tpInteger, "B");
        table.addColumn(TipoColuna.tpInteger, "C");
        return table;
    }
}
