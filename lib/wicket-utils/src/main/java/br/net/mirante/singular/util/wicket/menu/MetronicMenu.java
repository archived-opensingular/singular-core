package br.net.mirante.singular.util.wicket.menu;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.ArrayList;
import java.util.List;

public class MetronicMenu extends Panel {

    public List<AbstractMenuItem> itens = new ArrayList<>();

    public MetronicMenu(String id) {
        super(id);
    }

    public void addGroup(MetronicMenuGroup group) {
        itens.add(group);
    }

    public void addItem(MetronicMenuItem item) {
        itens.add(item);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new ListView<AbstractMenuItem>("itens", itens) {
            @Override
            protected void populateItem(ListItem<AbstractMenuItem> item) {
                item.add(item.getModelObject());
            }
        });
        mapMenuIds();
        configureOpenedItem();
    }

    private void mapMenuIds() {
        itens.forEach(i -> i.mapMenuId(String.valueOf(itens.indexOf(i))));
    }

    private void configureOpenedItem() {
        String openedItemId = (String) getSession().getAttribute("*_-!menu-id!-_*");
        if (openedItemId != null) {
            itens.forEach(i -> i.configureOpenedItem(openedItemId));
            getSession().removeAttribute("*_-!menu-id!-_*");
        }
    }
}
