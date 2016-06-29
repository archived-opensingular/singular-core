/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core;

import br.net.mirante.singular.form.STranslatorForAttribute;
import br.net.mirante.singular.form.type.basic.SPackageBasic;

import java.util.function.Supplier;

public class AtrFormula extends STranslatorForAttribute {

    public AtrFormula set(Supplier<Object> supplier) {
        getTipo().setAttributeValue(SPackageBasic.ATR_FORMULA, null);
        return this;
    }

}
