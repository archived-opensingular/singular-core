/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.wicket.behavior.TelefoneNacionalMaskBehaviour;
import org.apache.wicket.Component;


public class TelefoneNacionalMapper extends StringMapper {

    @Override
    public Component appendInput() {
        final Component inputComponent = super.appendInput();
        inputComponent.add(new TelefoneNacionalMaskBehaviour());
        return inputComponent;
    }
}
