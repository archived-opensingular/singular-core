/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.core;

import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.type.basic.SPackageBasic;

import java.util.function.Supplier;

public class AtrFormula extends STranslatorForAttribute {

    public AtrFormula set(Supplier<Object> supplier) {
        getTipo().setAttributeValue(SPackageBasic.ATR_FORMULA, null);
        return this;
    }

}
