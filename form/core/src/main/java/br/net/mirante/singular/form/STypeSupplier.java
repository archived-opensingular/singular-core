/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

import br.net.mirante.singular.form.type.basic.SPackageBasic;

import java.util.function.Supplier;

@SInfoType(name = "STypeSupplier", spackage = SPackageBasic.class)
public class STypeSupplier<V> extends STypeCode<SISupplier<V>, Supplier<V>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeSupplier() {
        super((Class) SISupplier.class, (Class) Supplier.class);
    }
}
