package org.opensingular.form.flatview.mapper;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.flatview.AbstractFlatViewGenerator;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.flatview.FlatViewGenerator;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
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
        canvas.addSubtitle(context.getLabelOrName());

        SIList<?> siList = context.getInstanceAs(SIList.class);
        SType<?> elementsType = siList.getElementsType();

        List<String> headerColumns = new ArrayList<>();
        if (elementsType instanceof STypeComposite) {
            for (SType<?> e : ((STypeComposite<?>) elementsType).getFields()) {
                if(e.asAtr().isVisible()) {
                    headerColumns.add(e.asAtr().getLabel());
                }
            }
        } else {
            headerColumns.add(elementsType.asAtr().getLabel());
        }

        TableCanvas tableCanvas = canvas.addTable();

        TableRowCanvas tableHeaderRow = tableCanvas.getTableHeader().addRow();
        for (String column : headerColumns) {
            tableHeaderRow.addColumn(column);
        }

        TableBodyCanvas tableBody = tableCanvas.getTableBody();
        for (SInstance child : siList) {
            child.getAspect(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR)
                    .ifPresent(viewGenerator ->
                            viewGenerator
                                    .writeOnCanvas(new TableRowDocumentCanvasAdapter(tableBody.addRow()),
                                            new FlatViewContext(child, true)));
        }
    }

    public static class TableRowDocumentCanvasAdapter implements DocumentCanvas {
        private final TableRowCanvas tableRow;

        public TableRowDocumentCanvasAdapter(TableRowCanvas tableRow) {
            this.tableRow = tableRow;
        }

        @Override
        public void addSubtitle(String title) {

        }

        @Override
        public DocumentCanvas addChild() {
            return this;
        }

        @Override
        public void addFormItem(FormItem formItem) {
            tableRow.addColumn(formItem.getValue());
        }

        @Override
        public void addLineBreak() {

        }

        @Override
        public void addList(List<String> values) {

        }

        @Override
        public TableCanvas addTable() {
            return null;
        }
    }

}
