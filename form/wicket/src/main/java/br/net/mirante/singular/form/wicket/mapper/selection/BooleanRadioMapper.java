/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.view.SViewBooleanByRadio;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IModel;

public class BooleanRadioMapper extends RadioMapper {

    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        Boolean valor = mi.getValue(Boolean.class);
        if (valor != null) {
            SViewBooleanByRadio booleanRadioView = (SViewBooleanByRadio) mi.getType().getView();
            if (valor) {
                return booleanRadioView.labelTrue();
            } else {
                return booleanRadioView.labelFalse();
            }
        }
        return StringUtils.EMPTY;
    }
}
