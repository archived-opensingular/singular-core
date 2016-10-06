/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form;

import org.opensingular.singular.form.type.core.SPackageCore;

import java.util.function.Predicate;

@SInfoType(name = "STypePredicate", spackage = SPackageCore.class)
public class STypePredicate extends STypeCode<SIPredicate, Predicate<SInstance>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypePredicate() {
        super((Class) SIPredicate.class, (Class) Predicate.class);
    }
}
