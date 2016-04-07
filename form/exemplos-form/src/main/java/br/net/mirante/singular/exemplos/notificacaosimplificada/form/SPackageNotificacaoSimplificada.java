/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.FormaFarmaceuticaBasica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.LinhaCbpf;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vegetal.STypeEnsaioControleQualidade;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeAttachmentList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Triple;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
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
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.getDictionary().loadPackage(SPackageVocabularioControlado.class);
        pb.createType(STypeEmpresaPropria.class);
        pb.createType(STypeEmpresaInternacional.class);
        pb.createType(STypeEmpresaTerceirizada.class);
        pb.createType(STypeLocalFabricacao.class);
        pb.createType(STypeAcondicionamento.class);
        pb.createType(STypeFarmacopeiaReferencia.class);
        pb.createType(STypeEnsaioControleQualidade.class);
    }

}

