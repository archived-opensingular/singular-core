/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vegetal;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SPackage;

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

