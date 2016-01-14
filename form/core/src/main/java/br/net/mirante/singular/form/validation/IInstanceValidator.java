package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.MInstancia;

/**
 * Validator para {@link MInstancia}
 * 
 * @param <MInstancia>
 */
public interface IInstanceValidator<I extends MInstancia> {
    
    void validate(IInstanceValidatable<I> validatable);
}
