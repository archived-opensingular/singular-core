package org.opensingular.lib.commons.canvas;

import org.opensingular.lib.commons.canvas.table.TableCanvas;

import java.util.List;

public class EmptyDocumentCanvas implements DocumentCanvas {
    @Override
    public void addSubtitle(String title) {

    }

    @Override
    public DocumentCanvas addChild() {
        return null;
    }

    @Override
    public void addFormItem(FormItem formItem) {

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
}