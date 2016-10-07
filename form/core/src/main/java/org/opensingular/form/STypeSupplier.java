/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form;

import org.opensingular.form.type.basic.SPackageBasic;

import java.util.function.Supplier;

@SInfoType(name = "STypeSupplier", spackage = SPackageBasic.class)
public class STypeSupplier<V> extends STypeCode<SISupplier<V>, Supplier<V>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeSupplier() {
        super((Class) SISupplier.class, (Class) Supplier.class);
    }
}
