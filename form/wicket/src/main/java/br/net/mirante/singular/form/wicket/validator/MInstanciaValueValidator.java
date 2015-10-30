package br.net.mirante.singular.form.wicket.validator;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;

public class MInstanciaValueValidator<T> extends Behavior implements org.apache.wicket.validation.IValidator<T> {

    private Component component;

    @Override
    public void bind(Component component) {
        this.component = component;
    }
    
    @Override
    public void validate(org.apache.wicket.validation.IValidatable<T> wicketValidatable) {
        if (wicketValidatable.getValue() == null)
            return;
        IModel<T> model = wicketValidatable.getModel();
        IMInstanciaAwareModel<T> instAwareModel = (IMInstanciaAwareModel<T>) model;
        MInstancia instancia = instAwareModel.getMInstancia();
        MTipo<?> tipo = instancia.getMTipo();
        tipo.validateValue(new ValueValidatableAdapter<T>(component, this, wicketValidatable, instAwareModel));
    }
}
