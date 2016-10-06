package org.opensingular.singular.server.core.wicket.view.home;

import org.opensingular.singular.server.commons.wicket.view.template.Content;
import org.apache.wicket.model.IModel;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

public class HomeContent extends Content {

    public HomeContent(String id) {
        super(id);
    }

    public HomeContent(String id, boolean withInfoLink, boolean withBreadcrumb) {
        super(id, withInfoLink, withBreadcrumb);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.ofValue("Página inicial");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("Página inicial");
    }
}
