/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.wicket.behavior.CountDownBehaviour;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.StringValidator;

import java.util.Optional;

import static br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior.Masks;

public class StringMapper extends ControlsFieldComponentAbstractMapper {

    @Override
    public Component appendInput() {
        FormComponent<?> comp;

        formGroup.appendInputText(comp = new TextField<>(model.getObject().getName(),
            new MInstanciaValorModel<>(model), String.class).setLabel(labelModel));

        Optional<Integer> maxSize = Optional.ofNullable(
                model.getObject().getAttributeValue(SPackageBasic.ATR_MAX_LENGTH));
        if (maxSize.isPresent()) {
            comp.add(StringValidator.maximumLength(maxSize.get()));
            comp.add(new CountDownBehaviour());
        }

        Optional<String> basicMask = Optional.ofNullable(
                model.getObject().getAttributeValue(SPackageBasic.ATR_BASIC_MASK));
        if (basicMask.isPresent()) {
            comp.add(new InputMaskBehavior(Masks.valueOf(basicMask.get())));
            comp.setOutputMarkupId(true);
        }

        return comp;
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        if ((mi != null) && (mi.getValue() != null)) {
            return String.valueOf(mi.getValue());
        }
        return StringUtils.EMPTY;
    }

}