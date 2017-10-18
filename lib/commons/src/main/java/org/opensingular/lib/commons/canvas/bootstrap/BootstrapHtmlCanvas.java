/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.commons.canvas.bootstrap;

import org.apache.commons.lang3.ObjectUtils;
import org.opensingular.lib.commons.canvas.FormItem;
import org.opensingular.lib.commons.canvas.HtmlCanvas;
import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;
import org.opensingular.lib.commons.canvas.table.HtmlTableCanvas;
import org.opensingular.lib.commons.canvas.table.TableCanvas;

import javax.annotation.Nonnull;
import java.util.List;

public class BootstrapHtmlCanvas extends HtmlCanvas {

    public BootstrapHtmlCanvas(boolean showTitleLevel) {
        super(showTitleLevel);
        getRootHtmlBuilder()
                .newChild("link")
                .putAttribute("rel", "stylesheet")
                .putAttribute("href", "http://localhost:8080/wkhtmltopdf-ws/resources/bootstrap/3.3.7/bootstrap.min.css")
                .putAttribute("crossorigin", "anonymous");
        setcurrentHtmlBuilder(getRootHtmlBuilder().newChild("div").putAttribute("class", "container"));

    }

    public BootstrapHtmlCanvas(RawHtmlBuilder rawHtmlBuilder, boolean showTitleLevel) {
        super(rawHtmlBuilder, showTitleLevel);
    }

    @Override
    protected @Nonnull HtmlCanvas newHtmlChildCanvas(RawHtmlBuilder child, boolean showTitleLevel) {
        return new BootstrapHtmlCanvas(child, showTitleLevel);
    }


    @Override
    protected RawHtmlBuilder createSubheaderTag(Integer headerTagLevel) {
        return getcurrentHtmlBuilder().newChild("div").putAttribute("class", "col-md-12").newChild("h" + headerTagLevel);
    }

    @Override
    protected void addPageHeader(String prefix, String title) {
        getcurrentHtmlBuilder()
                .newChild("div")
                .putAttribute("class", "page-header text-center")
                .newChild("h1")
                .appendText(ObjectUtils.defaultIfNull(title, ""));
    }

    @Override
    public void addFormItem(FormItem formItem) {
        if (formItem.isValueAndLabelNull()) {
            return;
        }
        RawHtmlBuilder item = getcurrentHtmlBuilder().newChild("div");
        item.putAttribute("style", "word-wrap:break-word");
        if (formItem.getCols() != null) {
            item.appendAttribute("class", "col-md-" + formItem.getCols(), " ");
        }
        if (formItem.getLabel() != null) {
            item.newChild("strong").appendText(formItem.getLabel()).appendText(": ");
        }
        if (formItem.getValue() != null) {
            item.appendText(formItem.getValue());
        }
    }

    @Override
    public void addList(List<String> values) {
        RawHtmlBuilder ul = getcurrentHtmlBuilder()
                .newChild("div").putAttribute("class", "col-md-12")
                .newChild("ul").putAttribute("class", "list-unstyled");
        for (String v : values) {
            RawHtmlBuilder li = ul.newChild("li");
            li.appendText(v);
        }
    }

    @Override
    public void addLineBreak() {
        getcurrentHtmlBuilder()
                .newChild("div").putAttribute("class", "col-md-12");
    }

    @Override
    public TableCanvas addTable() {
        return new HtmlTableCanvas(getRootHtmlBuilder()
                .newChild("div").putAttribute("class", "col-md-12")
                .newChild("table").putAttribute("class", "table"));
    }
}