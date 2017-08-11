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

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.views.ViewOutputFormat;

/**
 * @author Daniel C. Bordin on 24/07/2017.
 */
public class TableToolUtilTest {

    public static final ViewOutputFormat XXX = new ViewOutputFormat("XXX", "Xxx");

    @Test
    public void test() {
        TableTool table = createSimple();
        Assert.assertTrue(table.isDirectCompatiableWith(ViewOutputFormat.HTML));

        Assert.assertTrue(table.getDirectSupportedFormats().contains(ViewOutputFormat.HTML));
    }

    @Test
    public void testInvalidFormat() {
        TableTool table = createSimple();
        Assert.assertFalse(table.isDirectCompatiableWith(XXX));
        Assert.assertFalse(table.getDirectSupportedFormats().contains(XXX));
    }
    
    private TableTool createSimple() {
        TableTool table = new TableTool();
        table.addColumn(ColumnType.STRING,"A");
        PopulatorTable populator = table.createSimpleTablePopulator();
        populator.insertLine("L0");
        return table;
    }
}