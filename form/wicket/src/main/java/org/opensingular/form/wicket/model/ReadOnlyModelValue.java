package org.opensingular.form.wicket.model;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SInstance;
import org.opensingular.form.provider.ProviderLoader;
import org.opensingular.lib.wicket.util.model.ReloadableDetachableModel;

import java.io.Serializable;
import java.util.List;

public class ReadOnlyModelValue extends ReloadableDetachableModel<List<Serializable>> {

    private IModel<? extends SInstance> model;

    public ReadOnlyModelValue(IModel<? extends SInstance> model) {
        this.model = model;
    }

    @Override
    protected List<Serializable> load() {
        final RequestCycle requestCycle = RequestCycle.get();
        boolean            ajaxRequest  = requestCycle != null && requestCycle.find(AjaxRequestTarget.class) != null;
        boolean enableDanglingValues = !ajaxRequest;
        return new ProviderLoader(model::getObject, enableDanglingValues).load();
    }
}
