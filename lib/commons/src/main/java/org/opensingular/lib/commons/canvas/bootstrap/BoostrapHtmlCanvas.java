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
                .putAttribute("href", "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css")
                .putAttribute("integrity", "sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u")
                .putAttribute("crossorigin", "anonymous");
        getRootHtmlBuilder()
                .newChild("script")
                .putAttribute("src", "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js")
                .putAttribute("integrity", "sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa")
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
                .putAttribute("class", "page-header")
                .newChild("h1")
                .appendText(ObjectUtils.defaultIfNull(title, ""));
    }

    @Override
    public void addFormItem(FormItem formItem) {
        if (formItem.isValueAndLabelNull()) {
            return;
        }
        RawHtmlBuilder column = getcurrentHtmlBuilder().newChild("div");
        if (formItem.getCols() != null) {
            column.putAttribute("class", "col-md-" + formItem.getCols());
        }
        if (formItem.getLabel() != null) {
            column.appendText(formItem.getLabel()).appendText(": ");
        }
        if (formItem.getValue() != null) {
            column.appendText(formItem.getValue());
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