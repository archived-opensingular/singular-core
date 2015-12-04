package br.net.mirante.singular.util.wicket.menu;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.ArrayList;
import java.util.List;

import static br.net.mirante.singular.util.wicket.menu.AbstractMenuItem.ATTR_ACTIVE_ITEM;

public class MetronicMenu extends Panel {

    public List<AbstractMenuItem> itens = new ArrayList<>();
    public String activeItemId;

    public MetronicMenu(String id) {
        super(id);
    }

    public void addItem(AbstractMenuItem item) {
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
        configureActiveItem();
    }

    private void mapMenuIds() {
        itens.forEach(i -> i.mapItemId(String.valueOf(itens.indexOf(i))));
    }

    private void configureActiveItem() {
        activeItemId = (String) getSession().getAttribute(ATTR_ACTIVE_ITEM);
        if (activeItemId != null) {
            itens.forEach(i -> i.configureActiveItem(activeItemId));
            getSession().removeAttribute(ATTR_ACTIVE_ITEM);
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        if(getSession().getAttribute(ATTR_ACTIVE_ITEM) == null){
            getWebSession().setAttribute(ATTR_ACTIVE_ITEM, activeItemId);
        }
    }

}
