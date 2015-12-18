package br.net.mirante.singular.view.template;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.wicket.UIAdminSession;

public abstract class Content extends Panel {


    private boolean withBreadcrumb;
    private boolean withSettingsMenu;
    private boolean withInfoLink;
    private boolean withSideBar;

    public Content(String id) {
        this(id, false, false, false, false);
    }

    public Content(String id, boolean withSettingsMenu, boolean withSideBar) {
        this(id, withSettingsMenu, withSideBar, false, false);
    }

    public Content(String id, boolean withSettingsMenu, boolean withSideBar,
            boolean withInfoLink, boolean withBreadcrumb) {
        super(id);
        this.withBreadcrumb = withBreadcrumb;
        this.withSettingsMenu = withSettingsMenu;
        this.withInfoLink = withInfoLink;
        this.withSideBar = withSideBar;
    }

    public Content addSideBar() {
        this.withSideBar = true;
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("contentTitle", getContentTitlelModel()));
        add(new Label("contentSubtitle", getContentSubtitlelModel()));
        WebMarkupContainer breadcrumb = new WebMarkupContainer("breadcrumb");
        add(breadcrumb);
        breadcrumb.add(new WebMarkupContainer("breadcrumbDashboard").add(
                $b.attr("href", "dashboard")));
        breadcrumb.add(getBreadcrumbLinks("_BreadcrumbLinks"));
        if (!withBreadcrumb) {
            breadcrumb.add(new AttributeAppender("class", "hide", " "));
        }
        add(new SettingsMenu("_SettingsMenu").setVisible(withSettingsMenu));
        add(new SideBar("_SideBar").setVisible(withSideBar));
        WebMarkupContainer infoLink = new WebMarkupContainer("_Info");
        add(infoLink.setVisible(withInfoLink));
        if (withInfoLink) {
            infoLink.add(getInfoLink("_InfoLink"));
        }
    }

    protected String getUserId() {
        return UIAdminSession.get().getUserId();
    }
    
    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        return new WebMarkupContainer(id);
    }

    protected WebMarkupContainer getInfoLink(String id) {
        return new WebMarkupContainer(id);
    }

    protected abstract IModel<?> getContentTitlelModel();

    protected abstract IModel<?> getContentSubtitlelModel();
}
