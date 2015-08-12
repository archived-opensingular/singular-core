package br.net.mirante.singular.form.validation;

public interface IValidator<T> {
    void validate(IValidatable<T> validatable);
}
