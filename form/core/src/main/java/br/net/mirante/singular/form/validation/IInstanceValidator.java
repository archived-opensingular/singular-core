package br.net.mirante.singular.form.validation;

public interface IInstanceValidator<T> {
    void validate(IValueValidatable<T> validatable);
}
