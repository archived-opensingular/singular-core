package br.net.mirante.singular.showcase.validation;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public class PartialValidationButton extends AjaxButton {

    private final IModel<MInstancia> currentInstance;

    public PartialValidationButton(String id, IModel<MInstancia> currentInstance) {
        super(id);
        this.currentInstance = currentInstance;
    }

    protected void addValidationErrors(Form<?> form, MInstancia instance) {
        //@destacar:bloco
        final MInstancia obrigatorio1 = ((MIComposto) instance).getCampo("obrigatorio_1");
        InstanceValidationContext validationContext = new InstanceValidationContext(obrigatorio1);
        //@destacar:fim
        validationContext.validateSingle();
        WicketFormUtils.associateErrorsToComponents(validationContext, form);
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
        WicketFormProcessing.onFormError(form, Optional.of(target), currentInstance.getObject());
    }
}
