package br.net.mirante.singular.view.template;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;

public abstract class Template extends WebPage {

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("pageTitle", new ResourceModel(getPageTitleLocalKey())));
        add(new WebMarkupContainer("pageBody"));
        queue(new Header("_Header", withTopAction(), withSideBar()));
        queue(new Menu("_Menu"));
        queue(configureContent("_Content"));
        queue(new Footer("_Footer"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (withSideBar()) {
            addQuickSidebar(response);
        }
    }

    protected boolean withTopAction() {
        return true;
    }

    protected boolean withSideBar() {
        return true;
    }

    protected String getPageTitleLocalKey() {
        return "label.page.title.process";
    }

    protected abstract Content getContent(String id);

    private Content configureContent(String contentId) {
        if (withSideBar()) {
            return getContent(contentId).addSideBar();
        } else {
            return getContent(contentId);
        }
    }

    private void addQuickSidebar(IHeaderResponse response) {
        response.render(JavaScriptReferenceHeaderItem.forUrl("/resources/admin/layout/scripts/quick-sidebar.js"));
        StringBuilder script = new StringBuilder();
        script.append("jQuery(document).ready(function () {\n")
                .append("    QuickSidebar.init(); // init quick sidebar\n")
                .append("});");
        response.render(OnDomReadyHeaderItem.forScript(script));
    }
}
