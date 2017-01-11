package org.opensingular.lib.wicket.util.datatable.column;

import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class BSFolder<T> extends Folder<T> {

    public BSFolder(String id, AbstractTree tree, IModel model) {
        super(id, tree, model);

        Label icon = new Label("icon");
        icon.add(WicketUtils.$b.classAppender(getIconStyleClass()));
        add(icon);
    }

    protected String getIconStyleClass() {
        return "";
    }

}
