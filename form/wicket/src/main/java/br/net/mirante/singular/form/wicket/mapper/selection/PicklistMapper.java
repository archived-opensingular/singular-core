/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import org.opensingular.singular.form.SInstance;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import java.util.List;

@SuppressWarnings("serial")
public class PicklistMapper extends MultipleSelectMapper {

    @Override
    protected Component formGroupAppender(BSControls formGroup,
                                          IModel<? extends SInstance> model,
                                          final List<?> opcoesValue) {
        return formGroup.appendPicklist(retrieveChoices(model, opcoesValue));
    }
}