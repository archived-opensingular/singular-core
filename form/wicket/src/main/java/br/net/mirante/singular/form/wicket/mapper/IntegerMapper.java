/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;

import java.util.HashMap;
import java.util.Optional;

import static br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior.Masks;

public class IntegerMapper extends StringMapper {

    private static final int DEFAULT_SIZE = 9;

    @Override
    public Component appendInput() {
        Optional<Integer> size = Optional.ofNullable(
                model.getObject().getAttributeValue(SPackageBasic.ATR_TAMANHO_MAXIMO));
        TextField<Integer> comp = new TextField<>(model.getObject().getName(),
                new MInstanciaValorModel<>(model), Integer.class);
        formGroup.appendInputText(comp.setLabel(labelModel).setOutputMarkupId(true)
                .add(new InputMaskBehavior(Masks.NUMERIC, new HashMap<String, Object>() {{
                    put(InputMaskBehavior.MAX_LENGTH_ATTR, size.orElse(DEFAULT_SIZE));
                }})));
        return comp;
    }
}
