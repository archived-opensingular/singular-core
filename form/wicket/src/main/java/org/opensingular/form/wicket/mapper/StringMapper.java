/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.mapper;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.StringValidator;

import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.CountDownBehaviour;
import org.opensingular.form.wicket.behavior.InputMaskBehavior;
import org.opensingular.form.wicket.behavior.InputMaskBehavior.Masks;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSControls;

public class StringMapper extends AbstractControlsFieldComponentMapper {

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();

        FormComponent<?> comp;

        formGroup.appendInputText(comp = new TextField<>(model.getObject().getName(),
            new SInstanceValueModel<>(model), String.class).setLabel(labelModel));

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