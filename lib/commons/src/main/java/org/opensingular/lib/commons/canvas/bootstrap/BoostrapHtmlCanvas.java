package org.opensingular.lib.commons.canvas.bootstrap;

import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.opensingular.lib.commons.canvas.FormItem;
import org.opensingular.lib.commons.canvas.HtmlCanvas;
import org.opensingular.lib.commons.canvas.builder.RawHtmlBuilder;
import org.opensingular.lib.commons.canvas.table.HtmlTableCanvas;
import org.opensingular.lib.commons.canvas.table.TableCanvas;

import java.util.List;

public class BoostrapHtmlCanvas extends HtmlCanvas {

    public BoostrapHtmlCanvas(boolean showTitleLevel) {
        super(showTitleLevel);
        getRootHtmlBuilder()
                .newChild("link")
                .putAttribute("rel", "stylesheet")
                .putAttribute("href", "http://localhost:8080/wkhtmltopdf-ws/resources/bootstrap/3.3.7/bootstrap.min.css")
                .putAttribute("crossorigin", "anonymous");
        getRootHtmlBuilder()
                .newChild("script")
                .putAttribute("src", "http://localhost:8080/wkhtmltopdf-ws/resources/bootstrap/3.3.7/bootstrap.min.js")
                .putAttribute("crossorigin", "anonymous");
        setcurrentHtmlBuilder(getRootHtmlBuilder().newChild("div").putAttribute("class", "container"));

    }

    public BoostrapHtmlCanvas(RawHtmlBuilder rawHtmlBuilder, boolean showTitleLevel) {
        super(rawHtmlBuilder, showTitleLevel);
    }

    @Override
    protected @NotNull HtmlCanvas newHtmlChildCanvas(RawHtmlBuilder child, boolean showTitleLevel) {
        return new BoostrapHtmlCanvas(child, showTitleLevel);
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