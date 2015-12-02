package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.showcase.ShowCaseTable;
import br.net.mirante.singular.showcase.ShowCaseTable.ShowCaseGroup;
import br.net.mirante.singular.showcase.ShowCaseTable.ShowCaseItem;
import br.net.mirante.singular.util.wicket.collapsible.BSCollapsibleBorder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public abstract class ShowCaseMenuPanel extends Panel {

    private IModel<ShowCaseItem> selectedItem;

    public ShowCaseMenuPanel(String id) {
        super(id);
        add(buildMenu());
    }

    private WebMarkupContainer buildMenu() {
        WebMarkupContainer menu = new WebMarkupContainer("menu");
        menu.add(buildMenuGroups());
        return menu;
    }

    private ListView<ShowCaseGroup> buildMenuGroups() {
        return new ListView<ShowCaseGroup>("accordionMenu", new ArrayList<>(new ShowCaseTable().getGroups())) {
            @Override
            protected void populateItem(ListItem<ShowCaseGroup> grupo) {
                String name = grupo.getModelObject().getGroupName();
                BSCollapsibleBorder itemMenuBorder = new BSCollapsibleBorder("itemMenuBorder", $m.ofValue(name), true);
                itemMenuBorder.add(buildMenuItens(grupo.getModelObject()));
                grupo.add(itemMenuBorder);
            }
        };
    }

    private WebMarkupContainer buildMenuItens(ShowCaseGroup grupo) {
        WebMarkupContainer itensContainer = new WebMarkupContainer("itensContainer");
        itensContainer.add(new ListView<ShowCaseItem>("itens", new ArrayList<>(grupo.getItens())) {
            @Override
            protected void populateItem(ListItem<ShowCaseItem> item) {
                item.add(buildItemAnchor(itensContainer, item.getModel()));
                item.add($b.onConfigure(c -> {
                    super.onConfigure();
                    if (selectedItem != null && item.getModelObject().equals(selectedItem.getObject())) {
                        item.add($b.attr("class", "active"));
                    } else {
                        item.add($b.attr("class", ""));
                    }
                }));
            }
        });
        return itensContainer;
    }

    private AjaxLink buildItemAnchor(WebMarkupContainer itensContainer, IModel<ShowCaseItem> item) {
        AjaxLink itemAnchor = new AjaxLink("itemAnchor") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(itensContainer);
                selectedItem = item;
                onMenuItemClick(target, selectedItem);
            }
        };
        itemAnchor.add(new Label("itemLabel", $m.ofValue(item.getObject().getComponentName())));
        return itemAnchor;
    }

    public abstract void onMenuItemClick(AjaxRequestTarget target, IModel<ShowCaseItem> showCaseItem);

}
