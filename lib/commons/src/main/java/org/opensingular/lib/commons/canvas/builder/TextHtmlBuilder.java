package org.opensingular.lib.commons.canvas.builder;

public class TextHtmlBuilder implements HtmlBuilder {
    private final String text;

    public TextHtmlBuilder(String text) {
        this.text = text;
    }

    @Override
    public String build() {
        return text;
    }
}