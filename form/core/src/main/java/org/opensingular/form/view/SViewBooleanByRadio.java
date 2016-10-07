/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.view;

import org.opensingular.form.SType;
import org.opensingular.form.type.core.STypeBoolean;

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
