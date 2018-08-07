package org.opensingular.form.wicket.panel.quicknav;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.resource.JQueryPluginResourceReference;

public class QuickNavPanel extends Panel {

    private RefreshingView<String> tabRepeater;

    public QuickNavPanel(String id, RefreshingView<String> tabRepeater) {
        super(id);
        this.tabRepeater = tabRepeater;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem
                .forReference(new JQueryPluginResourceReference(QuickNavPanel.class, "QuickNav.js")));
        response.render(OnDomReadyHeaderItem.forScript("QuickNav.init();"));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer tabMenu = new WebMarkupContainer("tab-menu");
        tabMenu.add(tabRepeater);
        add(tabMenu);
    }
}
