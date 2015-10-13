package br.net.mirante.singular.form.wicket;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.wicket.model.instancia.IMInstanciaAwareModel;

public class MInstanciaValidator<T> extends Behavior implements IValidator<T> {

    @Override
    public void validate(IValidatable<T> validatable) {
        IModel<T> model = validatable.getModel();
        if (model instanceof IMInstanciaAwareModel<?>) {
            IMInstanciaAwareModel<T> instAwareModel = (IMInstanciaAwareModel<T>) model;
            MInstancia instancia = instAwareModel.getMInstancia();
            MTipo<?> tipo = instancia.getMTipo();
            tipo.validar(new ValidatableAdapter<T>(this, validatable, instAwareModel));
        }
    }

    private static final class ValidatableAdapter<V> implements br.net.mirante.singular.form.validation.IValidatable<V> {
        private final IValidator<V>            validator;
        private final IValidatable<V>          validatable;
        private final IMInstanciaAwareModel<V> model;
        public ValidatableAdapter(IValidator<V> validator, IValidatable<V> validatable, IMInstanciaAwareModel<V> model) {
            this.validator = validator;
            this.validatable = validatable;
            this.model = model;
        }
        @Override
        public V getValue() {
            return model.getObject();
        }
        @Override
        public MInstancia getMInstancia() {
            return model.getMInstancia();
        }
        @Override
        public void error(br.net.mirante.singular.form.validation.IValidationError singularError) {
            ValidationError validationError = new ValidationError(validator);
            validationError.setMessage(singularError.getMessage());
            validatable.error(validationError);
        }
        @Override
        public boolean isValid() {
            return validatable.isValid();
        }
    }
}
