package br.net.mirante.singular.form.mform.builder.selection;

import br.net.mirante.singular.form.mform.SType;

import java.util.HashMap;

public class MapSelectionBuilder extends AbstractBuilder {

    public MapSelectionBuilder(SType type) {
        super(type);
    }

    public MapSelectionDisplayBuilder id(SType id) {
        String simpleName = id.getNameSimple();
        type.asAtrProvider().asAtrProvider().idFunction((map) -> ((HashMap) map).get(simpleName));
        return new MapSelectionDisplayBuilder(super.type);
    }

}
