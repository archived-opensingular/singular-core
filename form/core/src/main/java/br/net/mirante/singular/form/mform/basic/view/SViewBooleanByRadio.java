/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.core.STypeBoolean;

/**
 * View para renderização do {@link STypeBoolean} em forma de input radio.
 */
public class SViewBooleanByRadio extends SViewSelectionByRadio {

    private String labelTrue = "Sim";
    
    private String labelFalse = "Não";
    
    @Override
    public boolean isApplicableFor(SType<?> type) {
        return type instanceof STypeBoolean;
    }

    public String labelTrue() {
        return labelTrue;
    }

    public void labelTrue(String labelTrue) {
        this.labelTrue = labelTrue;
    }

    public String labelFalse() {
        return labelFalse;
    }

    public void labelFalse(String labelFalse) {
        this.labelFalse = labelFalse;
    }
    
    
}
