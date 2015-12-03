package br.net.mirante.singular.util.wicket.menu;

import org.apache.wicket.markup.html.panel.Panel;

public abstract class AbstractMenuItem extends Panel {

    protected String menuItemId;
    protected String title;
    protected String icon;

    public AbstractMenuItem(String id) {
        super(id);
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    public void mapMenuId(String menuItemId){
        this.menuItemId = menuItemId;
    }

    protected abstract void configureOpenedItem(String openedItemId);
}
