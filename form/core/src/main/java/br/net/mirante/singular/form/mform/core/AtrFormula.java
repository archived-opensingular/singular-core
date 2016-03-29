/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core;

import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.STranslatorForAttribute;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;

public class AtrFormula extends STranslatorForAttribute {

    public AtrFormula set(Supplier<Object> supplier) {
        getTipo().setAttributeValue(SPackageBasic.ATR_FORMULA, null);
        return this;
    }

}
