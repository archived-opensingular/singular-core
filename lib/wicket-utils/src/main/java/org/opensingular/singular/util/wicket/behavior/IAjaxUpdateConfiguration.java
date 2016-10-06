/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.behavior;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.Behavior;

import org.opensingular.singular.commons.lambda.IBiConsumer;
import org.opensingular.singular.commons.lambda.ITriConsumer;

public interface IAjaxUpdateConfiguration<C extends Component> extends Serializable {

    IAjaxUpdateConfiguration<C> setOnError(ITriConsumer<AjaxRequestTarget, Component, RuntimeException> onError);
    IAjaxUpdateConfiguration<C> setUpdateAjaxAttributes(IBiConsumer<Component, AjaxRequestAttributes> updateAjaxAttributes);
    IAjaxUpdateConfiguration<C> setRefreshTargetComponent(boolean refresh);
    C getTargetComponent();

    default Behavior getBehavior() {
        return (Behavior) this;
    }
}
