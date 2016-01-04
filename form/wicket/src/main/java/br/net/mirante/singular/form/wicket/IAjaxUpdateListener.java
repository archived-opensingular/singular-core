package br.net.mirante.singular.form.wicket;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;

public interface IAjaxUpdateListener extends Serializable {

    void onUpdate(Component source, AjaxRequestTarget target, IModel<? extends MInstancia> instanceModel);
    void onError(Component source, AjaxRequestTarget target, IModel<? extends MInstancia> instanceModel);
}
