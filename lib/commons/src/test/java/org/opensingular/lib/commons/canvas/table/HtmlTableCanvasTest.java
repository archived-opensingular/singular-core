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

package org.opensingular.lib.commons.canvas.table;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;

public class HtmlTableCanvasTest {

    RawHtmlBuilder tableRawBuilder;
    HtmlTableCanvas htmlTableCanvas;

    @Before
    public void setUp() throws Exception {
        tableRawBuilder = new RawHtmlBuilder("table");
        htmlTableCanvas = new HtmlTableCanvas(tableRawBuilder);
    }

    @Test
    public void testCreateSimpleTable() throws Exception {
        TableHeadCanvas tableHeader = htmlTableCanvas.getTableHeader();
        tableHeader.addRow().addColumn("IDs");
        TableBodyCanvas tableBody = htmlTableCanvas.getTableBody();
        tableBody.addRow().addColumn("1");
        tableBody.addRow().addColumn("2");
        tableBody.addRow().addColumn("3");
        String html = tableRawBuilder.build();
        System.out.println(html);
        Assert.assertEquals("<table><thead><tr><th>IDs</th></tr></thead><tbody><tr><td>1</td></tr><tr><td>2</td></tr><tr><td>3</td></tr></tbody></table>", html);
    }


    @Test
    public void testCreateWithTwoColuns() throws Exception {
        TableHeadCanvas tableHeader = htmlTableCanvas.getTableHeader();
        TableRowCanvas headerRow = tableHeader.addRow();
        headerRow.addColumn("IDs");
        headerRow.addColumn("Nome");
        TableBodyCanvas tableBody = htmlTableCanvas.getTableBody();
        TableRowCanvas bodyRow_1 = tableBody.addRow();
        bodyRow_1.addColumn("1");
        bodyRow_1.addColumn("Danilo");
        TableRowCanvas bodyRow_2 = tableBody.addRow();
        bodyRow_2.addColumn("2");
        bodyRow_2.addColumn("Ronaldo");

        String html = tableRawBuilder.build();
        System.out.println(html);
        Assert.assertEquals("<table><thead><tr><th>IDs</th>" +
                "<th>Nome</th></tr></thead><tbody><tr><td>1</td>" +
                "<td>Danilo</td></tr><tr><td>2</td><td>Ronaldo</td></tr></tbody></table>", html);
    }
}