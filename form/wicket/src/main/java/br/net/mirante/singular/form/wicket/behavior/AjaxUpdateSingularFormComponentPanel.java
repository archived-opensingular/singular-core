package br.net.mirante.singular.form.wicket.behavior;

import br.net.mirante.singular.commons.lambda.IBiConsumer;
import org.opensingular.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.IAjaxUpdateListener;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * Evento ajax do singular para componente do tipo {@link br.net.mirante.singular.form.wicket.component.SingularFormComponentPanel}
 * @param <T>
 */
public class AjaxUpdateSingularFormComponentPanel<T> extends AbstractDefaultAjaxBehavior {

    public static final String VALUE_REQUEST_PARAMETER_NAME = "value";

    private final IAjaxUpdateListener listener;
    private final IModel<SInstance> model;
    private IBiConsumer<T, IModel<SInstance>> valueModelResolver;
    private Class<T> type;

    public AjaxUpdateSingularFormComponentPanel(IModel<SInstance> model, IAjaxUpdateListener listener) {
        this.listener = listener;
        this.model = model;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public void setValueModelResolver(IBiConsumer<T, IModel<SInstance>> valueModelResolver) {
        this.valueModelResolver = valueModelResolver;
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        T value = this.getComponent().getRequest().getRequestParameters().getParameterValue(VALUE_REQUEST_PARAMETER_NAME).to(type);
        valueModelResolver.accept(value, model);
        target.add(this.getComponent());
        listener.onProcess(this.getComponent(), target, model);
    }

}
