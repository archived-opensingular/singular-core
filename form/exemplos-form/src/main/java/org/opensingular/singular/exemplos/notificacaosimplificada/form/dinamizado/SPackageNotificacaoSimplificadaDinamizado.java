/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.exemplos.notificacaosimplificada.form.dinamizado;

import org.opensingular.singular.exemplos.notificacaosimplificada.form.baixorisco.SPackageNotificacaoSimplificadaBaixoRisco;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SInfoPackage;
import org.opensingular.singular.form.SPackage;

@SInfoPackage(name = SPackageNotificacaoSimplificadaDinamizado.PACOTE)
public class SPackageNotificacaoSimplificadaDinamizado extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada.dinamizado";

    public SPackageNotificacaoSimplificadaDinamizado() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.loadPackage(SPackageNotificacaoSimplificadaBaixoRisco.class);
        pb.createType(STypeLinhaProducaoDinamizado.class);
        pb.createType(STypeNotificacaoSimplificadaDinamizado.class);
    }
}

