/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco;

import br.net.mirante.singular.exemplos.notificacaosimplificada.common.STypeSubstanciaPopulator;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeFormaFarmaceutica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeLinhaProducao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.exemplos.util.TripleConverter;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;
import org.apache.commons.lang3.tuple.Triple;

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

