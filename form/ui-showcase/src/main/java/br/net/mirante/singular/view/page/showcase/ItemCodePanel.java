package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.util.wicket.tab.BSTabPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class ItemCodePanel extends Panel {

    public ItemCodePanel(IModel<String> code) {
        super(BSTabPanel.getTabPanelId());
        add(new Label("code", code));
    }
}
