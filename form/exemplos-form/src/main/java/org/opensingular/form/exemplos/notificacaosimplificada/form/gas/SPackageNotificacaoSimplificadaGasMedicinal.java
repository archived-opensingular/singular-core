/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.exemplos.notificacaosimplificada.form.gas;

import org.opensingular.form.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;

@SInfoPackage(name = SPackageNotificacaoSimplificadaGasMedicinal.PACOTE)
public class SPackageNotificacaoSimplificadaGasMedicinal extends SPackage {

    public static final String PACOTE = "mform.peticao.notificacaosimplificada.gas";

    public SPackageNotificacaoSimplificadaGasMedicinal() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.loadPackage(SPackageNotificacaoSimplificada.class);
        pb.createType(STypeNotificacaoSimplificadaGasMedicinal.class);
    }
}

