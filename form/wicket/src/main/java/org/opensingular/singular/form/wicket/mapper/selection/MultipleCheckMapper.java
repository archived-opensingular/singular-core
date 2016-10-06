/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.mapper.selection;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import org.opensingular.form.SInstance;
import org.opensingular.singular.form.wicket.model.MultipleSelectSInstanceAwareModel;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSControls;

@SuppressWarnings("serial")
public class MultipleCheckMapper extends MultipleSelectMapper {

    @Override
    protected ListMultipleChoice<?> retrieveChoices(IModel<? extends SInstance> model, List<?> opcoesValue) {
        return new CheckBoxMultipleChoice(
                model.getObject().getName(),
                new MultipleSelectSInstanceAwareModel(model),
                opcoesValue, renderer(model))
                .setLabelPosition(AbstractChoice.LabelPosition.WRAP_AFTER);
    }

    @Override
    protected Component formGroupAppender(BSControls formGroup, IModel<? extends SInstance> model, List<?> opcoesValue) {
        final ListMultipleChoice choices = retrieveChoices(model, opcoesValue);
        formGroup.appendCheckboxChoice( choices );
        return choices;
    }

}
