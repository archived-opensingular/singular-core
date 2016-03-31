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


        final STypeList<STypeComposite<SIComposite>, SIComposite> acondicionamentos = notificacaoSimplificada.addFieldListOfComposite("acondicionamentos", "acondicionamento");

        STypeComposite<SIComposite> acondicionamento = acondicionamentos.getElementsType();

        final STypeEmbalagemPrimaria   embalagemPrimaria   = acondicionamento.addField("embalagemPrimaria", STypeEmbalagemPrimaria.class);
        final STypeEmbalagemSecundaria embalagemSecundaria = acondicionamento.addField("embalagemSecundaria", STypeEmbalagemSecundaria.class);

        STypeInteger quantidade = acondicionamento.addFieldInteger("quantidade", true);
        quantidade
                .asAtrBootstrap()
                .colPreference(3)
                .asAtrBasic()
                .label("Quantidade");

        STypeComposite<SIComposite> unidadeMedida          = acondicionamento.addFieldComposite("unidadeMedida");
        STypeString                 idUnidadeMedida        = unidadeMedida.addFieldString("id");
        STypeString                 descricaoUnidadeMedida = unidadeMedida.addFieldString("descricao");
        unidadeMedida
                .asAtrBootstrap()
                .colPreference(3)
                .asAtrBasic()
                .label("Unidade de medida")
                .getTipo().setView(SViewAutoComplete::new);
        unidadeMedida.withSelectionFromProvider(descricaoUnidadeMedida, (ins, filter) -> {
            final SIList<?> list = ins.getType().newList();
            for (UnidadeMedida um : dominioService(ins).unidadesMedida(filter)) {
                final SIComposite c = (SIComposite) list.addNew();
                c.setValue(idUnidadeMedida, um.getId());
                c.setValue(descricaoUnidadeMedida, um.getDescricao());
            }
            return list;
        });

        final STypeAttachmentList estudosEstabilidade = acondicionamento.addFieldListOfAttachment("estudosEstabilidade", "estudoEstabilidade");

        estudosEstabilidade.asAtrBasic()
                .label("Estudo de estabilidade")
                .displayString("<#list _inst as c>${c.name}<#sep>, </#sep></#list>");
        {

            STypeAttachment f           = estudosEstabilidade.getElementsType();
            SType<?>        nomeArquivo = (STypeSimple) f.getField(f.FIELD_NAME);
            nomeArquivo.asAtrBasic().label("Nome do Arquivo");
        }

        {
            final STypeAttachmentList layoutsRotulagem = acondicionamento.addFieldListOfAttachment("layoutsRotulagem", "layoutRotulagem");
            layoutsRotulagem.asAtrBasic().label("Layout da rotulagem");

            STypeAttachment f           = layoutsRotulagem.getElementsType();
            SType<?>        nomeArquivo = (STypeSimple) f.getField(f.FIELD_NAME);
            nomeArquivo.asAtrBasic().label("Nome do Arquivo");
        }


        STypeList<STypeComposite<SIComposite>, SIComposite> locaisFabricacao = acondicionamento.addFieldListOfComposite("locaisFabricacao", "localFabricacao");
        STypeComposite<SIComposite>                         localFabricacao  = locaisFabricacao.getElementsType();
        localFabricacao.asAtrBasic().label("Local de Fabricação");

        STypeSimple tipoLocalFabricacao = localFabricacao.addFieldInteger("tipoLocalFabricacao");
        tipoLocalFabricacao
                .asAtrBasic()
                .label("Tipo de local");
        tipoLocalFabricacao
                .withRadioView()
                .withSelection()
                .add(1, "Produção Própria")
                .add(2, "Empresa Internacional")
                .add(3, "Empresa Terceirizada")
                .add(4, "Outro Local de Fabricação");


        final STypeEmpresaPropria empresaPropria = localFabricacao.addField("empresaPropria", STypeEmpresaPropria.class);

        empresaPropria.asAtrBasic()
                .dependsOn(tipoLocalFabricacao)
                .visivel(i -> Integer.valueOf(1).equals(Value.of(i, tipoLocalFabricacao)));

        final STypeEmpresaInternacional empresaInternacional = localFabricacao.addField("empresaInternacional", STypeEmpresaInternacional.class);

        empresaInternacional
                .asAtrBasic()
                .dependsOn(tipoLocalFabricacao)
                .visivel(i -> Integer.valueOf(2).equals(Value.of(i, tipoLocalFabricacao)));

        final STypeEmpresaTerceirizada empresaTerceirizada = localFabricacao.addField("empresaTerceirizada", STypeEmpresaTerceirizada.class);

        empresaTerceirizada
                .asAtrBasic()
                .dependsOn(tipoLocalFabricacao)
                .visivel(i -> Integer.valueOf(3).equals(Value.of(i, tipoLocalFabricacao)));


        STypeComposite<SIComposite> outroLocalFabricacao = localFabricacao.addFieldComposite("outroLocalFabricacao");

        STypeString idOutroLocalFabricacao          = outroLocalFabricacao.addFieldString("id");
        STypeString razaoSocialOutroLocalFabricacao = outroLocalFabricacao.addFieldString("razaoSocial");
        razaoSocialOutroLocalFabricacao.asAtrBasic().label("Razão Social");
        STypeString enderecoOutroLocalFabricacao = outroLocalFabricacao.addFieldString("endereco");
        outroLocalFabricacao
                .asAtrBasic().label("Outro local de fabricação")
                .dependsOn(tipoLocalFabricacao)
                .visivel(i -> Integer.valueOf(4).equals(Value.of(i, tipoLocalFabricacao)));

        outroLocalFabricacao
                .withSelectionFromProvider(razaoSocialOutroLocalFabricacao, (optionsInstance, lb) -> {
                    for (Triple p : dominioService(optionsInstance).outroLocalFabricacao()) {
                        lb
                                .add()
                                .set(idOutroLocalFabricacao, p.getLeft())
                                .set(razaoSocialOutroLocalFabricacao, p.getMiddle())
                                .set(enderecoOutroLocalFabricacao, p.getRight());
                    }
                })
                .asAtrBasic().label("Outro local de fabricação")
                .getTipo().setView(SViewAutoComplete::new);

        locaisFabricacao
                .withView(new SViewListByMasterDetail()
                        .col(tipoLocalFabricacao)
                        .col(localFabricacao, i -> {
                            String label = String.valueOf(Optional.ofNullable(Value.of(i, "outroLocalFabricacao.razaoSocial")).orElse(""));
                            label += String.valueOf(Optional.ofNullable(Value.of(i, "empresaTerceirizada.empresa.razaoSocial")).orElse(""));
                            label += String.valueOf(Optional.ofNullable(Value.of(i, "empresaInternacional.razaoSocial")).orElse(""));
                            label += String.valueOf(Optional.ofNullable(Value.of(i, "empresaPropria.razaoSocial")).orElse(""));
                            return label;
                        }).col(empresaTerceirizada.etapasFabricacao()))
                .asAtrBasic().label("Local de fabricação");

        STypeInteger prazoValidade = acondicionamento.addFieldInteger("prazoValidade", true);
        prazoValidade.asAtrBasic().label("Prazo de validade (meses)");

        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(embalagemPrimaria.getDescricaoEmbalagemPrimaria(), "Embalagem primária")
                        .col(embalagemSecundaria.getDescricaoEmbalagemSecundaria(), "Embalagem secundária")
                        .col(quantidade)
                        .col(descricaoUnidadeMedida)
                        .col(estudosEstabilidade, "Estudo de estabilidade")
                        .col(prazoValidade))
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

