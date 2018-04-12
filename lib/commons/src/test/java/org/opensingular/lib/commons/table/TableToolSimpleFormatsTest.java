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

import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Test;
import org.opensingular.lib.commons.views.ViewOutputFormat;
import org.opensingular.lib.commons.views.ViewOutputFormatExportable;
import org.opensingular.lib.commons.views.ViewsUtil;
import org.opensingular.lib.commons.views.format.FullPageHtmlGenerator;
import org.opensingular.lib.commons.views.format.ViewOutputHtmlWriterWrap;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author Daniel C. Bordin on 21/07/2017.
 */
public class TableToolSimpleFormatsTest extends TableToolSimpleBaseTest {

    public TableToolSimpleFormatsTest() {
        setOpenGeneratedFiles(false);
    }

    private void generateFormats(TableTool table) {
        generateFormats(table, "bootstrap.css");
        generateFormats(table, "alocpro.css");

        generateFormats(table, ViewOutputFormat.EXCEL);
        //generateFormats(table, ViewOutputFormat.PDF);
    }

    private void generateFormats(TableTool table, ViewOutputFormatExportable format) {
        generateFileAndShowOnDesktopForUser(
                tmpFileProvider -> ViewsUtil.exportToTempFile(table, format, tmpFileProvider));
    }

    private void generateFormats(TableTool table, String cssFile) {
        generateFileAndShowOnDesktopForUser("html", out -> {
            try (FullPageHtmlGenerator generator = new FullPageHtmlGenerator(out)) {
                generator.addInternalCSSFromResource(this, cssFile);
                generator.writeBegin();
                TableOutput outputHtml = new TableOutputHtml(new ViewOutputHtmlWriterWrap(generator.getOut(), false));
                table.generate(outputHtml);

                TableOutputSimulated output = new TableOutputSimulated();
                table.generate(output);
                generator.getOut().println("<br><xmp>");
                OutputStream os = new WriterOutputStream(generator.getOut());
                PrintStream pOut = new PrintStream(os);
                output.getResult().debug();
                output.getResult().debug(pOut);
                pOut.flush();
                generator.getOut().println("</xmp>");

                generator.writeEndAndClose();
            }
        });
    }

    @Test
    @Override
    public void testSimpleTable() {
        generateFormats(testSimpleTable_build());
    }

    @Override
    @Test
    public void testSimpleTable_withInvisibleColumn() {
        generateFormats(testSimpleTable_withInvisibleColumn_build());
    }

    @Test
    @Override
    public void testSimpleTable_dontShowTitle() {
        generateFormats(testSimpleTable_dontShowTitle_build());
    }

    @Test
    @Override
    public void testSimpleTable_empty1() {
        generateFormats(testSimpleTable_empty1_build());
    }

    @Test
    @Override
    public void testSimpleTable_empty2() {
        generateFormats(testSimpleTable_empty2_build());
    }

    @Test
    @Override
    public void testSimpleTable_withSuperTitle() {
        generateFormats(testSimpleTable_withSuperTitle_build());
    }

    @Test
    @Override
    public void testSimpleTable_withTotalizationLine1() {
        generateFormats(testSimpleTable_withTotalizationLine1_build());
    }

    @Test
    @Override
    public void testSimpleTable_withTotalizationLine2() {
        generateFormats(testSimpleTable_withTotalizationLine2_build());
    }

    @Test
    @Override
    public void testSimpleTable_withSuperTitleAndTotalization() {
        generateFormats(testSimpleTable_withSuperTitleAndTotalization_build());
    }

}
