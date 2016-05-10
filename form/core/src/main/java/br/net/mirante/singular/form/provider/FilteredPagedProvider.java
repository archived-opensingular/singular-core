package br.net.mirante.singular.form.provider;

import br.net.mirante.singular.form.SInstance;

import java.io.Serializable;

public interface FilteredPagedProvider<R extends Serializable> extends FilteredProvider<R> {

    long getSize(ProviderContext<SInstance> context);

}