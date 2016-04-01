/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.*;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeEmpresaInternacional;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeEmpresaPropria;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeEmpresaTerceirizada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeLocalFabricacao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeFormaFarmaceutica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeLinhaProducao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Triple;

@SInfoType(spackage = SPackageNotificacaoSimplificadaBaixoRisco.class)
public class SPackageNotificacaoSimplificadaBaixoRisco extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada.baixorisco";
    public static final String TIPO          = "MedicamentoBaixoRisco";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageNotificacaoSimplificadaBaixoRisco() {
        super(PACOTE);
    }


    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.getDictionary().loadPackage(SPackageNotificacaoSimplificada.class);

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtrBasic().displayString("${nomeComercialMedicamento} - ${configuracaoLinhaProducao.descricao} (<#list substancias as c>${c.substancia.descricao} ${c.concentracao.descricao}<#sep>, </#sep></#list>) ");
        notificacaoSimplificada.asAtrBasic().label("Notificação Simplificada - Medicamento de Baixo Risco");

        final STypeLinhaProducao linhaProducao          = notificacaoSimplificada.addField("linhaProducao", STypeLinhaProducao.class);



        final STypeComposite<?> configuracaoLinhaProducao     = notificacaoSimplificada.addFieldComposite("configuracaoLinhaProducao");
        STypeSimple             idConfiguracaoLinhaProducao   = configuracaoLinhaProducao.addFieldInteger("id");
        STypeSimple             idLinhaProducaoConfiguracao   = configuracaoLinhaProducao.addFieldInteger("idLinhaProducao");
        STypeSimple             descConfiguracaoLinhaProducao = configuracaoLinhaProducao.addFieldString("descricao");

        configuracaoLinhaProducao
                .asAtrBasic()
                .label("Descrição")
                .required()
                .dependsOn(linhaProducao)
                .visible(i -> Value.notNull(i, linhaProducao.id));
        configuracaoLinhaProducao
                .withSelectView()
                .withSelectionFromProvider(descConfiguracaoLinhaProducao, (optionsInstance, lb) -> {
                    Integer id = (Integer) Value.of(optionsInstance, linhaProducao.id);
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
                .withMiniumSizeOf(1)
                .withView(SViewListByTable::new)
                .asAtrBasic()
                .label("Substâncias")
                .dependsOn(configuracaoLinhaProducao)
                .visible(i -> Value.notNull(i, idConfiguracaoLinhaProducao));

        final STypeComposite<?> concentracaoSubstancia                = substancias.getElementsType();
        final STypeComposite<?> substancia                            = concentracaoSubstancia.addFieldComposite("substancia");
        STypeSimple             idSubstancia                          = substancia.addFieldInteger("id");
        STypeSimple             idConfiguracaoLinhaProducaoSubstancia = substancia.addFieldInteger("configuracaoLinhaProducao");
        STypeSimple             substanciaDescricao                   = substancia.addFieldString("descricao");
        substancia
                .asAtrBasic()
                .label("Substância")
                .required()
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
                .required()
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
                .required()
                .label("Nome Comercial do Medicamento")
                .asAtrBootstrap()
                .colPreference(8);

        final STypeFormaFarmaceutica formaFarmaceutica = notificacaoSimplificada.addField("formaFarmaceutica", STypeFormaFarmaceutica.class);


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



        final STypeAttachmentList layoutsRotulagem = notificacaoSimplificada
                .addFieldListOfAttachment("layoutsRotulagem", "layoutRotulagem");
        layoutsRotulagem
                .asAtrBasic()
                .required()
                .label("Layout rotulagem");

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

