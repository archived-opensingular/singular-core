/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimpliciada.baixocusto;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import br.net.mirante.singular.exemplos.notificacaosimpliciada.NotificacaoSimplificadaProviderUtils;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.SViewListByForm;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySearchModal;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.transformer.Value;

public class SPackageNotificacaoSimplificadaBaixoRisco extends SPackage {

    public static final String PACOTE = "mform.peticao.notificacaosimplificada";
    public static final String TIPO = "MedicamentoBaixoRisco";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public SPackageNotificacaoSimplificadaBaixoRisco() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.as(AtrBasic::new).label("Notificação Simplificada - Medicamento de Baixo Risco");

            final STypeComposite<?> linhaProducao = notificacaoSimplificada.addFieldComposite("linhaProducao");
            STypeSimple idLinhaProducao = linhaProducao.addFieldInteger("id");
            STypeSimple descricaoLinhaProducao = linhaProducao.addFieldString("descricao");

            linhaProducao
                    .asAtrBasic()
                    .label("Linha de Produção");
            linhaProducao.setView(SViewSelectionBySearchModal::new);
            linhaProducao.withSelectionFromProvider(descricaoLinhaProducao, (optionsInstance, lb) -> {
                for (Pair p : NotificacaoSimplificadaProviderUtils.linhasProducao()) {
                    lb
                            .add()
                            .set(idLinhaProducao, p.getKey())
                            .set(descricaoLinhaProducao, p.getValue());
                }
            });


            final STypeComposite<?> configuracaoLinhaProducao = notificacaoSimplificada.addFieldComposite("configuracaoLinhaProducao");
            STypeSimple idConfiguracaoLinhaProducao = configuracaoLinhaProducao.addFieldInteger("id");
            STypeSimple idLinhaProducaoConfiguracao = configuracaoLinhaProducao.addFieldInteger("idLinhaProducao");
            STypeSimple descConfiguracaoLinhaProducao = configuracaoLinhaProducao.addFieldString("descricao");

