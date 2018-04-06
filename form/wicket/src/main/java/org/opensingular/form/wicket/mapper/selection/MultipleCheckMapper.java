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
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.SMultiSelectionByCheckboxView;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.MultipleSelectSInstanceAwareModel;
import org.opensingular.form.wicket.model.ReadOnlyModelValue;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class MultipleCheckMapper extends MultipleSelectMapper {

    @Override
    protected ListMultipleChoice<?> retrieveChoices(IModel<? extends SInstance> model, IModel<List<Serializable>> valuesModel) {
        MultipleSelectSInstanceAwareModel multiSelectModel = new MultipleSelectSInstanceAwareModel(model);
        CheckBoxMultipleChoice<Serializable> formComp = new CheckBoxMultipleChoice<>(model.getObject().getName(), multiSelectModel, valuesModel, renderer(model));
        formComp.setLabelPosition(AbstractChoice.LabelPosition.WRAP_AFTER);
        return formComp;
    }

    @Override
    protected Component appendFormGroup(BSControls formGroup, WicketBuildContext ctx) {
        final SMultiSelectionByCheckboxView view = (SMultiSelectionByCheckboxView) ctx.getView();
        final ListMultipleChoice choices = retrieveChoices(ctx.getModel(), new ReadOnlyModelValue(ctx.getModel()));
        formGroup.appendCheckboxChoice(choices, view.isInline());
        return choices;
    }
}
