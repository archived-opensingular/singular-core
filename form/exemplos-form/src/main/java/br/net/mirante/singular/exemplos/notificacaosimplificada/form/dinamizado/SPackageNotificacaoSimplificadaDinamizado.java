/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.dinamizado;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.FormaFarmaceuticaBasica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco.SPackageNotificacaoSimplificadaBaixoRisco;
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
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.core.STypeString;

@SInfoType(spackage = SPackageNotificacaoSimplificadaDinamizado.class)
public class SPackageNotificacaoSimplificadaDinamizado extends SPackage {

    public static final String PACOTE = "mform.peticao.notificacaosimplificada.dinamizado";
    public static final String TIPO = "MedicamentoDinamizado";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public SPackageNotificacaoSimplificadaDinamizado() {
        super(PACOTE);
    }

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.getDictionary().loadPackage(SPackageNotificacaoSimplificadaBaixoRisco.class);


        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtrBasic().label("Notificação Simplificada - Medicamento Dinamizado");

        STypeString nomeComercial = notificacaoSimplificada.addFieldString("nomeComercialMedicamento");
        nomeComercial
                .asAtrBasic()
                .required()
                .label("Nome Comercial do Medicamento")
                .asAtrBootstrap()
                .colPreference(8);

        final STypeComposite<?> formaFarmaceutica = notificacaoSimplificada.addFieldComposite("formaFarmaceutica");
        SType<?> idFormaFormaceutica = formaFarmaceutica.addFieldInteger("id");
        STypeSimple descFormaFormaceutica = formaFarmaceutica.addFieldString("descricao");
        formaFarmaceutica
                .asAtrBasic()
                .required()
                .label("Forma Farmacêutica")
                .asAtrBootstrap()
                .colPreference(4);
        formaFarmaceutica
                .withSelectView()
                .withSelectionFromProvider(descFormaFormaceutica, (ins, filter) -> {
                    final SIList<?> list = ins.getType().newList();
                    for (FormaFarmaceuticaBasica ffb : dominioService(ins).formasFarmaceuticas(filter)) {
                        final SIComposite c = (SIComposite) list.addNew();
                        c.setValue(idFormaFormaceutica, ffb.getId());
                        c.setValue(descFormaFormaceutica, ffb.getDescricao());
                    }
                    return list;
                });


        final STypeList<STypeAcondicionamento, SIComposite> acondicionamentos = notificacaoSimplificada.addFieldListOf("acondicionamentos", STypeAcondicionamento.class);
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamentos.getElementsType().embalagemPrimaria.descricaoEmbalagemPrimaria, "Embalagem primária")
                        .col(acondicionamentos.getElementsType().embalagemSecundaria.descricaoEmbalagemSecundaria, "Embalagem secundária")
                        .col(acondicionamentos.getElementsType().quantidade)
                        .col(acondicionamentos.getElementsType().descricaoUnidadeMedida)
                        .col(acondicionamentos.getElementsType().estudosEstabilidade, "Estudo de estabilidade")
                        .col(acondicionamentos.getElementsType().prazoValidade))
                .asAtrBasic().label("Acondicionamento");


        final STypeAttachmentList layoutsBula = notificacaoSimplificada
                .addFieldListOfAttachment("layoutsBula", "layoutBula");
        layoutsBula
                .asAtrBasic()
                .required()
                .label("Layout bula");


        final STypeAttachmentList layoutsRotulagem = notificacaoSimplificada
                .addFieldListOfAttachment("layoutsRotulagem", "layoutRotulagem");
        layoutsRotulagem
                .asAtrBasic()
                .required()
                .label("Layout rotulagem");

        final STypeAttachmentList indicacoesPropostas = notificacaoSimplificada
                .addFieldListOfAttachment("indicacoesPropostas", "indicacaoProposta");
        indicacoesPropostas
                .asAtrBasic()
                .required()
                .label("Referências das indicações propostas");


        // config tabs
        SViewTab tabbed = notificacaoSimplificada.setView(SViewTab::new);
        tabbed.addTab("medicamento", "Medicamento")
//                .add(linhaProducao)
//                .add(configuracaoLinhaProducao)
//                .add(substancias)
                .add(formaFarmaceutica)
                .add(nomeComercial);
        tabbed.addTab("acondicionamento", "Acondicionamento")
                .add(acondicionamentos);
        tabbed.addTab("layoutsRotulagem", "Rotulagem")
                .add(layoutsBula)
                .add(layoutsRotulagem)
                .add(indicacoesPropostas);

    }

}

