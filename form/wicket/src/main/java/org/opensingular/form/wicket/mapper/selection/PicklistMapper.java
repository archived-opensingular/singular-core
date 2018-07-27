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

package org.opensingular.form.wicket.mapper.selection;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.IAjaxUpdateListener;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.form.wicket.mapper.SingularEventsHandlers;
import org.opensingular.form.wicket.model.ReadOnlyModelValue;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

import static org.opensingular.form.wicket.mapper.SingularEventsHandlers.OPTS_ORIGINAL_PROCESS_EVENT;
import static org.opensingular.form.wicket.mapper.SingularEventsHandlers.OPTS_ORIGINAL_VALIDATE_EVENT;

@SuppressWarnings("serial")
public class PicklistMapper extends MultipleSelectMapper {

    private String JS_PICKLIST_CHANGE_EVENT = "picklist:selected";
    @Override
    protected Component appendFormGroup(BSControls formGroup, WicketBuildContext ctx) {
        return formGroup.appendPicklist(retrieveChoices(ctx.getModel(), new ReadOnlyModelValue(ctx.getModel())));
    }

    @Override
    public void addAjaxUpdate(WicketBuildContext ctx, Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
        component.add(new SingularEventsHandlers(SingularEventsHandlers.FUNCTION.ADD_TEXT_FIELD_HANDLERS)
                .setOption(OPTS_ORIGINAL_PROCESS_EVENT, JS_PICKLIST_CHANGE_EVENT)
                .setOption(OPTS_ORIGINAL_VALIDATE_EVENT, JS_PICKLIST_CHANGE_EVENT))
                .add(AjaxUpdateInputBehavior.forProcess(model, listener))
                .add(AjaxUpdateInputBehavior.forValidate(model, listener));
    }
}