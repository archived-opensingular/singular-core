package br.net.mirante.singular.view.template;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

public class TopMenu extends Panel {

    private boolean withSideBar;

    public TopMenu(String id, boolean withSideBar) {
        super(id);
        this.withSideBar = withSideBar;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new WebMarkupContainer("sideBarToggle").setVisible(withSideBar));
    }
}
