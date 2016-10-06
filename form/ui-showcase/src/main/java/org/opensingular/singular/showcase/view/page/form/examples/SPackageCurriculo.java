/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.view.page.form.examples;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;

public class SPackageCurriculo extends SPackage {

    public static final String PACOTE         = "mform.exemplo.curriculo";

    public SPackageCurriculo() {
        super("mform.exemplo.curriculo");
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
       pb.createType(STypeCurriculo.class);
    }
}
