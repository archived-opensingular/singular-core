/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentAbstractMapper;
import br.net.mirante.singular.form.wicket.model.MultipleSelectMInstanceAwareModel;
import br.net.mirante.singular.form.wicket.renderer.SingularChoiceRenderer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.apache.wicket.util.lang.Generics.newArrayList;

@SuppressWarnings("serial")
public class MultipleSelectMapper extends ControlsFieldComponentAbstractMapper {

    @Override
    @SuppressWarnings("rawtypes")
    public Component appendInput() {
        final List<?>   opcoesValue;
        if (model.getObject().getType() instanceof STypeList) {
            opcoesValue = model.getObject().asAtrProvider().getSimpleProvider().load(ctx.getCurrentInstance());
        } else {
            opcoesValue = newArrayList();
        }
        return formGroupAppender(formGroup, model, opcoesValue);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected ListMultipleChoice<?> retrieveChoices(IModel<? extends SInstance> model, final List<?> opcoesValue) {
        return new ListMultipleChoice(model.getObject().getName(), new MultipleSelectMInstanceAwareModel(model), opcoesValue, renderer());
    }

    @SuppressWarnings("rawtypes")
    protected Component formGroupAppender(BSControls formGroup,
                                          IModel<? extends SInstance> model,
                                          final List<?> opcoesValue) {
        final ListMultipleChoice<?> choices = retrieveChoices(model, opcoesValue);
        formGroup.appendSelect(choices.setMaxRows(5), true, false);
        return choices;
    }

    @SuppressWarnings("rawtypes")
    protected IChoiceRenderer<Serializable> renderer() {
        return new SingularChoiceRenderer(model);
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final StringBuilder output = new StringBuilder();
        final SInstance     mi     = model.getObject();
        if (mi instanceof SIList) {
            final Collection children = ((SIList) mi).getChildren();
            final Iterator   iterator = children.iterator();
            boolean          first    = true;
            while (iterator.hasNext()) {
                final SInstance val       = (SInstance) iterator.next();
                final Object    converted = mi.asAtrProvider().getConverter().toObject(val);
                final String    label     = mi.asAtrProvider().getDisplayFunction().apply(converted);
                if (first) {
                    output.append(label);
                    first = false;
                } else {
                    output.append(", ");
                    output.append(label);
                }
            }
        }
        return output.toString();
    }

}
