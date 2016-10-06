/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.type.core.*;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;

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