            configuracaoLinhaProducao
                    .asAtrBasic()
                    .label("Descrição")
                    .dependsOn(linhaProducao)
                    .visivel(i -> Value.notNull(i, idLinhaProducao));
            configuracaoLinhaProducao
                    .withSelectView()
                    .withSelectionFromProvider(descConfiguracaoLinhaProducao, (optionsInstance, lb) -> {
                        Integer id = (Integer) Value.of(optionsInstance, idLinhaProducao);
                        for (Triple p : NotificacaoSimplificadaProviderUtils.configuracoesLinhaProducao(id)) {
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
                    .as(AtrBasic::new)
                    .label("Substâncias")
                    .dependsOn(configuracaoLinhaProducao)
                    .visivel(i -> Value.notNull(i, idConfiguracaoLinhaProducao));

            final STypeComposite<?> concentracaoSubstancia = substancias.getElementsType();
            final STypeComposite<?> substancia = concentracaoSubstancia.addFieldComposite("substancia");
            STypeSimple idSubstancia = substancia.addFieldInteger("id");
            STypeSimple idConfiguracaoLinhaProducaoSubstancia = substancia.addFieldInteger("configuracaoLinhaProducao");
            STypeSimple substanciaDescricao = substancia.addFieldString("descricao");
            substancia
                    .as(AtrBasic::new)
                    .label("Substância")
                    .as(AtrBootstrap::new)
                    .colPreference(6);
            substancia
                    .withSelectView()
                    .withSelectionFromProvider(substanciaDescricao, (optionsInstance, lb) -> {
                        Integer id = (Integer) Value.of(optionsInstance, idConfiguracaoLinhaProducao);
                        for (Triple p : NotificacaoSimplificadaProviderUtils.substancias(id)) {
                            lb
                                    .add()
                                    .set(idSubstancia, p.getLeft())
                                    .set(idConfiguracaoLinhaProducaoSubstancia, p.getMiddle())
                                    .set(substanciaDescricao, p.getRight());
                        }
                    });


            final STypeComposite<?> concentracao = concentracaoSubstancia.addFieldComposite("concentracao");
            SType<?> idConcentracacao = concentracao.addFieldInteger("id");
            STypeSimple idSubstanciaConcentracao = concentracao.addFieldInteger("idSubstancia");
            STypeSimple descConcentracao = concentracao.addFieldString("descricao");
            concentracao
                    .as(AtrBasic::new)
                    .label("Concentração")
                    .dependsOn(substancia)
                    .as(AtrBootstrap::new)
                    .colPreference(6);
            concentracao
                    .withSelectView()
                    .withSelectionFromProvider(substanciaDescricao, (optionsInstance, lb) -> {
                        Integer id = (Integer) Value.of(optionsInstance, idSubstancia);
                        for (Triple p : NotificacaoSimplificadaProviderUtils.concentracoes(id)) {
                            lb
                                    .add()
                                    .set(idConcentracacao, p.getLeft())
                                    .set(idSubstanciaConcentracao, p.getMiddle())
                                    .set(descConcentracao, p.getRight());
                        }
                    });


            STypeString nomeComercial = notificacaoSimplificada.addFieldString("nomeComercialMedicamento");
            nomeComercial
                    .as(AtrBasic::new)
                    .label("Nome Comercial do Medicamento")
                    .as(AtrBootstrap::new)
                    .colPreference(8);

            final STypeComposite<?> formaFarmaceutica = notificacaoSimplificada.addFieldComposite("formaFarmaceutica");
            SType<?> idFormaFormaceutica = formaFarmaceutica.addFieldInteger("id");
            STypeSimple descFormaFormaceutica = formaFarmaceutica.addFieldString("descricao");
            formaFarmaceutica
                    .as(AtrBasic::new)
                    .label("Forma Farmacêutica")
                    .as(AtrBootstrap::new)
                    .colPreference(4);
            formaFarmaceutica
                    .withSelectView()
                    .withSelectionFromProvider(descFormaFormaceutica, (optionsInstance, lb) -> {
                        for (Pair p : NotificacaoSimplificadaProviderUtils.formasFarmaceuticas()) {
                            lb
                                    .add()
                                    .set(idFormaFormaceutica, p.getKey())
                                    .set(descFormaFormaceutica, p.getValue());
                        }
                    });




        final STypeList<STypeComposite<SIComposite>, SIComposite> acondicionamentos = notificacaoSimplificada.addFieldListOfComposite("acondicionamentos", "acondicionamento");

        STypeComposite<SIComposite> acondicionamento = acondicionamentos.getElementsType();

        STypeComposite<SIComposite> embalagemPrimaria = acondicionamento.addFieldComposite("embalagemPrimaria");
        STypeString idEmbalagemPrimaria = embalagemPrimaria.addFieldString("id");
        STypeString descricaoEmbalagemPrimaria = embalagemPrimaria.addFieldString("descricao");
        {
            embalagemPrimaria
                    .as(AtrBootstrap::new)
                    .colPreference(6)
                    .as(AtrBasic::new)
                    .label("Embalagem primária")
                    .getTipo().setView(SViewSelectionBySearchModal::new);
            embalagemPrimaria.withSelectionFromProvider(descricaoEmbalagemPrimaria, (optionsInstance, lb) -> {
                for (Pair p : NotificacaoSimplificadaProviderUtils.embalagensPrimarias()) {
                    lb
                            .add()
                            .set(idEmbalagemPrimaria, p.getKey())
                            .set(descricaoEmbalagemPrimaria, p.getValue());
                }
            });
        }

        STypeComposite<SIComposite> embalagemSecundaria = acondicionamento.addFieldComposite("embalagemSecundaria");
        STypeString idEmbalagemSecundaria = embalagemSecundaria.addFieldString("id");
        STypeString descricaoEmbalagemSecundaria = embalagemSecundaria.addFieldString("descricao");
        {
            embalagemSecundaria
                    .as(AtrBootstrap::new)
                    .colPreference(6)
                    .as(AtrBasic::new)
                    .label("Embalagem secundária")
                    .getTipo().setView(SViewSelectionBySearchModal::new);
            embalagemSecundaria.withSelectionFromProvider(descricaoEmbalagemSecundaria, (optionsInstance, lb) -> {
                for (Pair p : NotificacaoSimplificadaProviderUtils.embalagensSecundarias()) {
                    lb
                            .add()
                            .set(idEmbalagemSecundaria, p.getKey())
                            .set(descricaoEmbalagemSecundaria, p.getValue());
                }
            });

        }
        STypeInteger quantidade = acondicionamento.addFieldInteger("quantidade", true);
        quantidade
                .as(AtrBootstrap::new)
                .colPreference(3)
                .as(AtrBasic::new)
                .label("Quantidade");

        STypeComposite<SIComposite> unidadeMedida = acondicionamento.addFieldComposite("unidadeMedida");
        STypeString idUnidadeMedida = unidadeMedida.addFieldString("id");
        STypeString descricaoUnidadeMedida = unidadeMedida.addFieldString("descricao");
        unidadeMedida
                .as(AtrBootstrap::new)
                .colPreference(3)
                .as(AtrBasic::new)
                .label("Unidade de medida")
                .getTipo().setView(SViewSelectionBySearchModal::new);
        unidadeMedida.withSelectionFromProvider(descricaoUnidadeMedida, (optionsInstance, lb) -> {
            for (Pair p : NotificacaoSimplificadaProviderUtils.unidadesMedida()) {
                lb
                        .add()
                        .set(idUnidadeMedida, p.getKey())
                        .set(descricaoUnidadeMedida, p.getValue());
            }
        });

        STypeList<STypeComposite<SIComposite>, SIComposite> estudosEstabilidade = acondicionamento.addFieldListOfComposite("estudosEstabilidade", "estudoEstabilidade");
        estudosEstabilidade.as(AtrBasic::new)
                .label("Estudo de estabilidade")
                .displayString("<#list _inst as c>${c.arquivo.name}<#sep>, </#sep></#list>");
        STypeComposite<SIComposite> estudoEstabilidade = estudosEstabilidade.getElementsType();
        {

            STypeAttachment f = estudoEstabilidade.addField("arquivo", STypeAttachment.class);
            f.as(AtrBasic.class).label("Informe o caminho do arquivo para o anexo")
                    .as(AtrBootstrap::new).colPreference(9);

            SType<?> nomeArquivo = (STypeSimple) f.getField(f.FIELD_NAME);
            nomeArquivo.as(AtrBasic::new).label("Nome do Arquivo");
            estudosEstabilidade.withView(SViewListByTable::new);
        }

        {
            STypeList<STypeComposite<SIComposite>, SIComposite> layoutsRotulagem = acondicionamento.addFieldListOfComposite("layoutsRotulagem", "layoutRotulagem");
            layoutsRotulagem.as(AtrBasic::new).label("Layout da rotulagem");
            STypeComposite<SIComposite> layoutRotulagem = layoutsRotulagem.getElementsType();

            STypeAttachment f = layoutRotulagem.addField("arquivo", STypeAttachment.class);
            f.as(AtrBasic.class).label("Informe o caminho do arquivo para o anexo")
                    .as(AtrBootstrap::new).colPreference(9);

            SType<?> nomeArquivo = (STypeSimple) f.getField(f.FIELD_NAME);
            nomeArquivo.as(AtrBasic::new).label("Nome do Arquivo");
            layoutsRotulagem.withView(SViewListByTable::new);
        }



        STypeList<STypeComposite<SIComposite>, SIComposite> locaisFabricacao = acondicionamento.addFieldListOfComposite("locaisFabricacao", "localFabricacao");
        STypeComposite<SIComposite> localFabricacao = locaisFabricacao.getElementsType();

        STypeBoolean producaoPropria = localFabricacao.addFieldBoolean("producaoPropria", true);
        producaoPropria.withRadioView();
        producaoPropria.asAtrBasic().label("Produção própria");

        STypeComposite<SIComposite> empresaPropria = localFabricacao.addFieldComposite("empresaPropria");
        empresaPropria.addFieldString("razaoSocial")
                .asAtrBasic().label("Razão Social");
        empresaPropria.addFieldCNPJ("cnpj")
                .asAtrBasic().label("CNPJ");
        empresaPropria.addFieldString("endereco")
                .asAtrBasic().label("Endereço");
        empresaPropria.asAtrBasic()
                .dependsOn(producaoPropria)
                .visivel(i -> BooleanUtils.isTrue(Value.of(i, producaoPropria)));

        STypeList<STypeComposite<SIComposite>, SIComposite> empresasInternacionais = localFabricacao.addFieldListOfComposite("empresasInternacionais", "empresaInternacional");
        STypeComposite<SIComposite> empresaInternacional = empresasInternacionais.getElementsType();

        STypeString idEmpresaInternacional = empresaInternacional.addFieldString("id");
        STypeString razaoSocialInternacional = empresaInternacional.addFieldString("razaoSocial");
        razaoSocialInternacional.asAtrBasic().label("Razão Social");
        STypeString enderecoInternacional = empresaInternacional.addFieldString("endereco");
        empresasInternacionais.asAtrBasic().label("Empresa internacional")
                .dependsOn(producaoPropria)
                .visivel(i -> BooleanUtils.isFalse(Value.of(i, producaoPropria)))
                .getTipo().withView(SViewListByForm::new);

        empresaInternacional
                .withSelectionFromProvider(razaoSocialInternacional, (optionsInstance, lb) -> {
                    for (Triple p : NotificacaoSimplificadaProviderUtils.empresaInternacional()) {
                        lb
                                .add()
                                .set(idEmpresaInternacional, p.getLeft())
                                .set(razaoSocialInternacional, p.getMiddle())
                                .set(enderecoInternacional, p.getRight());
                    }
                })
                .asAtrBasic().label("Empresa internacional")
                .getTipo().setView(SViewSelectionBySearchModal::new);

        STypeList<STypeComposite<SIComposite>, SIComposite> empresasTerceirizadas = localFabricacao.addFieldListOfComposite("empresasTerceirizadas", "empresaTerceirizada");
        STypeComposite<SIComposite> empresaTerceirizada = empresasTerceirizadas.getElementsType();

        STypeList<STypeComposite<SIComposite>, SIComposite> etapasFabricacao = empresaTerceirizada.addFieldListOfComposite("etapasFabricacao", "etapaFabricacao");
        STypeComposite<SIComposite> etapaFabricacao = etapasFabricacao.getElementsType();
        STypeString idEtapaFabricacao = etapaFabricacao.addFieldString("id");
        STypeString descricaoEtapaFabricacao = etapaFabricacao.addFieldString("descricao");

        etapaFabricacao
                .asAtrBasic().label("Etapa de fabricação")
                .getTipo().setView(SViewSelectionBySearchModal::new);
        etapaFabricacao.withSelectionFromProvider(descricaoEtapaFabricacao, (optionsInstance, lb) -> {
            for (Pair p : NotificacaoSimplificadaProviderUtils.etapaFabricacao()) {
                lb
                        .add()
                        .set(idEtapaFabricacao, p.getKey())
                        .set(descricaoEtapaFabricacao, p.getValue());
            }
        });

        etapasFabricacao
                .asAtrBasic().label("Etapa de fabricação")
                .getTipo().withView(SViewListByForm::new);;

        STypeComposite<SIComposite> empresa = empresaTerceirizada.addFieldComposite("empresa");
        STypeString idEmpresa = empresa.addFieldString("id");
        STypeString razaoSocial = empresa.addFieldString("razaoSocial");
        razaoSocial.asAtrBasic().label("Razão Social");
        STypeString endereco = empresa.addFieldString("endereco");
        empresa
                .asAtrBasic().label("Empresa")
                .getTipo().withView(SViewSelectionBySearchModal::new);

        empresa.withSelectionFromProvider(razaoSocial, (optionsInstance, lb) -> {
            for (Triple t : NotificacaoSimplificadaProviderUtils.empresaTerceirizada()) {
                lb
                        .add()
                        .set(idEmpresa, t.getLeft())
                        .set(razaoSocial, t.getMiddle())
                        .set(endereco, t.getRight());
            }
        });

        empresasTerceirizadas
                .asAtrBasic().label("Empresa terceirizada")
                .dependsOn(producaoPropria)
                .visivel(i -> BooleanUtils.isFalse(Value.of(i, producaoPropria)))
                .getTipo().withView(new SViewListByMasterDetail()
                    .col(razaoSocial, "Razão Social")
                    .col(endereco, "Endereço"));

        STypeList<STypeComposite<SIComposite>, SIComposite> outrosLocaisFabricacao = localFabricacao.addFieldListOfComposite("outrosLocaisFabricacao", "outroLocalFabricacao");
        STypeComposite<SIComposite> outroLocalFabricacao = outrosLocaisFabricacao.getElementsType();

        STypeString idOutroLocalFabricacao = outroLocalFabricacao.addFieldString("id");
        STypeString razaoSocialOutroLocalFabricacao = outroLocalFabricacao.addFieldString("razaoSocial");
        razaoSocialOutroLocalFabricacao.asAtrBasic().label("Razão Social");
        STypeString enderecoOutroLocalFabricacao = outroLocalFabricacao.addFieldString("endereco");
        outrosLocaisFabricacao.asAtrBasic().label("Outro local de fabricação")
                .dependsOn(producaoPropria)
                .visivel(i -> BooleanUtils.isFalse(Value.of(i, producaoPropria)))
                .getTipo().withView(SViewListByForm::new);

        outroLocalFabricacao
                .withSelectionFromProvider(razaoSocialOutroLocalFabricacao, (optionsInstance, lb) -> {
                    for (Triple p : NotificacaoSimplificadaProviderUtils.outroLocalFabricacao()) {
                        lb
                                .add()
                                .set(idOutroLocalFabricacao, p.getLeft())
                                .set(razaoSocialOutroLocalFabricacao, p.getMiddle())
                                .set(enderecoOutroLocalFabricacao, p.getRight());
                    }
                })
                .asAtrBasic().label("Outro local de fabricação")
                .getTipo().setView(SViewSelectionBySearchModal::new);

        locaisFabricacao
                .withView(new SViewListByMasterDetail())
//                    .col()
//                    .col())
                .asAtrBasic().label("Local de fabricação");

        STypeInteger prazoValidade = acondicionamento.addFieldInteger("prazoValidade", true);
        prazoValidade.asAtrBasic().label("Prazo de validade (meses)");

        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(descricaoEmbalagemPrimaria, "Embalagem primária")
                        .col(descricaoEmbalagemSecundaria, "Embalagem secundária")
                        .col(estudosEstabilidade, "Estudo de estabilidade")
                        .col(quantidade)
                        .col(prazoValidade))
                .asAtrBasic().label("Acondicionamento");

        final STypeList<STypeComposite<SIComposite>, SIComposite> layoutsRotulagem = notificacaoSimplificada.addFieldListOfComposite("layoutsRotulagem", "layout");
        layoutsRotulagem
                .withView(SViewListByTable::new)
                .as(AtrBasic::new)
                .label("Layouts Rotulagem");
        STypeComposite layout = layoutsRotulagem.getElementsType();
        layout
                .addField("anexo", STypeAttachment.class);




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

