package org.opensingular.singular.form.builder.selection;

import java.io.Serializable;
import java.util.Arrays;

import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.provider.LookupOptionsProvider;
import org.opensingular.singular.form.provider.Provider;
import org.opensingular.singular.form.provider.SimpleProvider;
import org.opensingular.singular.form.provider.TextQueryProvider;

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

    public void filteredProvider(TextQueryProvider<TYPE, ROOT_TYPE> provider) {
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
