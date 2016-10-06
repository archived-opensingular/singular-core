/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.examples;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import br.net.mirante.singular.form.type.core.*;
import br.net.mirante.singular.form.view.*;

public class SPackagePeticaoGGTOX extends SPackage {

    public static final String PACOTE = "mform.peticao";

    public SPackagePeticaoGGTOX() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.createType(STypePeticaoGGTOX.class);
    }
}

