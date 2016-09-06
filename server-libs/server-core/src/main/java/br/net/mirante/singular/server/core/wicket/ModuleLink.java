package br.net.mirante.singular.server.core.wicket;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class ModuleLink extends Link<Object> {

    private String url;

    public ModuleLink(String id, IModel<?> model, String url) {
        super(id);
        setBody(model);
        this.url = url;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        add($b.attr("target", "_blank"));
    }

    @Override
    public void onClick() {}

    @Override
    protected boolean getStatelessHint() {
        return true;
    }

    @Override
    protected CharSequence getURL() {
        return url;
    }
}
