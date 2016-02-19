package br.net.mirante.singular.view.template;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.StringValue;

import br.net.mirante.singular.persistence.entity.Dashboard;
import br.net.mirante.singular.service.FlowMetadataFacade;
import br.net.mirante.singular.service.UIAdminFacade;
import br.net.mirante.singular.wicket.UIAdminSession;

public abstract class Content extends Panel {

    public static final String PROCESS_DEFINITION_COD_PARAM = "pdCod";
    public static final String CUSTOM_DASHBOARD_COD_PARAM = "cdCod";

    private boolean withBreadcrumb;
    private boolean withSettingsMenu;
    private boolean withInfoLink;
    private boolean withSideBar;

    @Inject
    protected UIAdminFacade uiAdminFacade;
    
    @Inject
    protected FlowMetadataFacade flowMetadataFacade;
    
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
        queue(new FeedbackPanel("feedback"));
        add(new Label("contentTitle", getContentTitlelModel()));
        add(new Label("contentSubtitle", getContentSubtitlelModel()));
        WebMarkupContainer breadcrumb = new WebMarkupContainer("breadcrumb");
        add(breadcrumb);
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

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        StringValue customDashboardCode = getPage().getPageParameters().get(Content.CUSTOM_DASHBOARD_COD_PARAM);
        StringValue processDefinitionCode = getPage().getPageParameters().get(Content.PROCESS_DEFINITION_COD_PARAM);
        if (!customDashboardCode.isNull()) {
            Dashboard dashboard = uiAdminFacade.retrieveDashboardById(customDashboardCode.toString());
                String dashboardId = String.format("_dashboardMenu_%d", dashboard.getCod());
            response.render(OnDomReadyHeaderItem.forScript("$('#" + dashboardId + "').addClass('active');"));
        } else if (processDefinitionCode.isNull()) {
            response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemHome').addClass('active');"));
        } else {
            Pair<Long, Long> ids = uiAdminFacade.retrieveCategoryDefinitionIdsByCode(processDefinitionCode.toString());
            StringBuilder script = new StringBuilder();
            String menuId = String.format("_categoryMenu_%d", ids.getLeft());
            String itemId = String.format("_definitionMenu_%d", ids.getRight());
            script.append("$('#").append(menuId).append("').addClass('open');")
                    .append("$('#").append(menuId).append(">a>span.arrow').addClass('open');")
                    .append("$('#").append(menuId).append(">ul').show();")
                    .append("$('#").append(itemId).append("').addClass('active');");
            response.render(OnDomReadyHeaderItem.forScript(script));
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

    protected Fragment createBreadCrumbLink(String id, CharSequence href, String label) {
        return createBreadCrumbLink(id, href, label, false);
    }

    protected Fragment createActiveBreadCrumbLink(String id, CharSequence href, String label) {
        return createBreadCrumbLink(id, href, label, true);
    }
    
    protected Fragment createBreadCrumbLink(String id, CharSequence href, String label, boolean active) {
        Fragment fragment = new Fragment(id, "todoBreadcrumb", this);
        ExternalLink externalLink = new ExternalLink("breadcrumbLink", href.toString(), label);
        if(active){
            externalLink.add($b.classAppender("todo-active"));
        }
        fragment.add(externalLink);
        return fragment;
    }
    
    protected abstract IModel<?> getContentTitlelModel();

    protected abstract IModel<?> getContentSubtitlelModel();
}
