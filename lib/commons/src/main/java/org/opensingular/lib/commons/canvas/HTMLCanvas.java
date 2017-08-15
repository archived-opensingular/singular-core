package org.opensingular.lib.commons.canvas;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class HTMLCanvas implements DocumentCanvas {
    private final RawHtmlBuilder rawHtmlBuilder;
    private final boolean showTitleLevel;

    private String titlePrefix;
    private int index;
    private int headerTagLevel;
    private Map<Integer, HTMLCanvas> indexChildMap;

    public HTMLCanvas(boolean showTitleLevel) {
        this(new RawHtmlBuilder("div"), showTitleLevel);
    }

    public HTMLCanvas(RawHtmlBuilder rawHtmlBuilder, boolean showTitleLevel) {
        this.rawHtmlBuilder = rawHtmlBuilder;
        this.showTitleLevel = showTitleLevel;
        this.titlePrefix = "";
        this.index = 0;
        this.indexChildMap = new HashMap<>();
        this.headerTagLevel = 1;
    }


    @Override
    public void addTitle(String title) {
        String prefix = "";
        if (showTitleLevel) {
            if (index > 0) {
                prefix = titlePrefix + index + " ";
            }
            index++;
        }
        RawHtmlBuilder header = this.rawHtmlBuilder.newChild("h"+headerTagLevel);
        header.appendText(prefix);
        header.appendText(ObjectUtils.defaultIfNull(title, ""));
        if (headerTagLevel == 1) {
            headerTagLevel++;
        }
    }

    @Override
    public DocumentCanvas newChild() {
        int titleIndex = (index - 1);
        if (!indexChildMap.containsKey(titleIndex)) {
            HTMLCanvas newChild = new HTMLCanvas(rawHtmlBuilder.newChild("div"), showTitleLevel);
            if (showTitleLevel) {
                newChild.index = 1;
                newChild.headerTagLevel = childHeaderTagLevel();
                newChild.titlePrefix = titlePrefix + titleIndex + ".";
            }
            indexChildMap.put(titleIndex, newChild);
        }
        return indexChildMap.get(titleIndex);

    }

    private int childHeaderTagLevel() {
        int newHeaderTagLevel = headerTagLevel + 1;
        if (newHeaderTagLevel > 6) {
            return 6;
        }
        return newHeaderTagLevel;
    }

    @Override
    public void label(String label, String value) {
        RawHtmlBuilder span = this.rawHtmlBuilder.newChild("span");
        span.putAttribute("style", "margin-right:25px;");
        if (!StringUtils.isEmpty(label)) {
            RawHtmlBuilder labelComp = span.newChild("label");
            labelComp.putAttribute("style", "font-weight:bold;");
            labelComp.appendText(label);
            labelComp.appendText(": ");
        }
        span.appendText(defaultIfNull(value, ""));
    }

    @Override
    public void breakLine() {
        rawHtmlBuilder.newChild("br");
    }

    @Override
    public void list(List<String> values) {
        RawHtmlBuilder ul = this.rawHtmlBuilder.newChild("ul");
        for (String v : values) {
            RawHtmlBuilder li = ul.newChild("li");
            li.appendText(v);
        }
    }

    public void stylesheet(String css) {
        RawHtmlBuilder style = rawHtmlBuilder.newChild("style");
        style.appendTextWithoutEscape(css);
    }

    public String build() {
        return rawHtmlBuilder.build();
    }
}