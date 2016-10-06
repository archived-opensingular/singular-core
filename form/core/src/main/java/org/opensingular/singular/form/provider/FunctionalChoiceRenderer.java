package org.opensingular.singular.form.provider;

import br.net.mirante.singular.commons.lambda.IFunction;

import java.io.Serializable;

public class FunctionalChoiceRenderer<T extends Serializable> implements ChoiceRenderer<T> {

    private static final long serialVersionUID = 5161774886991801853L;

    private IFunction<T, String> displayFunction;
    private IFunction<T, String> idFunction;

    @Override
    public String getIdValue(T option) {
        return idFunction.apply(option);
    }

    @Override
    public String getDisplayValue(T option) {
        return displayFunction.apply(option);
    }

    public IFunction<T, String> getDisplayFunction() {
        return displayFunction;
    }

    public void setDisplayFunction(IFunction<T, String> displayFunction) {
        this.displayFunction = displayFunction;
    }

    public IFunction<T, String> getIdFunction() {
        return idFunction;
    }

    public void setIdFunction(IFunction<T, String> idFunction) {
        this.idFunction = idFunction;
    }

}