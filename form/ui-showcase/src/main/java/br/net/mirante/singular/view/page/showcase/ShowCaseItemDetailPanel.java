package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.showcase.CaseBase;
import br.net.mirante.singular.showcase.ShowCaseTable.ShowCaseItem;
import br.net.mirante.singular.util.wicket.collapsible.BSCollapsibleBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

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
        casesContainer.add(new ListView<CaseBase>("cases", itemModel.getObject().getCases()) {
            @Override
            protected void populateItem(ListItem<CaseBase> item) {
                String name = item.getModelObject().getSubCaseName();
                if (name == null) {
                    name = item.getModelObject().getComponentName();
                }
                boolean isFirst = itemModel.getObject().getCases().indexOf(item.getModelObject()) == 0;
                BSCollapsibleBorder border = new BSCollapsibleBorder("collapsible", $m.ofValue(name), isFirst, casesContainer);
                border.add(new Label("htmlDescription", item.getModelObject().getDescriptionHtml().orElse("")));

                item.add(border);
            }
        });
        return casesContainer;
    }

}
