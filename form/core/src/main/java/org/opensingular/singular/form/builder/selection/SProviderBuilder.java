package org.opensingular.singular.form.builder.selection;

import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.provider.LookupOptionsProvider;
import org.opensingular.singular.form.provider.SSimpleProvider;
import org.opensingular.singular.form.provider.STextQueryProvider;

public class SProviderBuilder extends AbstractBuilder {

    public SProviderBuilder(SType type) {
        super(type);
    }

    public <T extends SSimpleProvider> void simpleProvider(Class<T> provider) {
        type.asAtrProvider().asAtrProvider().provider(new LookupOptionsProvider(provider));
    }

    public <T extends STextQueryProvider> void filteredProvider(Class<T> provider) {
        type.asAtrProvider().asAtrProvider().provider(new LookupOptionsProvider(provider));
    }

    public <T extends SSimpleProvider> void simpleProvider(String providerName) {
        type.asAtrProvider().asAtrProvider().provider(new LookupOptionsProvider(providerName));
    }

    public <T extends STextQueryProvider> void filteredProvider(String providerName) {
        type.asAtrProvider().asAtrProvider().provider(new LookupOptionsProvider(providerName));
    }

    public void simpleProvider(SSimpleProvider sSimpleProvider) {
        type.asAtrProvider().asAtrProvider().provider(sSimpleProvider);
    }

    public void filteredProvider(STextQueryProvider mapSimpleProvider) {
        type.asAtrProvider().asAtrProvider().provider(mapSimpleProvider);
    }

}