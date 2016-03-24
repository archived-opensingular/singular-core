/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimpliciada.baixocusto;

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
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySearchModal;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

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
        {
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
        }
    }

}

