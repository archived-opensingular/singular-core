package org.opensingular.lib.commons.canvas.table;

import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;

public class HtmlTableRowCanvas extends AbstractTableCanvas implements TableRowCanvas {
    private final TYPE type;

    public HtmlTableRowCanvas(RawHtmlBuilder rawHtmlBuilder, TYPE type) {
        super(rawHtmlBuilder);
        this.type = type;
    }

    @Override
    public void addColumn(String value) {
        getRawHtmlBuilder().newChild(type.tag).appendText(value);
    }


    public enum TYPE {
        HEAD("th"),
        BODY("td");

        private String tag;

        TYPE(String tag) {
            this.tag = tag;
        }
    }
}