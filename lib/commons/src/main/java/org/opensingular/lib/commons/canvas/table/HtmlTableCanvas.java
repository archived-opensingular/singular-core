package org.opensingular.lib.commons.canvas.table;

import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;

public class HtmlTableCanvas extends AbstractTableCanvas implements TableCanvas {
    public HtmlTableCanvas(RawHtmlBuilder rawHtmlBuilder) {
        super(rawHtmlBuilder);
    }

    @Override
    public TableHeadCanvas getTableHeader() {
        return new HtmlTableHeadCanvas(getRawHtmlBuilder().newChild("thead"));
    }

    @Override
    public TableBodyCanvas getTableBody() {
        return new HtmlTableBodyCanvas(getRawHtmlBuilder().newChild("tbody"));
    }
}