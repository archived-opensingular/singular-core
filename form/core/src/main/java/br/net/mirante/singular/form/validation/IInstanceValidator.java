package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.SInstance;

/**
 * Validator para {@link SInstance}
 * 
 * @param <MInstancia>
 */
public interface IInstanceValidator<I extends SInstance> {
    
    void validate(IInstanceValidatable<I> validatable);
}
