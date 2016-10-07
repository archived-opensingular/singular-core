/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form;

import org.opensingular.form.type.core.SPackageCore;

import java.util.function.Predicate;

@SInfoType(name = "STypePredicate", spackage = SPackageCore.class)
public class STypePredicate extends STypeCode<SIPredicate, Predicate<SInstance>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypePredicate() {
        super((Class) SIPredicate.class, (Class) Predicate.class);
    }
}
