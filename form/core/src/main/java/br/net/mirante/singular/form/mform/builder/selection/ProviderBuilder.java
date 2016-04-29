package br.net.mirante.singular.form.mform.builder.selection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.options.LookupOptionsProvider;
import br.net.mirante.singular.form.mform.provider.FilteredProvider;
import br.net.mirante.singular.form.mform.provider.Provider;
import br.net.mirante.singular.form.mform.provider.SimpleProvider;

import java.io.Serializable;
import java.util.Arrays;

public class ProviderBuilder<TYPE extends Serializable, ROOT_TYPE extends SInstance> extends AbstractBuilder {

    public ProviderBuilder(SType type) {
        super(type);
    }

    protected void provider(Provider<TYPE, ROOT_TYPE> provider) {
        type.asAtrProvider().provider(provider);
    }

    public void simpleProvider(SimpleProvider<TYPE, ROOT_TYPE> provider) {
        provider(provider);
    }

    public void filteredProvider(FilteredProvider<TYPE, ROOT_TYPE> provider) {
        type.asAtrProvider().provider(provider);
    }

    @SafeVarargs
    public final void simpleProviderOf(TYPE... values) {
        type.asAtrProvider().provider((SimpleProvider<TYPE, ROOT_TYPE>) ins -> Arrays.asList(values));
    }

    public void provider(String provider) {
        type.asAtrProvider().provider(new LookupOptionsProvider(provider));
    }

    public <X extends Provider> void provider(Class<X> provider) {
        type.asAtrProvider().provider(new LookupOptionsProvider(provider));
    }

}
