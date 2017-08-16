package org.opensingular.lib.commons.canvas.table;

import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;

public class HtmlTableHeadCanvas extends AbstractTableCanvas implements TableHeadCanvas {

    public HtmlTableHeadCanvas(RawHtmlBuilder rawHtmlBuilder) {
        super(rawHtmlBuilder);
    }

    @Override
    public TableRowCanvas addRow() {
        return new HtmlTableRowCanvas(getRawHtmlBuilder().newChild("th"));
    }
}
