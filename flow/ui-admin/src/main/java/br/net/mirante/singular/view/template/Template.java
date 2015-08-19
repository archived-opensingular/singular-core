package br.net.mirante.singular.view.template;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;

public class Template extends WebPage {

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("pageTitle", new ResourceModel("label.page.title.local")));
    }
}
