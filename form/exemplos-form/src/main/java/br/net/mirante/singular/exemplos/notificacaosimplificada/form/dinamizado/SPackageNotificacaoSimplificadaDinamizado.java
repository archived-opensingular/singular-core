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
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.basic.view.SViewSearchModal;
import br.net.mirante.singular.form.mform.basic.view.SViewTextArea;
import br.net.mirante.singular.form.mform.converter.ValueToSICompositeConverter;
import br.net.mirante.singular.form.mform.core.SIInteger;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.FilteredPagedProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
        notificacaoSimplificada.asAtrBasic().displayString("${nomeComercial.nomeComercial} - ${caracteristicas.linhaProducao.descricao} (<#list caracteristicas.formulasHomeopaticas as c>${c.descricaoDinamizada.descricao}<#sep>, </#sep></#list>) ");

        addCaracteristicas(notificacaoSimplificada);
        addNomeComercial(notificacaoSimplificada);
        addAcondicionamentos(notificacaoSimplificada);
        addListaFormulaProduto(notificacaoSimplificada);
        addIndicacaoTerapeutica(notificacaoSimplificada);
        addListaReferencias(notificacaoSimplificada);
        addListaLayoutFolheto(notificacaoSimplificada);

    }

    private void addCaracteristicas(STypeComposite<?> notificacaoSimplificada) {
        STypeComposite<SIComposite> caracteristicas = notificacaoSimplificada.addFieldComposite("caracteristicas");
        caracteristicas.addField("classe", STypeCategoriaRegulatoria.class);

        final STypeLinhaProducaoDinamizado                        linhaProducao        = caracteristicas.addField("linhaProducao", STypeLinhaProducaoDinamizado.class);
        final STypeList<STypeComposite<SIComposite>, SIComposite> formulasHomeopaticas = caracteristicas.addFieldListOfComposite("formulasHomeopaticas", "formulaHomeopatica");

        formulasHomeopaticas
                .withMiniumSizeOf(1)
                .withView(SViewListByTable::new)
                .asAtrBasic()
                .dependsOn(linhaProducao)
                .visible(i -> Value.notNull(i, linhaProducao.id))
                .label("Insumo ativo");

        final STypeComposite<?> formulaHomeopatica                             = formulasHomeopaticas.getElementsType();
        final STypeComposite<?> descricaoDinamizada                            = formulaHomeopatica.addFieldComposite("descricaoDinamizada");
        final STypeInteger      idDescricaoDinamizada                          = descricaoDinamizada.addFieldInteger("id");
        final STypeSimple       idConfiguracaoLinhaProducaoDescricaoDinamizada = descricaoDinamizada.addFieldInteger("configuracaoLinhaProducao");
        final STypeString       descricaoDescricaoDinamizada                   = descricaoDinamizada.addFieldString("descricao");

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


        final STypeInteger potencia = formulaHomeopatica.addFieldInteger("potencia");
        potencia
                .asAtrBasic().label("Potência");

        final STypeString escala = formulaHomeopatica.addFieldString("escala");
        escala
                .asAtrBasic().label("Escala");

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
                .withSelectionOf("CH", "DH");

        final STypeComposite formaFarmaceutica = caracteristicas.addFieldComposite("formaFarmaceutica");

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
                    .displayString("${descricao}")
                    .asAtrBootstrap()
                    .colPreference(12);
            formaFarmaceutica.withView(new SViewSearchModal(), (Consumer<SViewSearchModal>) view -> {
                view.title("Buscar formas farmacêutica");
            });
            formaFarmaceutica
                    .asAtrProvider()
                    .provider(new FormaFarmaceuticaProvider() {
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
                .asAtrBasic().label("Acondicionamentos")
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
                        .col(acondicionamento.prazoValidade))
                .asAtrBasic().label("Acondicionamento");
        acondicionamento.laudosControle.asAtrBasic().visible(true);
    }

    private void addNomeComercial(STypeComposite<?> notificacaoSimplificada) {
        STypeComposite<SIComposite> nomeComercialComposto = notificacaoSimplificada.addFieldComposite("nomeComercial");

        final STypeString nomeComercial = nomeComercialComposto.addFieldString("nomeComercial");
        nomeComercial
                .asAtrBasic()
                .required()
                .label("Nome do medicamento")
                .asAtrBootstrap()
                .colPreference(4);
    }

    private void addListaFormulaProduto(STypeComposite<?> notificacaoSimplificada) {
        STypeComposite<SIComposite> listaFormulaProduto = notificacaoSimplificada.addFieldComposite("listaFormulaProduto");
        listaFormulaProduto
                .asAtrBasic().label("Fórmulas do produto")
                .asAtrAnnotation().setAnnotated();
        final STypeAttachmentList formulasProduto =
                listaFormulaProduto.addFieldListOfAttachment("formulasProduto", "formulaProduto");
        formulasProduto
                .withMiniumSizeOf(1)
                .asAtrBasic()
                .label("Fórmula do produto");
    }

    private void addListaLayoutFolheto(STypeComposite<?> notificacaoSimplificada) {
        STypeComposite<SIComposite> listaLayoutFolheto = notificacaoSimplificada.addFieldComposite("listaLayoutFolheto");
        listaLayoutFolheto
                .asAtrBasic().label("Layout dos folhetos")
                .asAtrAnnotation().setAnnotated();
        final STypeAttachmentList layoutsBula =
                listaLayoutFolheto.addFieldListOfAttachment("layoutsfolheto", "layoutfolheto");
        layoutsBula
                .withMiniumSizeOf(1)
                .asAtrBasic()
                .label("Layout folheto");
    }

    private void addListaReferencias(STypeComposite<?> notificacaoSimplificada) {
        STypeComposite<SIComposite> listaReferencias = notificacaoSimplificada.addFieldComposite("listaReferencias");
        listaReferencias
                .asAtrBasic().label("Referências das indicações propostas")
                .asAtrAnnotation().setAnnotated();
        final STypeAttachmentList indicacoesPropostas =
                listaReferencias.addFieldListOfAttachment("indicacoesPropostas", "indicacaoProposta");
        indicacoesPropostas
                .withMiniumSizeOf(1)
                .asAtrBasic()
                .label("Referências das indicações propostas");
    }

    private void addIndicacaoTerapeutica(STypeComposite<?> notificacaoSimplificada) {
        final STypeComposite<?> indicacaoTerapeutica          = notificacaoSimplificada.addFieldComposite("indicacaoTerapeutica");
        final STypeInteger      idIndicacaoTerapeutica        = indicacaoTerapeutica.addFieldInteger("id");
        final STypeSimple       descricaoIndicacaoTerapeutica = indicacaoTerapeutica.addFieldString("descricao");
        indicacaoTerapeutica
                .asAtrBasic()
                .label("Indicação terapêutica")
                .required()
                .asAtrBootstrap()
                .colPreference(6);
        indicacaoTerapeutica
                .withSelectView()
                .withSelectionFromProvider(descricaoIndicacaoTerapeutica, (optionsInstance, lb) -> {
                    for (Pair p : dominioService(optionsInstance).indicacoesTerapeuticas()) {
                        lb
                                .add()
                                .set(idIndicacaoTerapeutica, p.getLeft())
                                .set(descricaoIndicacaoTerapeutica, p.getRight());
                    }
                });

        STypeBoolean informarOutraIndicacaoTerapeutica = notificacaoSimplificada.addFieldBoolean("informarOutraIndicacaoTerapeutica");
        informarOutraIndicacaoTerapeutica
                .asAtrBasic()
                .label("Informar outra indicação terapêutica");
        STypeSimple outraIndicacaoTerapeutica = notificacaoSimplificada.addFieldString("outraIndicacaoTerapeutica");
        outraIndicacaoTerapeutica
                .withView(SViewTextArea::new)
                .asAtrBasic()
                .required()
                .tamanhoMaximo(600)
                .label("Outra indicação terapêutica")
                .dependsOn(informarOutraIndicacaoTerapeutica)
                .visible(i -> BooleanUtils.isTrue(Value.of(i, informarOutraIndicacaoTerapeutica)));

        indicacaoTerapeutica
                .asAtrBasic()
                .dependsOn(informarOutraIndicacaoTerapeutica)
                .visible(i -> BooleanUtils.isNotTrue(Value.of(i, informarOutraIndicacaoTerapeutica)));

    }

    private abstract class FormaFarmaceuticaProvider implements FilteredPagedProvider<FormaFarmaceuticaBasica> {

        abstract List<Integer> getIds(SInstance root);

        @Override
        public void loadFilterDefinition(STypeComposite<?> filter) {
            filter.addFieldString("descricao").asAtrBasic().label("Descrição");
            final STypeString conceito = filter.addFieldString("conceito");
            conceito.withTextAreaView();
            conceito.asAtrBasic().label("Conceito");
        }

        @Override
        public Long getSize(SInstance root, SInstance filter) {
            return dominioService(root)
                    .countFormasFarmaceuticasDinamizadas(getIds(root),
                            Value.of(filter, "descricao"), Value.of(filter, "conceito"));
        }

        @Override
        public List<FormaFarmaceuticaBasica> load(SInstance root, SInstance filter, long first, long count) {
            return dominioService(root)
                    .formasFarmaceuticasDinamizadas(getIds(root),
                            Value.of(filter, "descricao"), Value.of(filter, "conceito"),
                            first, count);
        }

        @Override
        public List<Column> getColumns() {
            return Arrays.asList(Column.of("conceito", "Conceito"), Column.of("descricao", "Descrição"));
        }

    }
}

