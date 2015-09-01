package br.net.mirante.singular.view.template;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.util.wicket.util.WicketUtils;

public class Menu extends Panel {

    @Inject
    private String adminWicketFilterContext;

    public Menu(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        queue(new WebMarkupContainer("dashboard").add(
                WicketUtils.$b.attr("href", adminWicketFilterContext.concat("dashboard"))));
        queue(new WebMarkupContainer("process").add(
                WicketUtils.$b.attr("href", adminWicketFilterContext.concat("process"))));
    }
}
