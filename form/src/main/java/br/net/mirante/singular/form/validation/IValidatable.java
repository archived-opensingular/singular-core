package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.MInstancia;

public interface IValidatable<T> {
    T getValue();
    MInstancia getMInstancia();
    void error(IValidationError error);
    boolean isValid();
}
