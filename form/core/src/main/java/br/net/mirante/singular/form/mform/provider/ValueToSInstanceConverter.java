package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;

import java.io.Serializable;

@FunctionalInterface
public interface ValueToSInstanceConverter<T> extends Serializable {

    void convert(SInstance emptyInstance, T object);

}
