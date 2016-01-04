package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.IAjaxUpdateListener;

public class AjaxUpdateInputBehavior extends AjaxFormComponentUpdatingBehavior {
    private final IAjaxUpdateListener listener;
    private final IModel<MInstancia>  model;
    public AjaxUpdateInputBehavior(String event, IModel<MInstancia> model, IAjaxUpdateListener listener) {
        super(event);
        this.listener = listener;
        this.model = model;
    }
    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        listener.onUpdate(this.getComponent(), target, model);
    }
}