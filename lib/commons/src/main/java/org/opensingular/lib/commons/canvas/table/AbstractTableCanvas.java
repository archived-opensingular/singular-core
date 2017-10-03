package org.opensingular.lib.commons.canvas.table;

import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;

public class AbstractTableCanvas {

    private final RawHtmlBuilder rawHtmlBuilder;

    public AbstractTableCanvas(RawHtmlBuilder rawHtmlBuilder) {
        this.rawHtmlBuilder = rawHtmlBuilder;
    }

    public RawHtmlBuilder getRawHtmlBuilder() {
        return rawHtmlBuilder;
    }
}
