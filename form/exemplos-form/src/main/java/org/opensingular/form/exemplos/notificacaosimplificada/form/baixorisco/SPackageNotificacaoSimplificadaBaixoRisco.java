/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.exemplos.notificacaosimplificada.form.baixorisco;

import org.opensingular.form.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;

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

