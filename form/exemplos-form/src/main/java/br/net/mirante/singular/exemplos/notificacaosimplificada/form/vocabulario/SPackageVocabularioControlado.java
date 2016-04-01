/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeEmpresaInternacional;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeEmpresaPropria;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeEmpresaTerceirizada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeLocalFabricacao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class SPackageVocabularioControlado extends SPackage {

    public static final String PACOTE        = "mform.peticao.anvisa.dominio";
    public static final String TIPO          = "VocabularioControlado";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageVocabularioControlado() {
        super(PACOTE);
    }


    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.createType(STypeEmbalagemPrimaria.class);
        pb.createType(STypeEmbalagemSecundaria.class);
    }

}

