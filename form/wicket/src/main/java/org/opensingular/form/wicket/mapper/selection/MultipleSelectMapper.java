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
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.enums.PhraseBreak;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.AbstractControlsFieldComponentMapper;
import org.opensingular.form.wicket.model.MultipleSelectSInstanceAwareModel;
import org.opensingular.form.wicket.model.ReadOnlyModelValue;
import org.opensingular.form.wicket.renderer.SingularChoiceRenderer;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class MultipleSelectMapper extends AbstractControlsFieldComponentMapper {

    @Override
    @SuppressWarnings("rawtypes")
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        return appendFormGroup(formGroup, ctx);
    }

    protected Component appendFormGroup(BSControls formGroup, WicketBuildContext ctx) {
        final ListMultipleChoice<?> choices = retrieveChoices(ctx.getModel(), new ReadOnlyModelValue(ctx.getModel()));
        formGroup.appendSelect(choices.setMaxRows(5), false, null);
        return choices;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected ListMultipleChoice<?> retrieveChoices(IModel<? extends SInstance> model, final IModel<List<Serializable>> valuesModel) {
        return new SListMultipleChoice(model.getObject().getName(), new MultipleSelectSInstanceAwareModel(model), valuesModel, renderer(model));
    }

    protected IChoiceRenderer<Serializable> renderer(IModel<? extends SInstance> model) {
        return new SingularChoiceRenderer(model);
    }

    @Override
    public String getReadOnlyFormattedText(WicketBuildContext ctx, IModel<? extends SInstance> model) {
        SInstance mi = model.getObject();
        if (!(mi instanceof SIList)) {
            return "";
        }
        StringBuilder output = new StringBuilder();
        boolean       first  = true;
        for (SInstance val : ((SIList<?>) mi).getChildren()) {
            Serializable converted = mi.asAtrProvider().getConverter().toObject(val);
            if (converted != null) {
                String label = mi.asAtrProvider().getDisplayFunction().apply(converted);
                if (first) {
                    output.append(label);
                    first = false;
                } else {
                    //TODO implementar logica de auto detecção
                    output.append(processPhraseBreak(mi));
                    output.append(label);
                }
            }
        }
        return output.toString();
    }

    private String processPhraseBreak(SInstance mi) {
        PhraseBreak phraseBreak = mi.asAtr().phraseBreak();
        if (phraseBreak == PhraseBreak.BREAK_LINE) {
            return "\n";
        } else if (phraseBreak == PhraseBreak.COMMA) {
            return ", ";
        }
        return "";
    }

}
