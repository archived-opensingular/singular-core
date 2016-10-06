package org.opensingular.form.provider;

import java.io.Serializable;
import java.util.List;

import org.opensingular.form.SInstance;

public interface Provider<E extends Serializable, S extends SInstance> extends Serializable {

    List<E> load(ProviderContext<S> context);

}