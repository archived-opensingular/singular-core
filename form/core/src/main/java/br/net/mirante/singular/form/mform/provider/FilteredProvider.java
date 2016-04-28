package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;

import java.io.Serializable;
import java.util.List;

public interface FilteredProvider<E extends Serializable, S extends SInstance> extends Provider<E, S> {

    List<E> load(S ins, String query);

}