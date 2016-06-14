/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.dinamizado;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.FormaFarmaceuticaBasica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco.SPackageNotificacaoSimplificadaBaixoRisco;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeCategoriaRegulatoria;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.exemplos.util.PairConverter;
import br.net.mirante.singular.exemplos.util.TripleConverter;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeAttachmentList;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.converter.ValueToSICompositeConverter;
import br.net.mirante.singular.form.provider.Config;
import br.net.mirante.singular.form.provider.FilteredPagedProvider;
import br.net.mirante.singular.form.provider.ProviderContext;
import br.net.mirante.singular.form.type.core.SIInteger;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.view.SViewByBlock;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.view.SViewListByTable;
import br.net.mirante.singular.form.view.SViewSearchModal;
import br.net.mirante.singular.form.view.SViewTextArea;

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
    protected void onLoadPackage(PackageBuilder pb) {

        pb.loadPackage(SPackageNotificacaoSimplificadaBaixoRisco.class);
        pb.createType(STypeLinhaProducaoDinamizado.class);

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtr().label("Medicamento Dinamizado");
        notificacaoSimplificada.asAtr().displayString("${nomeComercial} - ${linhaProducao.descricao} (<#list formulasHomeopaticas as c>${c.descricaoDinamizada.descricao}<#sep>, </#sep></#list>) ");

        addNomeComercial(notificacaoSimplificada);
        addCaracteristicas(notificacaoSimplificada);
        addAcondicionamentos(notificacaoSimplificada);
        addListaFormulaProduto(notificacaoSimplificada);
        addIndicacaoTerapeutica(notificacaoSimplificada);
        addListaReferencias(notificacaoSimplificada);
        addListaLayoutFolheto(notificacaoSimplificada);


        notificacaoSimplificada.withView(new SViewByBlock("Medicamento Dinamizado"), view -> {
            view
                    .newBlock()
                    .add("nomeComercial")
                    .add("classe")
                    .add("linhaProducao")
                    .add("formulasHomeopaticas")

                    .newBlock("Acondicionamento")
                    .add("listaAcondicionamento")

                    .newBlock("Fórmula do produto")
                    .add("listaFormulaProduto")
//                    .add("OutraFormula")

                    .newBlock("Indicação Terapeutica")
                    .add("indicacaoTerapeutica")
                    .add("informarOutraIndicacaoTerapeutica")
                    .add("outraIndicacaoTerapeutica")
                    .add("listaReferencias")

                    .newBlock("Layout folheto")
                    .add("listaLayoutFolheto");
        });
    }

    private void addCaracteristicas(STypeComposite<?> notificacaoSimplificada) {

        final STypeCategoriaRegulatoria                           classe               = notificacaoSimplificada.addField("classe", STypeCategoriaRegulatoria.class);
        final STypeLinhaProducaoDinamizado                        linhaProducao        = notificacaoSimplificada.addField("linhaProducao", STypeLinhaProducaoDinamizado.class);
        final STypeList<STypeComposite<SIComposite>, SIComposite> formulasHomeopaticas = notificacaoSimplificada.addFieldListOfComposite("formulasHomeopaticas", "formulaHomeopatica");

        formulasHomeopaticas
                .withMiniumSizeOf(1)
                .withView(SViewListByTable::new)
                .asAtr()
                .label("Insumo Ativo")
                .dependsOn(linhaProducao)
                .visible(i -> Value.notNull(i, linhaProducao.id));

        final STypeComposite<?>           formulaHomeopatica                             = formulasHomeopaticas.getElementsType();
        final STypeComposite<SIComposite> descricaoDinamizada                            = formulaHomeopatica.addFieldComposite("descricaoDinamizada");
        final STypeInteger                idDescricaoDinamizada                          = descricaoDinamizada.addFieldInteger("id");
        final STypeInteger                idConfiguracaoLinhaProducaoDescricaoDinamizada = descricaoDinamizada.addFieldInteger("configuracaoLinhaProducao");
        final STypeString                 descricaoDescricaoDinamizada                   = descricaoDinamizada.addFieldString("descricao");

        descricaoDinamizada
                .asAtr()
                .label("Descrição")
                .required()
                .asAtrBootstrap()
                .colPreference(5);

        descricaoDinamizada.autocompleteOf(Triple.class)
                .id("${left}")
                .display("${right}")
                .converter(new TripleConverter(idDescricaoDinamizada, idConfiguracaoLinhaProducaoDescricaoDinamizada, descricaoDescricaoDinamizada))
                .filteredProvider((ins, query) -> dominioService(ins).descricoesHomeopaticas(Value.of(ins, linhaProducao.id)));

        final STypeInteger potencia = formulaHomeopatica.addFieldInteger("potencia");
        potencia
                .asAtr().label("Potência")
                .asAtrBootstrap().colPreference(3).newRow();

        final STypeString escala = formulaHomeopatica.addFieldString("escala");
        escala
                .asAtr().label("Escala")
                .asAtrBootstrap().colPreference(3);

        potencia.addInstanceValidator(validatable -> {
            Integer       idDescricao = validatable.getInstance().findNearest(descricaoDinamizada).get().findNearest(idDescricaoDinamizada).get().getValue();
            final Integer value       = validatable.getInstance().getValue();
            final Triple  t           = dominioService(validatable.getInstance()).diluicao(idDescricao);
            if (t != null) {
                final BigDecimal min   = (BigDecimal) t.getMiddle();
                final BigDecimal max   = (BigDecimal) t.getRight();
                String           faixa = String.format("%s - %s", min, max);
                if (value == null) {

                } else if (BigDecimal.valueOf(value).compareTo(min) < 0) {
                    validatable.error(String.format("O valor está fora da faixa de concentração: %s", faixa));
                } else if (BigDecimal.valueOf(value).compareTo(max) > 0) {
                    validatable.error(String.format("O valor está fora da faixa de concentração: %s", faixa));

                }
            }
        });

        escala
                .withSelectView()
                .selectionOf("CH", "DH");

        final STypeComposite formaFarmaceutica = notificacaoSimplificada.addFieldComposite("formaFarmaceutica");

        {

            final STypeInteger id        = formaFarmaceutica.addFieldInteger("id");
            final STypeString  descricao = formaFarmaceutica.addFieldString("descricao");

            formaFarmaceutica
                    .asAtr()
                    .label("Forma farmacêutica")
                    .required()
                    .dependsOn(descricaoDinamizada)
                    .visible(i -> {
                        final SIList<SIComposite> list = i.findNearest(formulasHomeopaticas).orElse(null);
                        final boolean hasIdDescricaoDinamizadaPresent = list.stream()
                                .map(SIComposite::getChildren)
                                .flatMap(Collection::stream)
                                .map(ins -> ins.findNearest(descricaoDinamizada))
                                .filter(ins -> ins.isPresent() && Value.notNull(ins.get(), idDescricaoDinamizada))
                                .findFirst().isPresent();
                        return !list.isEmpty() && hasIdDescricaoDinamizadaPresent;
                    })
                    .displayString("${descricao}")
                    .asAtrBootstrap()
                    .colPreference(12);
            formaFarmaceutica.withView(new SViewSearchModal(), (Consumer<SViewSearchModal>) view -> {
                view.title("Buscar formas farmacêutica");
            });
            formaFarmaceutica
                    .asAtrProvider()
                    .filteredProvider(new FormaFarmaceuticaProvider() {
                        @Override
                        List<Integer> getIds(SInstance root) {
                            final SIList<SIComposite> formulas = root.findNearest(formulasHomeopaticas).orElse(null);
                            final List<Integer>       ids      = new ArrayList<>();
                            if (formulas != null) {
                                ids.addAll(formulas.stream()
                                        .flatMap(f -> f.getChildren().stream())
                                        .map(i -> i.findNearest(idDescricaoDinamizada))
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .map(SIInteger::getValue)
                                        .collect(Collectors.toList()));
                            }
                            return ids;
                        }
                    }).converter((ValueToSICompositeConverter<FormaFarmaceuticaBasica>) (ins, desc) -> {
                ins.setValue(id, desc.getId());
                ins.setValue(descricao, desc.getDescricao());
            });
        }
    }

    private void addAcondicionamentos(STypeComposite<?> notificacaoSimplificada) {
        STypeComposite<SIComposite> listaAcondicionamentos = notificacaoSimplificada.addFieldComposite("listaAcondicionamento");
        listaAcondicionamentos
                .asAtrAnnotation().setAnnotated();
        final STypeList<STypeAcondicionamento, SIComposite> acondicionamentos =
                listaAcondicionamentos.addFieldListOf("acondicionamentos", STypeAcondicionamento.class);
        STypeAcondicionamento acondicionamento = acondicionamentos.getElementsType();
        acondicionamentos.withMiniumSizeOf(1);
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamento.embalagemPrimaria, "Embalagem primária")
                        .col(acondicionamento.embalagemSecundaria.descricao, "Embalagem secundária")
                        .col(acondicionamento.quantidade)
                        .col(acondicionamento.unidadeMedida.sigla, "Unidade de medida")
                        .col(acondicionamento.estudosEstabilidade, "Estudo de estabilidade")
                        .col(acondicionamento.prazoValidade));
        acondicionamento.laudosControle.asAtr().visible(true);
    }

    private void addNomeComercial(STypeComposite<?> notificacaoSimplificada) {
        final STypeString nomeComercial = notificacaoSimplificada.addFieldString("nomeComercial");
        nomeComercial
                .asAtr()
                .required()
                .label("Nome do medicamento")
                .asAtrBootstrap()
                .colPreference(12);
    }

    private void addListaFormulaProduto(STypeComposite<?> notificacaoSimplificada) {
        STypeComposite<SIComposite> listaFormulaProduto = notificacaoSimplificada.addFieldComposite("listaFormulaProduto");
        listaFormulaProduto
                .asAtrAnnotation().setAnnotated();
        final STypeAttachmentList formulasProduto =
                listaFormulaProduto.addFieldListOfAttachment("formulasProduto", "formulaProduto");
        formulasProduto
                .withMiniumSizeOf(1);
//        notificacaoSimplificada.addField("OutraFormula", STypeAttachment.class).asAtr().label("Outra Formula");

    }

    private void addListaLayoutFolheto(STypeComposite<?> notificacaoSimplificada) {
        STypeComposite<SIComposite> listaLayoutFolheto = notificacaoSimplificada.addFieldComposite("listaLayoutFolheto");
        listaLayoutFolheto
                .asAtrAnnotation().setAnnotated();
        final STypeAttachmentList layoutsBula =
                listaLayoutFolheto.addFieldListOfAttachment("layoutsfolheto", "layoutfolheto");
        layoutsBula
                .withMiniumSizeOf(1);
    }

    private void addListaReferencias(STypeComposite<?> notificacaoSimplificada) {
        STypeComposite<SIComposite> listaReferencias = notificacaoSimplificada.addFieldComposite("listaReferencias");
        listaReferencias
                .asAtrAnnotation().setAnnotated();
        final STypeAttachmentList indicacoesPropostas =
                listaReferencias.addFieldListOfAttachment("indicacoesPropostas", "indicacaoProposta");
        indicacoesPropostas
                .asAtr().label("Referências da indicação proposta");
        indicacoesPropostas
                .withMiniumSizeOf(1);
    }

    private void addIndicacaoTerapeutica(STypeComposite<?> notificacaoSimplificada) {
        final STypeComposite<SIComposite> indicacaoTerapeutica          = notificacaoSimplificada.addFieldComposite("indicacaoTerapeutica");
        final STypeInteger                idIndicacaoTerapeutica        = indicacaoTerapeutica.addFieldInteger("id");
        final STypeSimple                 descricaoIndicacaoTerapeutica = indicacaoTerapeutica.addFieldString("descricao");
        indicacaoTerapeutica
                .asAtr()
                .label("Indicação")
                .required()
                .asAtrBootstrap()
                .colPreference(6);
        indicacaoTerapeutica
                .selectionOf(Pair.class)
                .id(p -> String.valueOf(p.getLeft()))
                .display(p -> String.valueOf(p.getRight()))
                .converter(new PairConverter(idIndicacaoTerapeutica, descricaoIndicacaoTerapeutica))
                .simpleProvider(ins -> dominioService(ins).indicacoesTerapeuticas());

        STypeBoolean informarOutraIndicacaoTerapeutica = notificacaoSimplificada.addFieldBoolean("informarOutraIndicacaoTerapeutica");
        informarOutraIndicacaoTerapeutica
                .asAtr()
                .label("Informar outra indicação terapêutica")

                .asAtrBootstrap().colPreference(12);
        STypeSimple outraIndicacaoTerapeutica = notificacaoSimplificada.addFieldString("outraIndicacaoTerapeutica");
        outraIndicacaoTerapeutica
                .withView(SViewTextArea::new)
                .asAtr()
                .required()
                .maxLength(600)
                .label("Outra indicação terapêutica")
                .dependsOn(informarOutraIndicacaoTerapeutica)
                .enabled(i -> BooleanUtils.isTrue(Value.of(i, informarOutraIndicacaoTerapeutica)));

        indicacaoTerapeutica
                .asAtr()
                .dependsOn(informarOutraIndicacaoTerapeutica)
                .enabled(i -> BooleanUtils.isNotTrue(Value.of(i, informarOutraIndicacaoTerapeutica)));

    }


}

