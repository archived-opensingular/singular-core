package org.opensingular.lib.commons.canvas.table;

import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;

public class HtmlTableBodyCanvas extends AbstractTableCanvas implements TableBodyCanvas {
    public HtmlTableBodyCanvas(RawHtmlBuilder rawHtmlBuilder) {
        super(rawHtmlBuilder);
    }

    @Override
    public TableRowCanvas addRow() {
        return new HtmlTableRowCanvas(getRawHtmlBuilder().newChild("tr"));
    }
}
