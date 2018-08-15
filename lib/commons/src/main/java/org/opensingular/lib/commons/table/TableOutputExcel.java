/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.opensingular.lib.commons.ui.Alignment;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.commons.views.format.ViewOutputExcel;

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
    public void generateLineSimpleStart(@Nonnull OutputTableContext ctx, @Nonnull LineInfo line, int lineAlternation) {
        if (line.isShowLine()) {
            newRow();
        }
    }

    @Override
    public void generateLineSimpleEnd(@Nonnull OutputTableContext ctx) {
    }

    @Override
    public void generateLineTreeStart(@Nonnull OutputTableContext ctx, @Nonnull LineInfo line, int level) {
    }

    @Override
    public void generateLineTreeEnd(@Nonnull OutputTableContext ctx) {
    }

    @Override
    public void generateCell(@Nonnull OutputCellContext ctx) {
        XSSFCell cell = incrementColumnAndCreateNewCell();
        configAlignment(cell.getCellStyle(), ctx.getColumn().getAlignment());
        if (ctx.getValue() == null) {
            return;
        }
        switch (ctx.getCell().getColumn().getType()) {
            case INTEGER:
            case NUMBER:
            case MONEY:
                cell.setCellValue(((Number) ctx.getValue()).doubleValue());
                break;
            case STRING:
                cell.setCellValue((String) ctx.getValue());
                break;
            default:
                cell.setCellValue(ctx.generateFormatDisplayString());
                break;
        }
        viewOutputExcel.getOutput().autoSizeColumn(cell.getColumnIndex());
    }

    private void configAlignment(XSSFCellStyle cellStyle, Alignment alignment) {
        switch (alignment) {
            case CENTER:
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                break;
            case RIGHT:
                cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                break;
            case LEFT:
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
    public void generateTitleCell(@Nonnull OutputTableContext ctx, @Nonnull Column column, int rowSpan, boolean asSubTitle, boolean columnWithSeparator) {
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
        viewOutputExcel.getOutput().autoSizeColumn(cell.getColumnIndex());
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
    public void generateTotalBlockStart(@Nonnull OutputTableContext ctx) {
    }

    @Override
    public void generateTotalBlockEnd(@Nonnull OutputTableContext ctx) {
    }

    @Override
    public void generateTotalLineStart(@Nonnull OutputTableContext ctx, @Nonnull LineInfo totalLine, @Nonnull Decorator tempDecorator, int level) {
        if (totalLine.isShowLine()) {
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