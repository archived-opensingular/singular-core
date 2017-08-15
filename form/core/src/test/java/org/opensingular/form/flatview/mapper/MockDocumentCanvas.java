package org.opensingular.form.flatview.mapper;

import org.junit.Assert;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
import org.opensingular.lib.commons.canvas.FormItem;

import java.util.ArrayList;
import java.util.List;

public class MockDocumentCanvas implements DocumentCanvas {

    private List<String> titles = new ArrayList<>();
    private List<MockDocumentCanvas> children = new ArrayList<>();


    @Override
    public void addTitle(String title) {
        titles.add(title);
    }

    @Override
    public DocumentCanvas newChild() {
        MockDocumentCanvas mockDocumentCanvas = new MockDocumentCanvas();
        children.add(mockDocumentCanvas);
        return mockDocumentCanvas;
    }

    @Override
    public void label(FormItem formItem) {

    }

    @Override
    public void breakLine() {

    }

    @Override
    public void list(List<String> values) {

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
}
