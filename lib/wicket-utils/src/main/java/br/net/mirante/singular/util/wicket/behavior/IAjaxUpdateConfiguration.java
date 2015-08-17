package br.net.mirante.singular.util.wicket.behavior;

import br.net.mirante.singular.util.wicket.lambda.IBiConsumer;
import br.net.mirante.singular.util.wicket.lambda.ITriConsumer;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.Behavior;

import java.io.Serializable;

public interface IAjaxUpdateConfiguration extends Serializable {

    IAjaxUpdateConfiguration setOnError(ITriConsumer<AjaxRequestTarget, Component, RuntimeException> onError);
    IAjaxUpdateConfiguration setUpdateAjaxAttributes(IBiConsumer<Component, AjaxRequestAttributes> updateAjaxAttributes);
    IAjaxUpdateConfiguration setRefreshTargetComponent(boolean refresh);
    Component getTargetComponent();

    default Behavior getBehavior() {
        return (Behavior) this;
    }
}
