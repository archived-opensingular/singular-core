package br.net.mirante.singular.form.validation;

public interface IValidatable<T> {
    T getValue();
    void error(IValidationError error);
}
