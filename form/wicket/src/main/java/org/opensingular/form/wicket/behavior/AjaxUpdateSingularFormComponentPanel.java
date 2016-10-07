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

package org.opensingular.form.wicket.behavior;

import org.opensingular.form.wicket.component.SingularFormComponentPanel;
import org.opensingular.lib.commons.lambda.IBiConsumer;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.IAjaxUpdateListener;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * Evento ajax do singular para componente do tipo {@link SingularFormComponentPanel}
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
