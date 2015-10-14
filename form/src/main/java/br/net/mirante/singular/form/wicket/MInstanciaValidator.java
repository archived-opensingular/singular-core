package br.net.mirante.singular.form.wicket;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.ValidationError;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.validation.IValidatable;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.wicket.model.instancia.IMInstanciaAwareModel;

public class MInstanciaValidator<T> extends Behavior implements org.apache.wicket.validation.IValidator<T> {

    @Override
    public void validate(org.apache.wicket.validation.IValidatable<T> wicketValidatable) {
        if (wicketValidatable.getValue() == null)
            return;
        IModel<T> model = wicketValidatable.getModel();
        IMInstanciaAwareModel<T> instAwareModel = (IMInstanciaAwareModel<T>) model;
        MInstancia instancia = instAwareModel.getMInstancia();
        MTipo<?> tipo = instancia.getMTipo();
        tipo.validar(new ValidatableAdapter<T>(this, wicketValidatable, instAwareModel));
    }

    private static final class ValidatableAdapter<V> implements IValidatable<V> {
        private final org.apache.wicket.validation.IValidator<V>   wicketValidator;
        private final org.apache.wicket.validation.IValidatable<V> wicketValidatable;
        private final IMInstanciaAwareModel<V>                     model;
        public ValidatableAdapter(
            org.apache.wicket.validation.IValidator<V> wValidator,
            org.apache.wicket.validation.IValidatable<V> wValidatable,
            IMInstanciaAwareModel<V> model)
        {
            this.wicketValidator = wValidator;
            this.wicketValidatable = wValidatable;
            this.model = model;
        }
        @Override
        public V getValue() {
            return wicketValidatable.getValue();
        }
        @Override
        public MInstancia getMInstancia() {
            return model.getMInstancia();
        }
        @Override
        public void error(IValidationError singularError) {
            errorInternal(singularError.getMessage());
        }
        @Override
        public IValidationError error(String msg) {
            return errorInternal(msg);
        }
        private IValidationError errorInternal(String msg) {
            org.apache.wicket.validation.ValidationError wicketError = new org.apache.wicket.validation.ValidationError(wicketValidator);
            wicketError.setMessage(msg);
            wicketValidatable.error(wicketError);
            return new ValidationErrorAdapter(wicketError);
        }
        @Override
        public boolean isValid() {
            return wicketValidatable.isValid();
        }
    }

    private static final class ValidationErrorAdapter implements IValidationError {
        private org.apache.wicket.validation.ValidationError wicketError;
        public ValidationErrorAdapter(ValidationError wicketError) {
            this.wicketError = wicketError;
        }
        @Override
        public String getMessage() {
            return wicketError.getMessage();
        }
    }

}
