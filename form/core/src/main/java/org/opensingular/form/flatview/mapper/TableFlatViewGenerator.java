package org.opensingular.form.flatview.mapper;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.*;
import org.opensingular.form.flatview.AbstractFlatViewGenerator;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.flatview.FlatViewGenerator;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
import org.opensingular.lib.commons.canvas.EmptyDocumentCanvas;
import org.opensingular.lib.commons.canvas.FormItem;
import org.opensingular.lib.commons.canvas.table.TableBodyCanvas;
import org.opensingular.lib.commons.canvas.table.TableCanvas;
import org.opensingular.lib.commons.canvas.table.TableRowCanvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Show list of composites in a table
 */
public class TableFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        canvas.addSubtitle(context.getLabel());

        SIList<?> siList = context.getInstanceAs(SIList.class);
        SType<?> elementsType = siList.getElementsType();
        boolean renderCompositeFieldsAsColumns = elementsType.isComposite() && isRenderCompositeFieldAsColumns(siList);

        List<String> headerColumns = new ArrayList<>();
        if (renderCompositeFieldsAsColumns) {
            doRenderCompositeFieldAsColumns((STypeComposite<?>) elementsType, headerColumns);
        } else {
            String label = elementsType.asAtr().getLabel();
            if (label != null) {
                headerColumns.add(label);
            }
        }

        TableCanvas tableCanvas = canvas.addTable();

        if (!headerColumns.isEmpty()) {
            writeHeaders(headerColumns, tableCanvas);
        }

        TableBodyCanvas tableBody = tableCanvas.getTableBody();
        for (SInstance child : siList) {
            writeChild(renderCompositeFieldsAsColumns, tableBody, child);
        }
    }

    private void writeHeaders(List<String> headerColumns, TableCanvas tableCanvas) {
        TableRowCanvas tableHeaderRow = tableCanvas.getTableHeader().addRow();
        for (String column : headerColumns) {
            tableHeaderRow.addColumn(column);
        }
    }

    private void writeChild(boolean renderCompositeFieldsAsColumns, TableBodyCanvas tableBody, SInstance child) {
        TableRowDocumentCanvasAdapter row = new TableRowDocumentCanvasAdapter(tableBody.addRow());
        if (renderCompositeFieldsAsColumns) {
            for (SInstance compositeField : ((SIComposite) child).getAllFields()) {
                callListItemDoWrite(row, compositeField);
            }
        } else {
            callListItemDoWrite(row, child);
        }
    }

    private void doRenderCompositeFieldAsColumns(STypeComposite<?> elementsType, List<String> headerColumns) {
        for (SType<?> e : elementsType.getFields()) {
            if (e.asAtr().isVisible()) {
                headerColumns.add(e.asAtr().getLabel());
            }
        }
    }

    private boolean isRenderCompositeFieldAsColumns(SIList<?> siList) {
        SViewListByTable view = (SViewListByTable) siList.getType().getView();
        boolean renderCompositeFieldsAsColumns = false;
        if (view != null) {
            renderCompositeFieldsAsColumns = view.isRenderCompositeFieldsAsColumns();
        }
        return renderCompositeFieldsAsColumns;
    }

    private void callListItemDoWrite(TableRowDocumentCanvasAdapter row, SInstance field) {
        field.getAspect(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR)
                .ifPresent(viewGenerator -> viewGenerator
                        .writeOnCanvas(row, new FlatViewContext(field, true, true)));
    }

    public static class TableRowDocumentCanvasAdapter extends EmptyDocumentCanvas {
        private final TableRowCanvas tableRow;

        TableRowDocumentCanvasAdapter(TableRowCanvas tableRow) {
            this.tableRow = tableRow;
        }

        @Override
        public void addFormItem(FormItem formItem) {
            String value;
            if (StringUtils.isBlank(formItem.getValue())) {
                value = "-";
            } else {
                value = formItem.getValue();
            }
            tableRow.addColumn(value);
        }
    }

}
