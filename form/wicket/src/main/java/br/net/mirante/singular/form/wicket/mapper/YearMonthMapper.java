/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.behavior.InputMaskBehavior;
import br.net.mirante.singular.form.wicket.model.MInstanciaValorModel;
import br.net.mirante.singular.util.wicket.form.YearMonthField;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import java.time.YearMonth;
import java.util.HashMap;

public class YearMonthMapper extends ControlsFieldComponentAbstractMapper {

    public Component appendInput() {
        YearMonthField comp = new YearMonthField(model.getObject().getName(), new MInstanciaValorModel<>(model));
        formGroup.appendDatepicker(comp.setLabel(labelModel)
                        .setOutputMarkupId(true).add(new InputMaskBehavior(InputMaskBehavior.Masks.SHORT_DATE)),
                new HashMap<String, String>() {{
                    put("data-date-format", "mm/yyyy");
                    put("data-date-start-view", "months");
                    put("data-date-min-view-mode", "months");
                    put("data-date-start-date", "01/1900");
                    put("data-date-end-date", "12/2999");
                }});
        return comp;
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        if ((model != null) && (model.getObject() != null)) {
            SInstance instancia = model.getObject();
            if (instancia.getValue() instanceof YearMonth) {
                YearMonth ym = (YearMonth) instancia.getValue();
                return String.format("%02d/%04d", ym.getMonthValue(), ym.getYear());
            }
        }
        return StringUtils.EMPTY;
    }
}
