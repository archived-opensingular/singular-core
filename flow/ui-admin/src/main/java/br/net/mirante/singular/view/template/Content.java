package br.net.mirante.singular.view.template;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public abstract class Content extends Panel {

    private boolean withSettingsMenu;

    public Content(String id) {
        super(id);
        this.withSettingsMenu = true;
    }

    public Content(String id, boolean withSettingsMenu) {
        super(id);
        this.withSettingsMenu = withSettingsMenu;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("contentTitle", new ResourceModel(getContentTitlelKey())));
        add(new Label("contentSubtitle", new ResourceModel(getContentSubtitlelKey())));
        if (withSettingsMenu) {
            add(new SettingsMenu("_SettingsMenu"));
        } else {
            add(new WebMarkupContainer("_SettingsMenu"));
        }
    }

    protected abstract String getContentTitlelKey();
    protected abstract String getContentSubtitlelKey();
}
