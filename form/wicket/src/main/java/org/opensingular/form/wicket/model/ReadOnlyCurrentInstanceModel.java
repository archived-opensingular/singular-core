package org.opensingular.form.wicket.model;

import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;


public class ReadOnlyCurrentInstanceModel<I extends SInstance> implements IReadOnlyModel<I> {

    private final WicketBuildContext context;

    public ReadOnlyCurrentInstanceModel(WicketBuildContext context) {
        this.context = context;
    }

    @Override
    public I getObject() {
        return context.getCurrentInstance();
    }
}
