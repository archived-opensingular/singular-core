/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.exemplos.notificacaosimplificada.form.vegetal;

import org.opensingular.form.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;

@SInfoPackage(name = SPackageNotificacaoSimplificadaFitoterapico.PACOTE)
public class SPackageNotificacaoSimplificadaFitoterapico extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada.fitoterapico";

    public SPackageNotificacaoSimplificadaFitoterapico() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.loadPackage(SPackageNotificacaoSimplificada.class);
        pb.createType(STypeNotificacaoSimplificadaFitoterapico.class);
    }

}

