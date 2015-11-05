package br.net.mirante.singular.form.wicket.validator;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.validation.IInstanceValidatable;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;

final class InstanceValidatableAdapter<I extends MInstancia> implements IInstanceValidatable<I> {
    private final org.apache.wicket.Component                  component;
    private final org.apache.wicket.validation.IValidator<I>   wicketValidator;
    private final org.apache.wicket.validation.IValidatable<I> wicketValidatable;
    private final IMInstanciaAwareModel<?>                     model;
    private ValidationErrorLevel                               defaultLevel = ValidationErrorLevel.ERROR;
    public InstanceValidatableAdapter(
        org.apache.wicket.Component component,
        org.apache.wicket.validation.IValidator<I> wValidator,
        org.apache.wicket.validation.IValidatable<I> wValidatable,
        IMInstanciaAwareModel<I> model) {
        this.component = component;
        this.wicketValidator = wValidator;
        this.wicketValidatable = wValidatable;
        this.model = model;
    }
    public void setDefaultLevel(ValidationErrorLevel defaultLevel) {
        this.defaultLevel = defaultLevel;
    }
    @Override
    @SuppressWarnings("unchecked")
    public I getInstance() {
        return (I) model.getMInstancia();
    }
    @Override
    public void error(IValidationError singularError) {
        errorInternal(defaultLevel, singularError.getMessage());
    }
    @Override
    public IValidationError error(String msg) {
        return errorInternal(defaultLevel, msg);
    }
    @Override
    public void error(ValidationErrorLevel level, IValidationError singularError) {
        errorInternal(level, singularError.getMessage());
    }
    @Override
    public IValidationError error(ValidationErrorLevel level, String msg) {
        return errorInternal(level, msg);
    }
    private IValidationError errorInternal(ValidationErrorLevel level, String msg) {
        org.apache.wicket.validation.ValidationError wicketError = new org.apache.wicket.validation.ValidationError(wicketValidator);
        wicketError.setMessage(msg);
        if (ValidationErrorLevel.ERROR == level) {
            wicketValidatable.error(wicketError);
        } else {
            component.warn(msg);
        }
        return new ValidationErrorAdapter(model.getMInstancia(), level, wicketError);
    }
}