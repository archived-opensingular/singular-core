package org.opensingular.lib.commons.canvas.builder;

import org.apache.commons.lang3.StringEscapeUtils;

public class EscapedTextHtmlBuilder implements HtmlBuilder {
    private final String text;

    public EscapedTextHtmlBuilder(String text) {
        this.text = text;
    }

    @Override
    public String build() {
        return StringEscapeUtils.escapeHtml4(text);
    }
}