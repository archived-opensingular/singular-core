/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.exemplos.notificacaosimplificada.form;

import org.opensingular.form.exemplos.notificacaosimplificada.form.gas.STypeAcondicionamentoGAS;
import org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado;
import org.opensingular.form.exemplos.notificacaosimplificada.form.vegetal.STypeEnsaioControleQualidade;
import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;

@SInfoPackage(name = SPackageNotificacaoSimplificada.PACOTE)
public class SPackageNotificacaoSimplificada extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada";
    public static final String TIPO          = "NotificacaoSimplificada";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageNotificacaoSimplificada() {
        super(PACOTE);
    }


    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.loadPackage(SPackageVocabularioControlado.class);
        pb.createType(STypeEmpresaPropria.class);
        pb.createType(STypeEmpresaInternacional.class);
        pb.createType(STypeEmpresaTerceirizada.class);
        pb.createType(STypeLocalFabricacao.class);
        pb.createType(STypeAcondicionamentoGAS.class);
        pb.createType(STypeAcondicionamento.class);
        pb.createType(STypeFarmacopeiaReferencia.class);
        pb.createType(STypeEnsaioControleQualidade.class);
    }

}

