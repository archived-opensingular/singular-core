package org.opensingular.lib.commons.canvas;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class HTMLCanvas implements DocumentCanvas {
    private final PrintWriter writer;
    private final boolean showTitleLevel;

    private String titlePrefix;
    private int index;
    private int headerTagLevel;
    private Map<Integer, HTMLCanvas> indexChildMap;

    public HTMLCanvas(PrintWriter writer, boolean showTitleLevel) {
        this.writer = writer;
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
        writer.write("<h" + headerTagLevel + ">");
        writer.write(prefix);
        writer.write(ObjectUtils.defaultIfNull(title, ""));
        writer.write("</h" + headerTagLevel + ">");
        if (headerTagLevel == 1) {
            headerTagLevel++;
        }
    }

    @Override
    public DocumentCanvas newChild() {
        int titleIndex = (index - 1);
        if (!indexChildMap.containsKey(titleIndex)) {
            HTMLCanvas newChild = new HTMLCanvas(writer, showTitleLevel);
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
        if (!StringUtils.isEmpty(label)) {
            writer.write("<span style='margin-right:15px;'>");
            writer.write("<label>");
            writer.write(label);
            writer.write(": </label>");
            writer.write(defaultIfNull(value, ""));
            writer.write("</span>");
        }
    }

    @Override
    public void breakLine() {
        writer.write("<br />");
    }

    public void stylesheet(String css) {
        writer.write("<style>");
        writer.write(css);
        writer.write("</style>");
    }
}