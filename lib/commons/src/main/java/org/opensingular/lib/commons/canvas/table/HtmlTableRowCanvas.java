package org.opensingular.lib.commons.canvas.table;

import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;

public class HtmlTableRowCanvas extends AbstractTableCanvas implements TableRowCanvas {
    public HtmlTableRowCanvas(RawHtmlBuilder rawHtmlBuilder) {
        super(rawHtmlBuilder);
    }

    @Override
    public void addColumn(String value) {
        getRawHtmlBuilder().newChild("td").appendText(value);
    }
}