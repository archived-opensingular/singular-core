/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.TelefoneNacionalMaskBehaviour;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;


public class TelefoneNacionalMapper extends StringMapper {

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final Component inputComponent = super.appendInput(ctx, formGroup, labelModel);
        inputComponent.add(new TelefoneNacionalMaskBehaviour());
        return inputComponent;
    }
}
