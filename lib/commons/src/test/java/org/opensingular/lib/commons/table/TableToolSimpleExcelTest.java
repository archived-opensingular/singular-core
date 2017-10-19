/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.commons.table;

import org.junit.Before;
import org.junit.Test;
import org.opensingular.lib.commons.views.format.ViewOutputExcel;

public class TableToolSimpleExcelTest extends TableToolSimpleBaseTest {

    private TableOutputExcel tableOutputExcel;
    private ViewOutputExcel viewOutputExcel;

    public TableToolSimpleExcelTest() {
        setOpenGeneratedFiles(false);
    }

    @Before
    public void setUp() throws Exception {
        viewOutputExcel = new ViewOutputExcel("X");
        tableOutputExcel = new TableOutputExcel(viewOutputExcel);
    }

    @Test
    @Override
    public void testSimpleTable() {
        testSimpleTable_build().generate(tableOutputExcel);
        writeAndOpenIfEnabled();
    }

    @Test
    @Override
    public void testSimpleTable_withInvisibleColumn() {
        testSimpleTable_withInvisibleColumn_build().generate(tableOutputExcel);
        writeAndOpenIfEnabled();
    }

    @Test
    @Override
    public void testSimpleTable_dontShowTitle() {
        testSimpleTable_dontShowTitle_build().generate(tableOutputExcel);
        writeAndOpenIfEnabled();
    }

    @Test
    @Override
    public void testSimpleTable_empty1() {
        testSimpleTable_empty1_build().generate(tableOutputExcel);
        writeAndOpenIfEnabled();
    }

    @Test
    @Override
    public void testSimpleTable_empty2() {
        testSimpleTable_empty2_build().generate(tableOutputExcel);
        writeAndOpenIfEnabled();
    }

    @Test
    @Override
    public void testSimpleTable_withSuperTitle() {
        testSimpleTable_withSuperTitle_build().generate(tableOutputExcel);
        writeAndOpenIfEnabled();
    }

    @Test
    @Override
    public void testSimpleTable_withTotalizationLine1() {
        testSimpleTable_withTotalizationLine1_build().generate(tableOutputExcel);
        writeAndOpenIfEnabled();
    }

    @Test
    @Override
    public void testSimpleTable_withTotalizationLine2() {
        testSimpleTable_withTotalizationLine2_build().generate(tableOutputExcel);
        writeAndOpenIfEnabled();
    }

    @Test
    @Override
    public void testSimpleTable_withSuperTitleAndTotalization() {
        testSimpleTable_withSuperTitleAndTotalization_build().generate(tableOutputExcel);
        writeAndOpenIfEnabled();
    }

    public void writeAndOpenIfEnabled() {
        generateFileAndShowOnDesktopForUser("xlsx", out -> {
            viewOutputExcel.write(out);
        });
    }
}
