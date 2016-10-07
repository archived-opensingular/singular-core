package org.opensingular.form.provider;

import org.opensingular.form.SInstance;

import java.io.Serializable;

public interface FilteredProvider<R extends Serializable> extends Provider<R, SInstance> {

    void configureProvider(Config cfg);

}