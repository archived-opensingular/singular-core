package org.opensingular.lib.commons.table;

import org.junit.Before;
import org.junit.Test;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.lib.commons.views.format.ViewOutputExcel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TableToolSimpleExcelTest extends TableToolSimpleBaseTest {

    private TableOutputExcel tableOutputExcel;
    private ViewOutputExcel viewOutputExcel;

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
        try {
            File xlsx = File.createTempFile("test", ".xlsx");
            FileOutputStream fos = new FileOutputStream(xlsx);
            viewOutputExcel.write(fos);
            if (OPEN_GENERATED_FILE) {
                SingularTestUtil.showFileOnDesktopForUser(xlsx);
            } else {
                xlsx.deleteOnExit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
