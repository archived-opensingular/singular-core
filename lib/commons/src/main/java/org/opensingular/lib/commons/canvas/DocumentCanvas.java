package org.opensingular.lib.commons.canvas;

import org.opensingular.lib.commons.canvas.table.TableCanvas;

import java.util.List;

public interface DocumentCanvas {

    void addSubtitle(String title);

    DocumentCanvas addChild();

    void addFormItem(FormItem formItem);

    void addLineBreak();

    void addList(List<String> values);

    TableCanvas addTable();

}