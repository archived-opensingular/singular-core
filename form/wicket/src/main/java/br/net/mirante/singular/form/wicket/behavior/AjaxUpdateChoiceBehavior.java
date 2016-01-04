package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.IAjaxUpdateListener;

public final class AjaxUpdateChoiceBehavior extends AjaxFormChoiceComponentUpdatingBehavior {
    private final IAjaxUpdateListener listener;
    private final IModel<MInstancia>  model;
    public AjaxUpdateChoiceBehavior(IModel<MInstancia> model, IAjaxUpdateListener listener) {
        this.listener = listener;
        this.model = model;
    }
    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        listener.onUpdate(this.getComponent(), target, model);
    }
}