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
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.opensingular.form.view.SMultiSelectionBySelectView;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.ReadOnlyModelValue;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.multiselect.ChosenOptions;

@SuppressWarnings("serial")
public class MultipleSelectBSMapper extends MultipleSelectMapper {

    @Override
    protected Component appendFormGroup(BSControls formGroup, WicketBuildContext ctx) {
        final ListMultipleChoice<?> choices = retrieveChoices(ctx.getModel(), new ReadOnlyModelValue(ctx.getModel()));
        formGroup.appendMultiSelect(choices.setMaxRows(5), createChosenOptions(((SMultiSelectionBySelectView) ctx.getView())));
        return choices;
    }

    private ChosenOptions createChosenOptions(SMultiSelectionBySelectView view) {
        return new ChosenOptions()
                .setDataPlaceholder(view.getDataPlaceholder())
                .setDisableSearch(view.isDisableSearch())
                .setDisableSearchThreshold(view.getDisableSearchThreshold())
                .setHideResultsOnSelect(view.isHideResultsOnSelect())
                .setNoResultsText(view.getNoResultsText())
                .setShowSpinner(view.isShowSpinner());
    }
}
