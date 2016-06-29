package br.net.mirante.singular.form.builder.selection;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeComposite;

public class AbstractBuilder {

    protected final SType   type;
    protected final boolean isList;
    protected final boolean isComposite;

    public AbstractBuilder(SType type) {
        this.type = type;
        this.isList = type.getClass().isAssignableFrom(SIList.class);
        this.isComposite = type.getClass().isAssignableFrom(STypeComposite.class);
    }
}