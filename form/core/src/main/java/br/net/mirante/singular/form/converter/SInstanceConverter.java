package br.net.mirante.singular.form.converter;

import br.net.mirante.singular.form.SInstance;

import java.io.Serializable;

/**
 * @param <T> O tipo do objeto a ser convertido
 */
public interface SInstanceConverter<T extends Serializable, S extends SInstance> {

    void fillInstance(S ins, T obj);

    T toObject(S ins);

}
