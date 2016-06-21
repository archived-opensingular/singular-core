/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.habilitacao;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeLocalFabricacao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeDate;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.transformer.Value;

public class SPackageHabilitacaoEmpresa extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada.habilitacao";

    public SPackageHabilitacaoEmpresa() {
        super(PACOTE);
    }


    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.loadPackage(SPackageNotificacaoSimplificada.class);
        pb.createType(STypeHabilitacaoEmpresa.class);
    }

}

