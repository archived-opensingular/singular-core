package br.net.mirante.singular.form.wicket.component;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;

public abstract class SingularButton extends AjaxButton {

    public SingularButton(String id) {
        super(id);
    }

    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        super.onError(target, form);
        WicketFormProcessing.onFormError(form, Optional.of(target), getCurrentInstance());
    }

    public abstract IModel<? extends SInstance>  getCurrentInstance();
}
