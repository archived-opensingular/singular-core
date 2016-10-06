package org.opensingular.singular.form.wicket.renderer;

import org.opensingular.form.SInstance;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.List;


public class SingularChoiceRenderer implements IChoiceRenderer<Serializable> {

    private static final long serialVersionUID = -4297329829600866968L;

    private final IModel<? extends SInstance> model;

    public SingularChoiceRenderer(IModel<? extends SInstance> model) {
        this.model = model;
    }

    @Override
    public String getDisplayValue(Serializable val) {
        return String.valueOf(model.getObject().asAtrProvider().getDisplayFunction().apply(val));
    }

    @Override
    public String getIdValue(Serializable val, int index) {
        return String.valueOf(model.getObject().asAtrProvider().getIdFunction().apply(val));
    }

    @Override
    public Serializable getObject(String id, IModel<? extends List<? extends Serializable>> choices) {
        return choices
                .getObject()
                .stream()
                .filter(choice -> getIdValue(choice, choices.getObject().indexOf(choice)).equals(id)).findFirst().orElse(null);
    }

}
