package br.net.mirante.singular.showcase.component.validation;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.wicket.util.WicketFormProcessing;
import br.net.mirante.singular.form.wicket.util.WicketFormUtils;
import java.util.Optional;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public class PartialValidationButton extends AjaxButton {

    private final IModel<? extends SInstance2>  currentInstance;

    public PartialValidationButton(String id, IModel<? extends SInstance2> currentInstance) {
        super(id);
        this.currentInstance = currentInstance;
    }

    protected void addValidationErrors(Form<?> form, SInstance2 instance) {
        //@destacar:bloco
        final SInstance2 obrigatorio1 = ((SIComposite) instance).getCampo("obrigatorio_1");
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
        WicketFormProcessing.onFormError(form, Optional.of(target), currentInstance);
    }
}
