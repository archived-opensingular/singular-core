/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior.Masks;
import br.net.mirante.singular.form.wicket.model.SInstanceValueModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import java.util.HashMap;
import java.util.Optional;

public class NumberMapper<T extends Number> extends StringMapper {

    private static final int DEFAULT_SIZE = 9;

    private final Class<T> numberType;

    public NumberMapper(Class<T> numberType) {
        this.numberType = numberType;
    }

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {

        final IModel<? extends SInstance> model = ctx.getModel();
        final Optional<Integer>           size  = Optional.ofNullable(model.getObject().getAttributeValue(SPackageBasic.ATR_MAX_LENGTH));
        final TextField<T>                comp  = new TextField<>(model.getObject().getName(), new SInstanceValueModel<>(model), numberType);

        formGroup
                .appendInputText(comp
                        .setLabel(labelModel)
                        .setOutputMarkupId(true)
                        .add(new InputMaskBehavior(Masks.NUMERIC, new HashMap<String, Object>() {{
                            put(InputMaskBehavior.MAX_LENGTH_ATTR, size.orElse(DEFAULT_SIZE));
                        }}))
                );

        return comp;
    }

}