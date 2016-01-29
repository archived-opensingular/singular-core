package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.SInstance2;

/**
 * Validator para {@link SInstance2}
 * 
 * @param <MInstancia>
 */
public interface IInstanceValidator<I extends SInstance2> {
    
    void validate(IInstanceValidatable<I> validatable);
}
