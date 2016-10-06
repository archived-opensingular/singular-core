/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.exemplos.notificacaosimplificada.form.dinamizado;

import org.opensingular.form.exemplos.notificacaosimplificada.form.baixorisco.SPackageNotificacaoSimplificadaBaixoRisco;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;

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

