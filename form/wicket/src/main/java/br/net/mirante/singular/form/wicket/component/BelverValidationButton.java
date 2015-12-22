package br.net.mirante.singular.form.wicket.component;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import java.util.Optional;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public abstract class BelverValidationButton extends AjaxButton {

    private final IModel<? extends MInstancia>  currentInstance;

    public BelverValidationButton(String id, IModel<? extends MInstancia>  currentInstance) {
        super(id);
        this.currentInstance = currentInstance;
    }

    protected abstract void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends MInstancia>  instanceModel);
    protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends MInstancia> instanceModel) {}

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        super.onSubmit(target, form);
        if (WicketFormProcessing.onFormSubmit(form, Optional.of(target), currentInstance.getObject(), true)) {
            onValidationSuccess(target, form, currentInstance);
        } else {
            onValidationError(target, form, currentInstance);
        }
        target.add(form);
    }

    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        super.onError(target, form);
        WicketFormProcessing.onFormError(form, Optional.of(target), currentInstance.getObject());
    }

    public IModel<? extends MInstancia>  getCurrentInstance() {
        return currentInstance;
    }
}
