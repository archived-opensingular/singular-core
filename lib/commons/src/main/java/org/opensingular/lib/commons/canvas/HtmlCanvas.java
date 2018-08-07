/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.commons.canvas;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;
import org.opensingular.lib.commons.canvas.table.HtmlTableCanvas;
import org.opensingular.lib.commons.canvas.table.TableCanvas;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class HtmlCanvas implements DocumentCanvas {
    private final boolean        showTitleLevel;
    private final RawHtmlBuilder rootHtmlBuilder;
    private       RawHtmlBuilder currentHtmlBuilder;

    private String                   titlePrefix;
    private int                      index;
    private int                      headerTagLevel;
    private Map<Integer, HtmlCanvas> indexChildMap;

    public HtmlCanvas(boolean showTitleLevel) {
        this(new RawHtmlBuilder("div"), showTitleLevel);
    }

    protected HtmlCanvas(RawHtmlBuilder rootHtmlBuilder, boolean showTitleLevel) {
        this.rootHtmlBuilder = rootHtmlBuilder;
        this.showTitleLevel = showTitleLevel;
        this.titlePrefix = "";
        this.index = 0;
        this.indexChildMap = new HashMap<>();
        this.headerTagLevel = 1;
        this.currentHtmlBuilder = rootHtmlBuilder;
    }

    @Override
    public void addSubtitle(String title) {
        String prefix = "";
        if (showTitleLevel) {
            if (index > 0) {
                prefix = titlePrefix + index + " ";
            }
            index++;
        }
        if (headerTagLevel == 1) {
            addPageHeader(prefix, title);
            headerTagLevel++;
        }
        else {
            RawHtmlBuilder header = createSubheaderTag(headerTagLevel);
            header.appendText(prefix);
            header.appendText(ObjectUtils.defaultIfNull(title, ""));
        }
    }

    protected RawHtmlBuilder createSubheaderTag(Integer headerTagLevel) {
        return currentHtmlBuilder.newChild("h" + headerTagLevel);
    }

    protected void addPageHeader(String prefix, String title) {
        RawHtmlBuilder header = currentHtmlBuilder.newChild("h1");
        header.appendText(prefix);
        header.appendText(ObjectUtils.defaultIfNull(title, ""));
    }

    @Override
    public DocumentCanvas addChild() {
        int titleIndex = (index - 1);
        if (!indexChildMap.containsKey(titleIndex)) {
            HtmlCanvas newChild = newHtmlChildCanvas(currentHtmlBuilder.newChild("div"), showTitleLevel);
            if (showTitleLevel) {
                newChild.index = 1;
                newChild.headerTagLevel = childHeaderTagLevel();
                newChild.titlePrefix = titlePrefix + titleIndex + ".";
            }
            indexChildMap.put(titleIndex, newChild);
        }
        return indexChildMap.get(titleIndex);

    }

    @Nonnull
    protected HtmlCanvas newHtmlChildCanvas(RawHtmlBuilder child, boolean showTitleLevel) {
        return new HtmlCanvas(child, showTitleLevel);
    }

    private int childHeaderTagLevel() {
        int newHeaderTagLevel = headerTagLevel + 1;
        if (newHeaderTagLevel > 4) {
            return 4;
        }
        return newHeaderTagLevel;
    }

    @Override
    public void addFormItem(FormItem formItem) {
        RawHtmlBuilder span = this.currentHtmlBuilder.newChild("span");
        span.putAttribute("style", "margin-right:25px;");
        if (!StringUtils.isEmpty(formItem.getLabel())) {
            RawHtmlBuilder labelComp = span.newChild("label");
            labelComp.putAttribute("style", "font-weight:bold;");
            labelComp.appendText(formItem.getLabel());
            labelComp.appendText(": ");
        }
        span.appendText(defaultIfNull(formItem.getValue(), ""));
    }

    @Override
    public void addLineBreak() {
        currentHtmlBuilder.newChild("br");
    }

    @Override
    public void addList(List<String> values) {
        RawHtmlBuilder ul = this.currentHtmlBuilder.newChild("ul");
        for (String v : values) {
            RawHtmlBuilder li = ul.newChild("li");
            li.appendText(v);
        }
    }

    @Override
    public TableCanvas addTable() {
        addLineBreak();
        return new HtmlTableCanvas(getRootHtmlBuilder().newChild("table"));
    }

    public void stylesheet(String css) {
        RawHtmlBuilder style = currentHtmlBuilder.newChild("style");
        style.appendTextWithoutEscape(css);
    }

    public String build() {
        return rootHtmlBuilder.build();
    }

    protected RawHtmlBuilder getcurrentHtmlBuilder() {
        return currentHtmlBuilder;
    }

    public void setcurrentHtmlBuilder(RawHtmlBuilder currentHtmlBuilder) {
        this.currentHtmlBuilder = currentHtmlBuilder;
    }

    public RawHtmlBuilder getRootHtmlBuilder() {
        return rootHtmlBuilder;
    }
}