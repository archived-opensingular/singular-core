/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.model.MultipleSelectMInstanceAwareModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import java.util.List;

@SuppressWarnings("serial")
public class MultipleCheckMapper extends MultipleSelectMapper {

    @Override
    protected ListMultipleChoice<?> retrieveChoices(IModel<? extends SInstance> model, List<?> opcoesValue) {
        return new CheckBoxMultipleChoice(
                model.getObject().getName(),
                new MultipleSelectMInstanceAwareModel(model),
                opcoesValue, renderer())
                .setLabelPosition(AbstractChoice.LabelPosition.WRAP_AFTER);
    }

    @Override
    protected Component formGroupAppender(BSControls formGroup, IModel<? extends SInstance> model, List<?> opcoesValue) {
        final ListMultipleChoice choices = retrieveChoices(model, opcoesValue);
        formGroup.appendCheckboxChoice( retrieveChoices(model, opcoesValue));
        return choices;
    }

}
