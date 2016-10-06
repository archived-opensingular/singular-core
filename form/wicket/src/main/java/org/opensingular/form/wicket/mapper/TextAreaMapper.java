/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.mapper;

import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.StringValidator;

import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewTextArea;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.CountDownBehaviour;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSControls;

public class TextAreaMapper extends StringMapper {

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();
        final SView view = ctx.getView();

        if (view instanceof SViewTextArea) {

            SViewTextArea mTextAreaView = (SViewTextArea) view;

            final SInstance mi = model.getObject();
            FormComponent<?> textArea = new TextArea<>(mi.getName(), new SInstanceValueModel<>(model));
            textArea.setLabel(labelModel);
            formGroup.appendTextarea(textArea, mTextAreaView.getLines());

            Optional<Integer> maxSize = Optional.ofNullable(mi.getAttributeValue(SPackageBasic.ATR_MAX_LENGTH));

            if (maxSize.isPresent()) {
                textArea.add(StringValidator.maximumLength(maxSize.get()));
                textArea.add(new CountDownBehaviour());
            }

            return textArea;
        }

        throw new WicketRuntimeException("TextAreaMapper deve ser utilizado com MTextAreaView");

    }
}
