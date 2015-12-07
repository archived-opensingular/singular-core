package br.net.mirante.singular.util.wicket.menu;

import br.net.mirante.singular.util.wicket.resource.Icone;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class AbstractMenuItem extends Panel {

    public static String ATTR_ACTIVE_ITEM = "*_-!item-active-id!-_*";

    protected String itemId;
    protected String title;
    protected Icone icon;

    public AbstractMenuItem(String id) {
        super(id);
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void mapItemId(String itemId) {
        this.itemId = itemId;
    }

    protected abstract void configureActiveItem(String activeItemId);
}
