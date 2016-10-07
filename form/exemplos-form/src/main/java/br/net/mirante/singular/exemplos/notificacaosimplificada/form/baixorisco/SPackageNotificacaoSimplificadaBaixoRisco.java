/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SPackage;

@SInfoPackage(name = SPackageNotificacaoSimplificadaBaixoRisco.PACOTE)
public class SPackageNotificacaoSimplificadaBaixoRisco extends SPackage {

    public static final String PACOTE = "mform.peticao.notificacaosimplificada.baixorisco";

    public static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageNotificacaoSimplificadaBaixoRisco() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.loadPackage(SPackageNotificacaoSimplificada.class);
        pb.createType(STypeNotificacaoSimplificadaBaixoRisco.class);
    }
}

