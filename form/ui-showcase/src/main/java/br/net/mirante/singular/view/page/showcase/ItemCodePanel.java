package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.util.wicket.tab.BSTabPanel;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class ItemCodePanel extends Panel {

    public ItemCodePanel(IModel<String> code) {
        super(BSTabPanel.getTabPanelId());
        add(new Label("code", code));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        add($b.onReadyScript(this::getSyntaxHighlighterScript));
    }

    private String getSyntaxHighlighterScript() {
        return "SyntaxHighlighter.all()";
    }
}
