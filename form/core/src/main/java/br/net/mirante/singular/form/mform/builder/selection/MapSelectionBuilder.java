package br.net.mirante.singular.form.mform.builder.selection;

import java.util.HashMap;

import br.net.mirante.singular.form.mform.SType;

public class MapSelectionBuilder extends AbstractBuilder {

    public MapSelectionBuilder(SType type) {
        super(type);
    }

    public MapSelectionDisplayBuilder id(SType id) {
        String simpleName = id.getNameSimple();
        type.asAtrProvider().idFunction((map) -> ((HashMap) map).get(simpleName));
        return new MapSelectionDisplayBuilder(super.type);
    }

}
