package br.net.mirante.singular.view.template;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

public class Header extends Panel {

    private boolean withTopAction;
    private boolean withSideBar;

    public Header(String id) {
        super(id);
        this.withTopAction = true;
    }

    public Header(String id, boolean withTopAction, boolean withSideBar) {
        super(id);
        this.withTopAction = withTopAction;
        this.withSideBar = withSideBar;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (withTopAction) {
            add(new TopAction("_TopAction"));
        } else {
            add(new WebMarkupContainer("_TopAction"));
        }
        add(new TopMenu("_TopMenu", withSideBar));
    }
}
