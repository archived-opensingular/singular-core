/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.baixocusto;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.*;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Optional;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class SPackageNotificacaoSimplificada extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada";
    public static final String TIPO          = "MedicamentoBaixoRisco";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageNotificacaoSimplificada() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.createType(STypeEmbalagemPrimaria.class);
        pb.createType(STypeEmbalagemSecundaria.class);
        pb.createType(STypeEmpresaPropria.class);
        pb.createType(STypeEmpresaInternacional.class);
        pb.createType(STypeEmpresaTerceirizada.class);
        pb.createType(STypeLocalFabricacao.class);
        pb.createType(STypeAcondicionamento.class);

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtrBasic().displayString("${nomeComercialMedicamento} - ${configuracaoLinhaProducao.descricao} (<#list substancias as c>${c.substancia.descricao} ${c.concentracao.descricao}<#sep>, </#sep></#list>) ");
        notificacaoSimplificada.asAtrBasic().label("Notificação Simplificada - Medicamento de Baixo Risco");

        final STypeComposite<?> linhaProducao          = notificacaoSimplificada.addFieldComposite("linhaProducao");
        STypeSimple             idLinhaProducao        = linhaProducao.addFieldInteger("id");
        STypeSimple             descricaoLinhaProducao = linhaProducao.addFieldString("descricao");

        linhaProducao
                .asAtrBasic()
                .label("Linha de Produção");
        linhaProducao.setView(SViewAutoComplete::new);
        linhaProducao.withSelectionFromProvider(descricaoLinhaProducao, (ins, filter) -> {
            final SIList<?> list = ins.getType().newList();
            for (LinhaCbpf lc : dominioService(ins).linhasProducao(filter)) {
                final SIComposite c = (SIComposite) list.addNew();
                c.setValue(idLinhaProducao, lc.getId());
                c.setValue(descricaoLinhaProducao, lc.getDescricao());
            }
            return list;
        });


        final STypeComposite<?> configuracaoLinhaProducao     = notificacaoSimplificada.addFieldComposite("configuracaoLinhaProducao");
        STypeSimple             idConfiguracaoLinhaProducao   = configuracaoLinhaProducao.addFieldInteger("id");
        STypeSimple             idLinhaProducaoConfiguracao   = configuracaoLinhaProducao.addFieldInteger("idLinhaProducao");
        STypeSimple             descConfiguracaoLinhaProducao = configuracaoLinhaProducao.addFieldString("descricao");

        configuracaoLinhaProducao
                .asAtrBasic()
                .label("Descrição")
                .dependsOn(linhaProducao)
                .visivel(i -> Value.notNull(i, idLinhaProducao));
        configuracaoLinhaProducao
                .withSelectView()
                .withSelectionFromProvider(descConfiguracaoLinhaProducao, (optionsInstance, lb) -> {
                    Integer id = (Integer) Value.of(optionsInstance, idLinhaProducao);
                    for (Triple p : dominioService(optionsInstance).configuracoesLinhaProducao(id)) {
                        lb
                                .add()
                                .set(idConfiguracaoLinhaProducao, p.getLeft())
                                .set(idLinhaProducaoConfiguracao, p.getMiddle())
                                .set(descConfiguracaoLinhaProducao, p.getRight());
                    }
                });


        final STypeList<STypeComposite<SIComposite>, SIComposite> substancias = notificacaoSimplificada.addFieldListOfComposite("substancias", "concentracaoSubstancia");
        substancias
                .withView(SViewListByTable::new)
                .asAtrBasic()
                .label("Substâncias")
                .dependsOn(configuracaoLinhaProducao)
                .visivel(i -> Value.notNull(i, idConfiguracaoLinhaProducao));

        final STypeComposite<?> concentracaoSubstancia                = substancias.getElementsType();
        final STypeComposite<?> substancia                            = concentracaoSubstancia.addFieldComposite("substancia");
        STypeSimple             idSubstancia                          = substancia.addFieldInteger("id");
        STypeSimple             idConfiguracaoLinhaProducaoSubstancia = substancia.addFieldInteger("configuracaoLinhaProducao");
        STypeSimple             substanciaDescricao                   = substancia.addFieldString("descricao");
        substancia
                .asAtrBasic()
                .label("Substância")
                .asAtrBootstrap()
                .colPreference(6);
        substancia
                .withSelectView()
                .withSelectionFromProvider(substanciaDescricao, (optionsInstance, lb) -> {
                    Integer id = (Integer) Value.of(optionsInstance, idConfiguracaoLinhaProducao);
                    for (Triple p : dominioService(optionsInstance).substancias(id)) {
                        lb
                                .add()
                                .set(idSubstancia, p.getLeft())
                                .set(idConfiguracaoLinhaProducaoSubstancia, p.getMiddle())
                                .set(substanciaDescricao, p.getRight());
                    }
                });


        final STypeComposite<?> concentracao             = concentracaoSubstancia.addFieldComposite("concentracao");
        SType<?>                idConcentracacao         = concentracao.addFieldInteger("id");
        STypeSimple             idSubstanciaConcentracao = concentracao.addFieldInteger("idSubstancia");
        STypeSimple             descConcentracao         = concentracao.addFieldString("descricao");
        concentracao
                .asAtrBasic()
                .label("Concentração")
                .dependsOn(substancia)
                .asAtrBootstrap()
                .colPreference(6);
        concentracao
                .withSelectView()
                .withSelectionFromProvider(substanciaDescricao, (optionsInstance, lb) -> {
                    Integer id = (Integer) Value.of(optionsInstance, idSubstancia);
                    for (Triple p : dominioService(optionsInstance).concentracoes(id)) {
                        lb
                                .add()
                                .set(idConcentracacao, p.getLeft())
                                .set(idSubstanciaConcentracao, p.getMiddle())
                                .set(descConcentracao, p.getRight());
                    }
                });


        STypeString nomeComercial = notificacaoSimplificada.addFieldString("nomeComercialMedicamento");
        nomeComercial
                .asAtrBasic()
                .label("Nome Comercial do Medicamento")
                .asAtrBootstrap()
                .colPreference(8);

        final STypeComposite<?> formaFarmaceutica     = notificacaoSimplificada.addFieldComposite("formaFarmaceutica");
        SType<?>                idFormaFormaceutica   = formaFarmaceutica.addFieldInteger("id");
        STypeSimple             descFormaFormaceutica = formaFarmaceutica.addFieldString("descricao");
        formaFarmaceutica
                .asAtrBasic()
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
                        .col(acondicionamentos.getElementsType().embalagemPrimaria.getDescricaoEmbalagemPrimaria(), "Embalagem primária")
                        .col(acondicionamentos.getElementsType().embalagemSecundaria.getDescricaoEmbalagemSecundaria(), "Embalagem secundária")
                        .col(acondicionamentos.getElementsType().quantidade)
                        .col(acondicionamentos.getElementsType().descricaoUnidadeMedida)
                        .col(acondicionamentos.getElementsType().estudosEstabilidade, "Estudo de estabilidade")
                        .col(acondicionamentos.getElementsType().prazoValidade))
                .asAtrBasic().label("Acondicionamento");



        final STypeAttachmentList layoutsRotulagem = notificacaoSimplificada
                .addFieldListOfAttachment("layoutsRotulagem", "layout");
        layoutsRotulagem
                .asAtrBasic()
                .label("Layouts Rotulagem");

        // config tabs
        SViewTab tabbed = notificacaoSimplificada.setView(SViewTab::new);
        tabbed.addTab("medicamento", "Medicamento")
                .add(linhaProducao)
                .add(configuracaoLinhaProducao)
                .add(substancias)
                .add(formaFarmaceutica)
                .add(nomeComercial);
        tabbed.addTab("acondicionamento", "Acondicionamento")
                .add(acondicionamentos);
        tabbed.addTab("layoutsRotulagem", "Rotulagem")
                .add(layoutsRotulagem);

    }

}

