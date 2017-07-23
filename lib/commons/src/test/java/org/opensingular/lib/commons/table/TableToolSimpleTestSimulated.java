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

import org.junit.Test;

/**
 * @author Daniel C. Bordin on 18/04/2017.
 */
public class TableToolSimpleTestSimulated extends TableToolSimpleTestBase {


    @Test
    @Override
    public void testSimpleTable() {
        TableOutputSimulated output = generate(testSimpleTable_build());
        output.getResult().assertLinesSize(3);
        output.getResult().assertLine(0, null, null, null);
        output.getResult().assertLine(1, "P3", 3, 30);
        output.getResult().assertLine(2, "P10", 10, 100);
    }

    @Test
    @Override
    public void testSimpleTable_withInvisibleColumn() {
        TableOutputSimulated output = generate(testSimpleTable_withInvisibleColumn_build());

        output.getResult().assertLinesSize(3);
        output.getResult().assertLine(0, "A", "C");
        output.getResult().assertLine(1, "P3", 30);
        output.getResult().assertLine(2, "P10", 100);
    }

    @Test
    @Override
    public void testSimpleTable_dontShowTitle() {
        TableOutputSimulated output = generate(testSimpleTable_dontShowTitle_build());
        output.getResult().assertLinesSize(2);
        output.getResult().assertLine(0, "P3", 3, 30);
        output.getResult().assertLine(1, "P10", 10, 100);
    }

    @Test
    @Override
    public void testSimpleTable_empty1() {
        TableOutputSimulated output = generate(testSimpleTable_empty1_build());
        output.getResult().assertLinesSize(1);
        output.getResult().assertLine(0, "A", "B", "C");
    }

    @Test
    @Override
    public void testSimpleTable_empty2() {
        TableOutputSimulated output = generate(testSimpleTable_empty2_build());
        output.getResult().assertLinesSize(0);
    }

    @Test
    @Override
    public void testSimpleTable_withSuperTitle() {
        TableOutputSimulated output = generate(testSimpleTable_withSuperTitle_build());
        output.getResult().assertLinesSize(4);
        output.getResult().assertLine(0, "A", "#", "Super", "Super");
        output.getResult().assertLine(1, "A", "#", "B", "C");
        output.getResult().assertLine(2, "P3", "#", 3, 30);
        output.getResult().assertLine(3, "P10", "#", 10, 100);
    }

    @Test
    @Override
    public void testSimpleTable_withTotalizationLine1() {
        TableOutputSimulated output = generate(testSimpleTable_withTotalizationLine1_build());
        output.getResult().assertLinesSize(4);
        output.getResult().assertLine(0, "A", "B", "C");
        output.getResult().assertLine(1, "P3", 3, 30);
        output.getResult().assertLine(2, "P10", 10, 100);
        output.getResult().assertLine(3, "Total", 13, 130);
    }

    @Test
    @Override
    public void testSimpleTable_withTotalizationLine2() {
        TableOutputSimulated output = generate(testSimpleTable_withTotalizationLine2_build());
        output.getResult().assertLinesSize(4);
        output.getResult().assertLine(0, "A", "B", "C");
        output.getResult().assertLine(1, "P3", 3, 30);
        output.getResult().assertLine(2, "P10", 10, 100);
        output.getResult().assertLine(3, "Total", null, 130);
    }

    @Test
    @Override
    public void testSimpleTable_withSuperTitleAndTotalization() {
        TableOutputSimulated output = generate(testSimpleTable_withSuperTitleAndTotalization_build());
        //output.getResult().debug();
        output.getResult().assertLinesSize(7);
        output.getResult().assertLine(0, "A", "#", "Super", "Super", "#", "D");
        output.getResult().assertLine(1, "A", "#", "B", "C", null, "D");
        output.getResult().assertLine(2, "L0", "#", 3, "10,20", "#", "Flavio Almeida");
        output.getResult().assertLine(3, "L1", "#", 10, "20,20", "#", "Paulo");
        output.getResult().assertLine(4, "L2", "#", 5, "1.000,21", "#", null);
        output.getResult().assertLine(5, "L3", "#", 100, null, "#", "Andrades");
        output.getResult().assertLine(6, "Total", "#", null, "1.030,61", "#", null);
    }

    private TableOutputSimulated generate(TableTool table) {
        TableOutputSimulated output = new TableOutputSimulated();
        table.generate(output);
        return output;
    }
}
