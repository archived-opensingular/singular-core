package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.showcase.ShowCaseTable.ShowCaseItem;
import br.net.mirante.singular.util.wicket.tab.BSTabPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class ShowCaseItemDetailPanel extends Panel {

    private IModel<ShowCaseItem> itemModel;

    public ShowCaseItemDetailPanel(String id, IModel<ShowCaseItem> itemModel) {
        super(id);
        this.itemModel = itemModel;
        add(new Label("header", itemModel.getObject().getComponentName()));
        add(buildItemCases());
    }

    private WebMarkupContainer buildItemCases() {
        WebMarkupContainer casesContainer = new WebMarkupContainer("casesContainer");
        BSTabPanel bsTabPanel = new BSTabPanel("cases");
        itemModel.getObject().getCases().forEach(c -> {
            String name = c.getSubCaseName();
            if (name == null) {
                name = c.getComponentName();
            }
            bsTabPanel.addTab(name, new ItemCasePanel(BSTabPanel.getTabPanelId(), c));
        });
        casesContainer.add(bsTabPanel);
        return casesContainer;
    }

}
