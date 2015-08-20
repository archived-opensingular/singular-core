package br.net.mirante.singular.view.template;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public abstract class Content extends Panel {

    private boolean withBreadcrumb;
    private boolean withSettingsMenu;
    private boolean withSideBar;

    public Content(String id) {
        this(id, true, true);
    }

    public Content(String id, boolean withSettingsMenu, boolean withSideBar) {
        this(id, withSettingsMenu, withSideBar, false);
    }

    public Content(String id, boolean withSettingsMenu, boolean withSideBar, boolean withBreadcrumb) {
        super(id);
        this.withBreadcrumb = withBreadcrumb;
        this.withSettingsMenu = withSettingsMenu;
        this.withSideBar = withSideBar;
    }

    public Content addSideBar() {
        this.withSideBar = true;
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("contentTitle", new ResourceModel(getContentTitlelKey())));
        add(new Label("contentSubtitle", new ResourceModel(getContentSubtitlelKey())));
        WebMarkupContainer breadcrumb = new WebMarkupContainer("breadcrumb");
        add(breadcrumb);
        if (!withBreadcrumb) {
            breadcrumb.add(new AttributeAppender("class", "hide", " "));
        }
        if (withSettingsMenu) {
            add(new SettingsMenu("_SettingsMenu"));
        } else {
            add(new WebMarkupContainer("_SettingsMenu"));
        }
        if (withSideBar) {
            add(new SideBar("_SideBar"));
        } else {
            add(new WebMarkupContainer("_SideBar"));
        }
    }

    protected abstract String getContentTitlelKey();
    protected abstract String getContentSubtitlelKey();
}
