/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

import br.net.mirante.singular.form.function.IBehavior;
import br.net.mirante.singular.form.type.basic.SPackageBasic;

@SInfoType(name = "Behavior", spackage = SPackageBasic.class)
public class STypeBehavior extends STypeCode<SIBehavior, IBehavior<SInstance>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeBehavior() {
        super((Class) SIBehavior.class, (Class) IBehavior.class);
    }
}
