/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.wicket.util.behavior;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.Behavior;

import org.opensingular.lib.commons.lambda.IBiConsumer;
import org.opensingular.lib.commons.lambda.ITriConsumer;

public interface IAjaxUpdateConfiguration<C extends Component> extends Serializable {

    IAjaxUpdateConfiguration<C> setOnError(ITriConsumer<AjaxRequestTarget, Component, RuntimeException> onError);
    IAjaxUpdateConfiguration<C> setUpdateAjaxAttributes(IBiConsumer<Component, AjaxRequestAttributes> updateAjaxAttributes);
    IAjaxUpdateConfiguration<C> setRefreshTargetComponent(boolean refresh);
    C getTargetComponent();

    default Behavior getBehavior() {
        return (Behavior) this;
    }
}
