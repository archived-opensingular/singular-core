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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;

import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.IAjaxUpdateListener;

public class AjaxUpdateInputBehavior extends AjaxFormComponentUpdatingBehavior {

    private final IAjaxUpdateListener listener;
    private final IModel<SInstance>   model;
    private final boolean             validateOnly;

    public AjaxUpdateInputBehavior(String event, IModel<SInstance> model, boolean validateOnly, IAjaxUpdateListener listener) {
        super(event);
        this.model = model;
        this.validateOnly = validateOnly;
        this.listener = listener;
    }

    public static AjaxUpdateInputBehavior forValidate(IModel<SInstance> model, IAjaxUpdateListener listener) {
        return new AjaxUpdateInputBehavior(IWicketComponentMapper.SINGULAR_VALIDATE_EVENT, model, true, listener);
    }
    public static AjaxUpdateInputBehavior forProcess(IModel<SInstance> model, IAjaxUpdateListener listener) {
        return new AjaxUpdateInputBehavior(IWicketComponentMapper.SINGULAR_PROCESS_EVENT, model, false, listener);
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);
        //if (validateOnly)
        //    attributes.getExtraParameters().put("forceDisableAJAXPageBlock", true);
    }

    @Override
    public void onUpdate(AjaxRequestTarget target) {
        Component comp = this.getComponent();
        if (validateOnly) {
            listener.onValidate(comp, target, model);
        } else
            listener.onProcess(comp, target, model);
    }

}