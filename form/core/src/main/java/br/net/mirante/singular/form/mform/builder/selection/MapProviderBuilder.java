package br.net.mirante.singular.form.mform.builder.selection;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.provider.MapFilteredProvider;
import br.net.mirante.singular.form.mform.provider.MapSimpleProvider;

public class MapProviderBuilder extends AbstractBuilder {

    public MapProviderBuilder(SType type) {
        super(type);
    }

    public void provider(MapSimpleProvider mapSimpleProvider){
        type.asAtrProvider().asAtrProvider().provider(mapSimpleProvider);
    }

    public void filteredProvider(MapFilteredProvider mapSimpleProvider){
        type.asAtrProvider().asAtrProvider().provider(mapSimpleProvider);
    }
}
