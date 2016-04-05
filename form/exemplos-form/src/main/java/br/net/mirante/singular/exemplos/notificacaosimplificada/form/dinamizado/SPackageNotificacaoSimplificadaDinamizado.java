/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.dinamizado;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.FormaFarmaceuticaBasica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco.SPackageNotificacaoSimplificadaBaixoRisco;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeCategoriaRegulatoria;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.core.SIInteger;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SInfoType(spackage = SPackageNotificacaoSimplificadaDinamizado.class)
public class SPackageNotificacaoSimplificadaDinamizado extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada.dinamizado";
    public static final String TIPO          = "MedicamentoDinamizado";
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
        pb.createType(STypeLinhaProducaoDinamizado.class);

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtrBasic().label("Notificação Simplificada - Medicamento Dinamizado");
        notificacaoSimplificada.asAtrBasic().displayString("${nomeComercialMedicamento} - ${linhaProducao.descricao} (<#list formulasHomeopaticas as c>${c.descricaoDinamizada.descricao} ${c.diluicao.descricao}<#sep>, </#sep></#list>) ");

        notificacaoSimplificada.addField("classe", STypeCategoriaRegulatoria.class);


        final STypeLinhaProducaoDinamizado linhaProducao = notificacaoSimplificada.addField("linhaProducao", STypeLinhaProducaoDinamizado.class);

        final STypeList<STypeComposite<SIComposite>, SIComposite> formulasHomeopaticas = notificacaoSimplificada.addFieldListOfComposite("formulasHomeopaticas", "formulaHomeopatica");
        formulasHomeopaticas
                .withMiniumSizeOf(1)
                .withView(SViewListByTable::new)
                .asAtrBasic()
                .dependsOn(linhaProducao)
                .visible(i -> Value.notNull(i, linhaProducao.id))
                .label("Descrição");

        final STypeComposite<?> formulaHomeopatica                             = formulasHomeopaticas.getElementsType();
        final STypeComposite<?> descricaoDinamizada                            = formulaHomeopatica.addFieldComposite("descricaoDinamizada");
        STypeInteger            idDescricaoDinamizada                          = descricaoDinamizada.addFieldInteger("id");
        STypeSimple             idConfiguracaoLinhaProducaoDescricaoDinamizada = descricaoDinamizada.addFieldInteger("configuracaoLinhaProducao");
        STypeSimple             descricaoDescricaoDinamizada                   = descricaoDinamizada.addFieldString("descricao");
        descricaoDinamizada
                .asAtrBasic()
                .label("Descrição")
                .required()
                .asAtrBootstrap()
                .colPreference(6);
        descricaoDinamizada
                .withSelectView()
                .withSelectionFromProvider(descricaoDescricaoDinamizada, (optionsInstance, lb) -> {
                    for (Triple p : dominioService(optionsInstance).descricoesHomeopaticas(Value.of(optionsInstance, linhaProducao.id))) {
                        lb
                                .add()
                                .set(idDescricaoDinamizada, p.getLeft())
                                .set(idConfiguracaoLinhaProducaoDescricaoDinamizada, p.getMiddle())
                                .set(descricaoDescricaoDinamizada, p.getRight());
                    }
                });

        final STypeComposite formaFarmaceutica = notificacaoSimplificada.addFieldComposite("formaFarmaceutica");

        {

            final STypeInteger id        = formaFarmaceutica.addFieldInteger("id");
            final STypeString  descricao = formaFarmaceutica.addFieldString("descricao");

            formaFarmaceutica
                    .asAtrBasic()
                    .label("Forma farmacêutica")
                    .required()
                    .dependsOn(descricaoDinamizada)
                    .visible(i -> {
                        final SIList<SIComposite> list = i.findNearest(formulasHomeopaticas).orElse(null);
                        return !(list == null || list.isEmpty()) && list.stream()
                                .map(SIComposite::getChildren)
                                .flatMap(Collection::stream)
                                .map(ins -> ins.findNearest(descricaoDinamizada))
                                .filter(ins -> ins.isPresent() && Value.notNull(ins.get(), idDescricaoDinamizada))
                                .findFirst().isPresent();
                    })
                    .asAtrBootstrap()
                    .colPreference(4);
            formaFarmaceutica.setView(SViewAutoComplete::new);
            formaFarmaceutica.withSelectionFromProvider(descricao, (ins, filter) -> {
                final SIList<?>           list     = ins.getType().newList();
                final SIList<SIComposite> formulas = ins.findNearest(formulasHomeopaticas).orElse(null);
                if (formulas != null) {
                    final List<Integer> ids = formulas.stream()
                            .flatMap(f -> f.getChildren().stream())
                            .map(i -> i.findNearest(idDescricaoDinamizada))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .map(SIInteger::getValue)
                            .collect(Collectors.toList());
                    for (FormaFarmaceuticaBasica lc : dominioService(ins).formasFarmaceuticasDinamizadas(ids, filter)) {
                        final SIComposite c = (SIComposite) list.addNew();
                        c.setValue(id, lc.getId());
                        c.setValue(descricao, lc.getDescricao());
                    }
                }
                return list;
            });

        }

        final STypeComposite<?> diluicao                 = formulaHomeopatica.addFieldComposite("diluicao");
        final SType<?>          idConcentracacao         = diluicao.addFieldInteger("id");
        final STypeSimple       idSubstanciaConcentracao = diluicao.addFieldInteger("idSubstancia");
        final STypeSimple       descConcentracao         = diluicao.addFieldString("descricao");
        diluicao
                .asAtrBasic()
                .required()
                .label("Faixa de Diluição / Potência")
                .dependsOn(descricaoDinamizada)
                .asAtrBootstrap()
                .colPreference(6);
        diluicao
                .withSelectView()
                .withSelectionFromProvider(descConcentracao, (optionsInstance, lb) -> {
                    Integer id = (Integer) Value.of(optionsInstance, idDescricaoDinamizada);
                    for (Triple p : dominioService(optionsInstance).diluicoes(id)) {
                        lb
                                .add()
                                .set(idConcentracacao, p.getLeft())
                                .set(idSubstanciaConcentracao, p.getMiddle())
                                .set(descConcentracao, p.getRight());
                    }
                });

        final STypeList<STypeAcondicionamento, SIComposite> acondicionamentos =
                notificacaoSimplificada.addFieldListOf("acondicionamentos", STypeAcondicionamento.class);
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamentos.getElementsType().embalagemPrimaria.descricao, "Embalagem primária")
                        .col(acondicionamentos.getElementsType().embalagemSecundaria.descricao, "Embalagem secundária")
                        .col(acondicionamentos.getElementsType().quantidade)
                        .col(acondicionamentos.getElementsType().unidadeMedida.sigla, "Unidade de medida")
                        .col(acondicionamentos.getElementsType().estudosEstabilidade, "Estudo de estabilidade")
                        .col(acondicionamentos.getElementsType().prazoValidade))
                .asAtrBasic().label("Acondicionamento");

        final STypeString nomeComercial = notificacaoSimplificada.addFieldString("nomeComercialMedicamento");
        nomeComercial
                .asAtrBasic()
                .required()
                .label("Nome Comercial")
                .asAtrBootstrap()
                .colPreference(4);

        final STypeAttachmentList layoutsBula =
                notificacaoSimplificada.addFieldListOfAttachment("layoutsfolheto", "layoutfolheto");
        layoutsBula
                .asAtrBasic()
                .required()
                .label("Layout folheto");

        final STypeAttachmentList indicacoesPropostas =
                notificacaoSimplificada.addFieldListOfAttachment("indicacoesPropostas", "indicacaoProposta");
        indicacoesPropostas
                .asAtrBasic()
                .required()
                .label("Referências das indicações propostas");

    }

}