class FormaFarmaceuticaProvider implements FilteredPagedProvider<FormaFarmaceuticaBasica> {

    List<Integer> getIds(SInstance root) {
        return Collections.emptyList();
    }

    @Override
    public void configureProvider(Config cfg) {

        cfg.getFilter().addFieldString("descricao").asAtr().label("Descrição");

        final STypeString conceito = cfg.getFilter().addFieldString("conceito");
        conceito.withTextAreaView();
        conceito.asAtr().label("Conceito");

        cfg.result()
                .addColumn("conceito", "Conceito")
                .addColumn("descricao", "Descrição");
    }

    @Override
    public long getSize(ProviderContext<SInstance> context) {
        return dominioService(context.getInstance())
                .countFormasFarmaceuticasDinamizadas(getIds(context.getInstance()),
                        Value.of(context.getFilterInstance(), "descricao"), Value.of(context.getFilterInstance(), "conceito"));
    }

    @Override
    public List<FormaFarmaceuticaBasica> load(ProviderContext<SInstance> context) {
        return dominioService(context.getInstance())
                .formasFarmaceuticasDinamizadas(getIds(context.getInstance()),
                        Value.of(context.getFilterInstance(), "descricao"),
                        Value.of(context.getFilterInstance(), "conceito"),
                        context.getFirst(), context.getCount());
    }

    private DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

}

