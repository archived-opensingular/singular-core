/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.core;

import org.opensingular.form.SIComposite;

public class SIFormula extends SIComposite {

    public void setScipt(String valor) {
        setValue(STypeFormula.CAMPO_SCRIPT, valor);
    }

    public void setSciptJS(String valor) {
        setScipt(valor);
        setTipoScript(STypeFormula.TipoScript.JS);
    }

    private void setTipoScript(STypeFormula.TipoScript t) {
        setValue(STypeFormula.CAMPO_TIPO_SCRIPT, t);
    }

    public String getTipoScript() {
        return getValueString(STypeFormula.CAMPO_TIPO_SCRIPT);
    }

    public STypeFormula.TipoScript getTipoScriptEnum() {
        return getValueEnum(STypeFormula.CAMPO_TIPO_SCRIPT, STypeFormula.TipoScript.class);
    }
}
