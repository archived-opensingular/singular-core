package br.net.mirante.singular.view.template;

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
        queue(new Header("_Header", withTopAction()));
        queue(new Menu("_Menu"));
        queue(getContent("_Content"));
        queue(new Footer("_Footer"));
    }

    protected boolean withTopAction() {
        return true;
    }

    protected abstract Content getContent(String id);
    protected abstract String getPageTitleLocalKey();
}
