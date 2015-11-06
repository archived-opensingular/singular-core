package br.net.mirante.singular.view.template;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.wicket.UIAdminWicketFilterContext;

public class Menu extends Panel {

    @Inject
    private UIAdminWicketFilterContext uiAdminWicketFilterContext;

    public Menu(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        queue(new WebMarkupContainer("home")
                .add(WicketUtils.$b.attr("href", uiAdminWicketFilterContext.getRelativeContext())));
        queue(new WebMarkupContainer("crud")
                .add(WicketUtils.$b.attr("href", uiAdminWicketFilterContext.getRelativeContext()
                        .concat("form/crud"))));
        queue(new WebMarkupContainer("showcase")
                .add(WicketUtils.$b.attr("href", uiAdminWicketFilterContext.getRelativeContext()
                        .concat("showcase"))));
    }
}
