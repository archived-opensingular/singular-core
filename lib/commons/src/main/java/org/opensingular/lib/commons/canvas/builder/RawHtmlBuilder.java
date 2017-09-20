package org.opensingular.lib.commons.canvas.builder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RawHtmlBuilder implements HtmlBuilder {
    private final String tag;

    private List<HtmlBuilder> childs = new ArrayList<>();
    private Map<String, String> attribues = new LinkedHashMap<>();
    private StringBuilder buffer = new StringBuilder();

    public RawHtmlBuilder(String tag) {
        this.tag = tag;
    }

    private void startTag() {
        buffer.append('<').append(tag);
        for (Map.Entry<String, String> attr : attribues.entrySet()) {
            buffer.append(' ');
            buffer.append(attr.getKey());
            buffer.append('=');
            buffer.append('\'').append(attr.getValue()).append('\'');
        }
        buffer.append('>');
    }

    private void endTag() {
        buffer.append("</").append(tag).append('>');
    }

    public RawHtmlBuilder newChild(String tag) {
        RawHtmlBuilder rawHtmlBuilder = new RawHtmlBuilder(tag);
        childs.add(rawHtmlBuilder);
        return rawHtmlBuilder;
    }

    public RawHtmlBuilder putAttribute(String key, String val) {
        attribues.put(key, val);
        return this;
    }

    public RawHtmlBuilder appendText(String text) {
        childs.add(new EscapedTextHtmlBuilder(text));
        return this;
    }

    @Override
    public String build() {
        startTag();
        for (HtmlBuilder builder : childs) {
            buffer.append(builder.build());
        }
        endTag();
        return buffer.toString();
    }

    public RawHtmlBuilder appendAttribute(String attr, String value, String separator) {
        if (!attribues.containsKey(attr)) {
            putAttribute(attr, value);
        } else {
            putAttribute(attr, attribues.get(attr) + separator + value);
        }
        return this;
    }

    public void appendTextWithoutEscape(String text) {
        childs.add(new TextHtmlBuilder(text));
    }
}