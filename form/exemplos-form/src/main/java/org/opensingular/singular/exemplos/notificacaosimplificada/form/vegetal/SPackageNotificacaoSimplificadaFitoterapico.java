/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.exemplos.notificacaosimplificada.form.vegetal;

import org.opensingular.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SInfoPackage;
import org.opensingular.singular.form.SPackage;

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

