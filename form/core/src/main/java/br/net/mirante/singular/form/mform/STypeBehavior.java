/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.function.IBehavior;

@SInfoType(spackage = SPackageBasic.class)
public class STypeBehavior extends STypeCode<SIBehavior, IBehavior<SInstance>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeBehavior() {
        super((Class) SIBehavior.class, (Class) IBehavior.class);
    }
}
