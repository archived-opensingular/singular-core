/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.mapper;

import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.TelefoneNacionalMaskBehaviour;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

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
