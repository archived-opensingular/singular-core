package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;

import java.io.Serializable;

@FunctionalInterface
public interface ValueToSInstanceConverter<S extends SInstance, T> extends Serializable {

    void convert(S emptyInstance, T object);

}
