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

package org.opensingular.lib.wicket.views;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.table.ColumnType;
import org.opensingular.lib.commons.table.PopulatorTable;
import org.opensingular.lib.commons.table.TableTool;
import org.opensingular.lib.commons.views.ViewOutputFormat;

/**
 * @author Daniel C. Bordin on 24/07/2017.
 */
public class ViewOutputWriterWicketTest {

    @Test
    public void test() {
        TableTool table = createSimple();
        Assert.assertTrue(table.isDirectCompatiableWith(ViewOutputFormat.HTML));
        Assert.assertTrue(table.isDirectCompatiableWith(ViewOutputWriterWicket.WICKET));
        Assert.assertTrue(table.getDirectSupportedFormats().contains(ViewOutputFormat.HTML));
        Assert.assertTrue(table.getDirectSupportedFormats().contains(ViewOutputWriterWicket.WICKET));
    }

    private TableTool createSimple() {
        TableTool table = new TableTool();
        table.addColumn(ColumnType.STRING,"A");
        PopulatorTable populator = table.createSimpleTablePopulator();
        populator.insertLine("L0");
        return table;
    }

}