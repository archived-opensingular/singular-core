package org.opensingular.form.flatview.mapper;

import org.junit.Assert;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
import org.opensingular.lib.commons.canvas.FormItem;
import org.opensingular.lib.commons.canvas.table.TableCanvas;

import java.util.ArrayList;
import java.util.List;

public class MockDocumentCanvas implements DocumentCanvas {

    private List<String> titles = new ArrayList<>();
    private List<MockDocumentCanvas> children = new ArrayList<>();
    private List<FormItem> formItens = new ArrayList<>();

    @Override
    public void addSubtitle(String title) {
        titles.add(title);
    }

    @Override
    public DocumentCanvas addChild() {
        MockDocumentCanvas mockDocumentCanvas = new MockDocumentCanvas();
        children.add(mockDocumentCanvas);
        return mockDocumentCanvas;
    }

    @Override
    public void addFormItem(FormItem formItem) {
        formItens.add(formItem);
    }

    @Override
    public void addLineBreak() {

    }

    @Override
    public void addList(List<String> values) {

    }

    @Override
    public TableCanvas addTable() {
        return null;
    }

    public MockDocumentCanvas assertTitleCount(int count) {
        Assert.assertEquals(count, titles.size());
        return this;
    }

    public MockDocumentCanvas assertTitle(String expectedTitle) {
        Assert.assertTrue(titles.contains(expectedTitle));
        return this;
    }

    public MockDocumentCanvas assertChildCount(int i) {
        Assert.assertEquals(i, children.size());
        return this;
    }

    public void assertLabelValue(String value) {
        Assert.assertTrue(formItens.stream().map(FormItem::getValue).anyMatch(value::equalsIgnoreCase));
    }
}
