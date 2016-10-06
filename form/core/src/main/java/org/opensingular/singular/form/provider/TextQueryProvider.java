package org.opensingular.singular.form.provider;

import org.opensingular.singular.form.SInstance;

import java.io.Serializable;
import java.util.List;

public interface TextQueryProvider<E extends Serializable, S extends SInstance> extends Provider<E, S> {

    @Override
    default List<E> load(ProviderContext<S> context) {
        return load(context.getInstance(), context.getQuery());
    }

    List<E> load(S ins, String query);

}