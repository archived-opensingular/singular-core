package br.net.mirante.singular.view.page.showcase;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class ItemCodePanel extends Panel {

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("SyntaxHighlighter.all();"));
    }

    public ItemCodePanel(String id, IModel<String> code) {
        super(id);
        add(new Label("code", code));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

}
