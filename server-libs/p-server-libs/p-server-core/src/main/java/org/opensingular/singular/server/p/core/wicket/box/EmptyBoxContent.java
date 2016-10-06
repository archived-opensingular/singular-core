package org.opensingular.singular.server.p.core.wicket.box;

import org.opensingular.singular.server.commons.wicket.view.template.Content;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Content utilizado para quando não for possivel encontrar um ItemBoxDTO para ser renderizado na BoxPage
 */
public class EmptyBoxContent extends Content {

    public EmptyBoxContent(String id) {
        super(id);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return Model.of("");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return Model.of("");
    }

}