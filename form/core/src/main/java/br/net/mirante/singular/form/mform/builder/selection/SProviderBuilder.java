package br.net.mirante.singular.form.mform.builder.selection;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.provider.SFilteredProvider;
import br.net.mirante.singular.form.mform.provider.SSimpleProvider;

public class SProviderBuilder extends AbstractBuilder {

    public SProviderBuilder(SType type) {
        super(type);
    }

    public void provider(SSimpleProvider sSimpleProvider){
        type.asAtrProvider().asAtrProvider().provider(sSimpleProvider);
    }

    public void filteredProvider(SFilteredProvider mapSimpleProvider){
        type.asAtrProvider().asAtrProvider().provider(mapSimpleProvider);
    }
}
