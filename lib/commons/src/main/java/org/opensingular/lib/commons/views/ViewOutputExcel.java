package org.opensingular.lib.commons.views;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.opensingular.lib.commons.util.Loggable;

import java.io.IOException;
import java.io.OutputStream;

public class ViewOutputExcel implements ViewOutput<XSSFSheet>, Loggable {
    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;

    public ViewOutputExcel(String worksheetName) {
        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet(worksheetName);
    }

    @Override
    public XSSFSheet getOutput() {
        return sheet;
    }

    @Override
    public ViewOutputFormat getFormat() {
        return ViewOutputFormat.EXCEL;
    }

    public void write(OutputStream os) {
        try {
            workbook.write(os);
        } catch (IOException e) {
            getLogger().error(e.getMessage(), e);
        }
    }
}