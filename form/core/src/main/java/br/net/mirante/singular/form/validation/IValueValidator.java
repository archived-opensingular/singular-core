package br.net.mirante.singular.form.validation;

public interface IValueValidator<T> {
    void validate(IValueValidatable<T> validatable);
}
