package org.opensingular.form.provider;

import org.opensingular.form.SInstance;

import java.io.Serializable;

public interface FilteredPagedProvider<R extends Serializable> extends FilteredProvider<R> {

    long getSize(ProviderContext<SInstance> context);

}