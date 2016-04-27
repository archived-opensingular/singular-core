package br.net.mirante.singular.form.mform.converter;

import br.net.mirante.singular.form.mform.SInstance;

/**
 * @param <T> O tipo do objeto a ser convertido
 */
public interface SInstanceConverter<T> {

    void fillInstance(SInstance ins, T obj);

    T toObject(SInstance ins);

}
