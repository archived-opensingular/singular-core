package org.opensingular.lib.commons.table;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.commons.views.ViewOutputExcel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class TableOutputExcel extends TableOutput implements Loggable {
    private final ViewOutputExcel viewOutputExcel;

    private int rowIndex = -1;
    private int columnIndex = -1;
    private XSSFRow row;
    private Map<Integer, Integer> columnRowSpanMap = new HashMap<>();


    public TableOutputExcel(ViewOutputExcel viewOutputExcel) {
        super();
        this.viewOutputExcel = viewOutputExcel;
    }

    private void incrementRow() {
        rowIndex++;
    }

    private void incrementColumn() {
        columnIndex++;
    }

    private void newRow() {
        incrementRow();
        row = viewOutputExcel.getOutput().createRow(rowIndex);
        columnIndex = -1;
    }

    @Override
    public String getUrlApp() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean isStaticContent() {
        return false;
    }

    @Override
    public void generateTableStart(@Nonnull OutputTableContext ctx, @Nonnull TableTool tableTool) {
    }

    @Override
    public void generateTableEnd(@Nonnull OutputTableContext ctx, @Nonnull TableTool tableTool) {
    }

    @Override
    public void generateBodyBlockStart(@Nonnull OutputTableContext ctx) {

    }

    @Override
    public void generateBodyBlockEnd(@Nonnull OutputTableContext ctx) {

    }

    @Override
    public void generateLineSimpleStart(@Nonnull OutputTableContext ctx, @Nonnull InfoLinha line, int lineAlternation) {
        if (line.isExibirLinha()) {
            newRow();
        }
    }

    @Override
    public void generateLineSimpleEnd(@Nonnull OutputTableContext ctx) {
    }

    @Override
    public void generateLineTreeStart(@Nonnull OutputTableContext ctx, @Nonnull InfoLinha line, int nivel) {
    }

    @Override
    public void generateLineTreeEnd(@Nonnull OutputTableContext ctx) {
    }

    @Override
    public void generateCell(@Nonnull OutputCellContext ctx) {
        XSSFCell cell = incrementColumnAndCreateNewCell();
        configurarAlinhamento(cell.getCellStyle(), ctx.getColumn().getAlinhamento());
        if (ctx.getValue() == null) {
            return;
        }
        switch (ctx.getCell().getColumn().getTipo()) {
            case tpInteger:
            case tpNumber:
            case tpMoney:
                cell.setCellValue(((Number) ctx.getValue()).doubleValue());
                break;
            default:
                cell.setCellValue(ctx.generateFormatDisplayString());
                break;
        }
    }

    private void configurarAlinhamento(XSSFCellStyle cellStyle, Column.TipoAlinhamento alinhamento) {
        switch (alinhamento) {
            case tpCentro:
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                break;
            case tpDireita:
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                break;
            case tpEsquerda:
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
                break;
        }
    }

    @Override
    public void generateTitleBlockStart(@Nonnull OutputTableContext ctx) {
    }

    @Override
    public void generateTitleBlockEnd(@Nonnull OutputTableContext ctx) {

    }

    @Override
    public void generateTitleLineStart(@Nonnull OutputTableContext ctx, boolean superTitleLine) {
        newRow();
    }

    @Override
    public void generateTitleLineEnd(@Nonnull OutputTableContext ctx, boolean superTitleLine) {

    }

    @Override
    public void generateTiltleCell(@Nonnull OutputTableContext ctx, @Nonnull Column column, int rowSpan, boolean asSubTitle, boolean columnWithSeparator) {
        if (column.getTitle() == null) {
            return;
        }
        incrementColumn();
        while (containsRowSpanForColumn()) {
            incrementColumn();
            if (containsRowSpanForColumn()) {
                columnRowSpanMap.put(columnIndex, columnRowSpanMap.get(columnIndex) - 1);
            }
        }
        XSSFCell cell = row.createCell(columnIndex);
        cell.setCellValue(column.getTitle());
        if (rowSpan > 1) {
            int spanOffset = rowSpan - 1;
            columnRowSpanMap.put(columnIndex, spanOffset);
            viewOutputExcel.getOutput().addMergedRegion(new CellRangeAddress(rowIndex, rowIndex + spanOffset, columnIndex, columnIndex));
        }
    }

    private boolean containsRowSpanForColumn() {
        return columnRowSpanMap.get(columnIndex) != null && columnRowSpanMap.get(columnIndex) > 0;
    }

    @Override
    public void generateTitleCellSuper(@Nonnull OutputTableContext ctx, @Nonnull Column column, int colSpan, boolean columnWithSeparator) {
        if (column.getTitle() != null) {
            XSSFCell cell = incrementColumnAndCreateNewCell();
            cell.setCellValue(column.getSuperTitle());
            if (colSpan > 1) {
                int spanOffset = colSpan - 1;
                viewOutputExcel.getOutput().addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, columnIndex, columnIndex += spanOffset));
            }
        }
    }

    private XSSFCell incrementColumnAndCreateNewCell() {
        incrementColumn();
        return row.createCell(columnIndex);
    }

    @Override
    public void generateTotalLineStart(@Nonnull OutputTableContext ctx, @Nonnull InfoLinha totalLine, @Nonnull Decorator tempDecorator, int level) {
        if (totalLine.isExibirLinha()) {
            newRow();
        }
    }

    @Override
    public void generateTotalLineEnd(@Nonnull OutputTableContext ctx) {

    }

    @Override
    public void generateTotalCellSkip(@Nonnull OutputTableContext ctx, @Nonnull Column column, boolean columnWithSeparator) {
        incrementColumn();
    }

    @Override
    public void generateTotalLabel(@Nonnull OutputTableContext ctx, @Nonnull Column column, @Nonnull String label, @Nonnull DecoratorCell tempDecorator, int level) {
        XSSFCell cell = incrementColumnAndCreateNewCell();
        cell.setCellValue("Total");
    }

    @Override
    public void generateTotalCell(@Nonnull OutputCellContext ctx, @Nullable Number value) {
        XSSFCell cell = incrementColumnAndCreateNewCell();
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
    }

}