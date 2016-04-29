package br.net.mirante.singular.form.mform.builder.selection;

import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;

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