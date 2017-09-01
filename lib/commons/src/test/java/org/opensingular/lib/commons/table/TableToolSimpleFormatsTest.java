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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.lib.commons.util.TempFileUtils;
import org.opensingular.lib.commons.views.ViewOutputFormatExportable;
import org.opensingular.lib.commons.views.ViewsUtil;
import org.opensingular.lib.commons.views.format.FullPageHtmlGenerator;
import org.opensingular.lib.commons.views.format.ViewOutputHtmlWriterWrap;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author Daniel C. Bordin on 21/07/2017.
 */
public class TableToolSimpleFormatsTest extends TableToolSimpleBaseTest {

    private static TempFileProvider tmpProvider;

    @BeforeClass
    public static void createTmpProvider() {
        tmpProvider = TempFileProvider.createForUseInTryClause(TableToolSimpleFormatsTest.class);
    }

    @AfterClass
    public static void cleanTmpProvider() {
        if (OPEN_GENERATED_FILE) {
            SingularTestUtil.waitMilli(10000);
            tmpProvider.deleteOrException();
        }
    }

    public void cleanTmpProvider2() {
        if (!OPEN_GENERATED_FILE) {
            tmpProvider.deleteOrException();
        }
    }

    private void generateFormats(TableTool table) {
        generateFormats(table, "bootstrap.min.css");
        generateFormats(table, "alocpro.css");

       // generateFormats(table, ViewOutputFormat.EXCEL);
        //generateFormats(table, ViewOutputFormat.PDF);
    }

    private void generateFormats(TableTool table, ViewOutputFormatExportable format) {
        File arq = ViewsUtil.exportToTempFile(table, format);
        try {
            if (OPEN_GENERATED_FILE) {
                SingularTestUtil.showFileOnDesktopForUser(arq);
            }
        } finally {
            TempFileUtils.deleteOrException(arq, this);
        }

    }
    private void generateFormats(TableTool table, String cssFile) {
        File arq = tmpProvider.createTempFile(".html");
        try (FullPageHtmlGenerator generator = new FullPageHtmlGenerator(arq)) {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (OPEN_GENERATED_FILE) {
            SingularTestUtil.showFileOnDesktopForUser(arq);
        }
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
