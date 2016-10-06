package org.opensingular.form.builder.selection;

import org.opensingular.form.SIList;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SType;

public class AbstractBuilder {

    protected final SType<?> type;
    protected final boolean  isList;
    protected final boolean  isComposite;

    public AbstractBuilder(SType<?> type) {
        this.type = type;
        this.isList = type.getClass().isAssignableFrom(SIList.class);
        this.isComposite = type.getClass().isAssignableFrom(STypeComposite.class);
    }
}