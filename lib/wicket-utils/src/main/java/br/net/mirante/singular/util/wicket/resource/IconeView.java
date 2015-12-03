package br.net.mirante.singular.util.wicket.resource;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

public class IconeView extends WebMarkupContainer {

    public IconeView(String id) {
        this(id, $m.ofValue());
    }
    public IconeView(String id, IModel<Icone> model) {
        super(id, model);
        add($b.classAppender($m.get(() -> {
            if (getModelObject() != null) {
                return getModelObject().getCssClass() + " fa-lg";
            }
            return false;
        }), $m.isNullOrEmpty(getModel())));
    }

    public IconeView setIcone(Icone icone) {
        return setModelObject(icone);
    }
    public IconeView setModelObject(Icone icone) {
        setDefaultModelObject(icone);
        return this;
    }
    public Icone getIcone() {
        return getModelObject();
    }
    public Icone getModelObject() {
        return (Icone) getDefaultModelObject();
    }
    @SuppressWarnings("unchecked")
    public IModel<Icone> getModel() {
        return (IModel<Icone>) getDefaultModel();
    }
}
