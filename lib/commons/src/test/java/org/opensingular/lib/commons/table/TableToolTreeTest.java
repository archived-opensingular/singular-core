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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel C. Bordin on 18/04/2017.
 */
public class TableToolTreeTest {

    @Test
    public void testSimple() {
        TableTool table = createTableToolWith3Columns();
        TableOutputSimulated output = generateWithTreeData(table);

        output.getResult().assertLinesSize(8);
        output.getResult().assertLine(0, "A", "B", "C").assertLevel(0, 0);
        output.getResult().assertLine(1, "P3", 3, null).assertLevel(1, 0);
        output.getResult().assertLine(2, "P210", 210, null).assertLevel(2, 0);
        output.getResult().assertLine(3, "P21", 21, 1).assertLevel(3, 1);
        output.getResult().assertLine(4, "P2", 2, 2).assertLevel(4, 2);
        output.getResult().assertLine(5, "P1", 1, 2).assertLevel(5, 2);
        output.getResult().assertLine(6, "P10", 10, null).assertLevel(6, 0);
        output.getResult().assertLine(7, "P1", 1, 1).assertLevel(7, 1);
    }

    @Test
    public void tesWithTotalizationLine() {
        TableTool table = createTableToolWith3Columns();
        table.setShowTotalLine(true);
        TableOutputSimulated output = generateWithTreeData(table);

        output.getResult().assertLinesSize(9);
        output.getResult().assertLine(8, "Total", 223, null);
    }

    @Test
    public void testLimitingLevels() {
        TableTool table = createTableToolWith3Columns();
        table.setLevelLimit(2);
        TableOutputSimulated output = generateWithTreeData(table);

        output.getResult().debug();
        output.getResult().assertLinesSize(6);
        output.getResult().assertLine(0, "A", "B", "C").assertLevel(0, 0);
        output.getResult().assertLine(1, "P3", 3, null).assertLevel(1, 0);
        output.getResult().assertLine(2, "P210", 210, null).assertLevel(2, 0);
        output.getResult().assertLine(3, "P21", 21, 1).assertLevel(3, 1);
        output.getResult().assertLine(4, "P10", 10, null).assertLevel(4, 0);
        output.getResult().assertLine(5, "P1", 1, 1).assertLevel(5, 1);
    }

    @Test
    public void testPercetOfParent() {
        TableTool table = createTableToolWith3Columns();
        table.getColumn(1).setShowAsPercentageOfParent(true);
        TableOutputSimulated output = generateWithTreeData(table);

        output.getResult().debug();
        output.getResult().assertLinesSize(8);
        output.getResult().assertLine(0, "A", "B", "C").assertLevel(0, 0);
        output.getResult().assertLine(1, "P3", "100,0%", null).assertLevel(1, 0);
        output.getResult().assertLine(2, "P210", "100,0%", null).assertLevel(2, 0);
        output.getResult().assertLine(3, "P21", "10,0%", 1).assertLevel(3, 1);
        output.getResult().assertLine(4, "P2", "1,0%", 2).assertLevel(4, 2);
        output.getResult().assertLine(5, "P1", "0,5%", 2).assertLevel(5, 2);
        output.getResult().assertLine(6, "P10", "100,0%", null).assertLevel(6, 0);
        output.getResult().assertLine(7, "P1", "10,0%", 1).assertLevel(7, 1);
    }

    private TableOutputSimulated generateWithTreeData(TableTool table) {
        List<Integer> values = Lists.newArrayList(3, 210, 10);
        table.setLeitorArvore(new LeitorArvore() {
            @Override
            public Object getRaizes() {
                return values;
            }

            @Override
            public Object getFilhos(Object item) {
                int v = (Integer) item;
                if (v > 9) {
                    List<Integer> list = new ArrayList<>();
                    list.add(v / 10);
                    for (int i = ((v / 10) % 10) - 1; i > 0; i--) {
                        list.add(i);
                    }
                    return list;
                }
                return null;
            }

            @Override
            public void recuperarValores(LineReadContext ctx, Object current, InfoLinha line) {
                line.get(0).setValor("P" + current);
                line.get(1).setValor(current);
                line.get(2).setValor(ctx.getLevel());
            }
        });
        TableOutputSimulated output = new TableOutputSimulated();
        table.generate(output);
        return output;
    }

    @NotNull
    private TableTool createTableToolWith3Columns() {
        TableTool table = new TableTool();
        table.addColumn(ColumnType.STRING, "A");
        table.addColumn(ColumnType.INTEGER, "B");
        table.addColumn(ColumnType.INTEGER, "C");
        return table;
    }
}
