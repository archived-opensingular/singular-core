package org.opensingular.lib.commons.table;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

import static org.junit.Assert.*;

public class TableOutputExcelTest {

    private TableOutputExcel tableOutputExcel;
    private String worksheetName = "My Test Workshet";

    @Before
    public void setUp() throws Exception {
        tableOutputExcel = new TableOutputExcel(worksheetName);
    }

    @Test
    public void testWorkbookNotNull() throws Exception {
        assertNotNull(tableOutputExcel.getWorkbook());
    }

    @Test
    public void testWorksheetNotNull() throws Exception {
        assertNotNull(tableOutputExcel.getSheet());
    }

    @Test
    public void testWorksheetName() throws Exception {
        assertEquals(worksheetName, tableOutputExcel.getSheet().getSheetName());
    }

    @Test
    public void testWriteResult() throws Exception {
        File xlsx = File.createTempFile("test", ".xlsx");
        FileOutputStream fos = new FileOutputStream(xlsx);
        tableOutputExcel.writeResult(fos);
        assertTrue(xlsx.length() > 0L);
        xlsx.deleteOnExit();
    }
}