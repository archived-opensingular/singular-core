package br.net.mirante.singular.form.wicket.component;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public class BelverValidationButton extends AjaxButton {

    private final IModel<MInstancia> currentInstance;

    public BelverValidationButton(String id, IModel<MInstancia> currentInstance) {
        super(id);
        this.currentInstance = currentInstance;
    }

    protected boolean addValidationErrors(Form<?> form, MInstancia trueInstance) {
        InstanceValidationContext validationContext = new InstanceValidationContext(trueInstance);
        validationContext.validateAll();
        WicketFormUtils.associateErrorsToComponents(validationContext, form);
        return !validationContext.hasErrorsAboveLevel(ValidationErrorLevel.WARNING);
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        super.onSubmit(target, form);
        addValidationErrors(form, currentInstance.getObject());
        target.add(form);
    }

    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        super.onError(target, form);
        target.add(form);
    }

    public IModel<MInstancia> getCurrentInstance() {
        return currentInstance;
    }
}
