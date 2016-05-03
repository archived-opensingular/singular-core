package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;

import java.io.Serializable;
import java.util.List;

public interface Provider<E extends Serializable, S extends SInstance> extends Serializable {

    List<E> load(ProviderContext<S> context);

}