package org.opensingular.form.wicket.model;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SInstance;
import org.opensingular.form.provider.ProviderLoader;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by italoferreira on 20/03/18 - 19:25
 */
public class ReadOnlyModelValue implements IReadOnlyModel<List<Serializable>> {

    private IModel<? extends SInstance> model;

    public ReadOnlyModelValue(IModel<? extends SInstance> model) {
        this.model = model;
    }

    @Override
    public List<Serializable> getObject() {
        final RequestCycle requestCycle = RequestCycle.get();
        boolean            ajaxRequest  = requestCycle != null && requestCycle.find(AjaxRequestTarget.class) != null;
        /* Se for requisição Ajax, limpa o campo caso o valor não for encontrado, caso contrario mantem o valor. */
        boolean enableDanglingValues = !ajaxRequest;
        return new ProviderLoader(model::getObject, enableDanglingValues).load();
    }
}
