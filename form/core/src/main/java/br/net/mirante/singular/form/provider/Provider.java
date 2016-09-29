package br.net.mirante.singular.form.provider;

import java.io.Serializable;
import java.util.List;

import br.net.mirante.singular.form.SInstance;

public interface Provider<E extends Serializable, S extends SInstance> extends Serializable {

    List<E> load(ProviderContext<S> context);

}