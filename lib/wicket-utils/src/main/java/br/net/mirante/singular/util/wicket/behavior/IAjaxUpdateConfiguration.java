package br.net.mirante.singular.util.wicket.behavior;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.Behavior;

import br.net.mirante.singular.util.wicket.lambda.IBiConsumer;
import br.net.mirante.singular.util.wicket.lambda.ITriConsumer;

public interface IAjaxUpdateConfiguration<C extends Component> extends Serializable {

    IAjaxUpdateConfiguration<C> setOnError(ITriConsumer<AjaxRequestTarget, Component, RuntimeException> onError);
    IAjaxUpdateConfiguration<C> setUpdateAjaxAttributes(IBiConsumer<Component, AjaxRequestAttributes> updateAjaxAttributes);
    IAjaxUpdateConfiguration<C> setRefreshTargetComponent(boolean refresh);
    C getTargetComponent();

    default Behavior getBehavior() {
        return (Behavior) this;
    }
}
