package org.opensingular.singular.exemplos.notificacaosimplificada.form.dinamizado;

import org.opensingular.singular.exemplos.notificacaosimplificada.domain.FormaFarmaceuticaBasica;
import org.opensingular.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeCategoriaRegulatoria;
import org.opensingular.singular.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.singular.exemplos.util.TripleConverter;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SIList;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.STypeAttachmentList;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.STypeSimple;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.converter.ValueToSICompositeConverter;
import org.opensingular.singular.form.provider.Config;
import org.opensingular.singular.form.provider.FilteredPagedProvider;
import org.opensingular.singular.form.provider.ProviderContext;
import org.opensingular.singular.form.provider.SSimpleProvider;
import org.opensingular.singular.form.type.core.SIInteger;
import org.opensingular.singular.form.type.core.STypeBoolean;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.util.transformer.Value;
import org.opensingular.singular.form.view.SViewByBlock;
import org.opensingular.singular.form.view.SViewListByMasterDetail;
import org.opensingular.singular.form.view.SViewListByTable;
import org.opensingular.singular.form.view.SViewSearchModal;
import org.opensingular.singular.form.view.SViewTextArea;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SInfoType(name = "STypeNotificacaoSimplificadaDinamizado", spackage = SPackageNotificacaoSimplificadaDinamizado.class)
public class STypeNotificacaoSimplificadaDinamizado extends STypeComposite<SIComposite> {


    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {

        asAtr().label("Medicamento Dinamizado");
        asAtr().displayString("${nomeComercial} - ${linhaProducao.descricao} (<#list formulasHomeopaticas as c>${c.descricaoDinamizada.descricao}<#sep>, </#sep></#list>) ");

        addNomeComercial();
        addCaracteristicas();
        addAcondicionamentos();
        addListaFormulaProduto();
        addIndicacaoTerapeutica();
        addListaReferencias();
        addListaLayoutFolheto();


        withView(new SViewByBlock("Medicamento Dinamizado"), view -> {
            view
                    .newBlock()
                    .add("nomeComercial")
                    .add("classeConformeMatriz")
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

    private void addCaracteristicas() {

        final STypeCategoriaRegulatoria                           classe               = addField("classeConformeMatriz", STypeCategoriaRegulatoria.class);
        final STypeLinhaProducaoDinamizado                        linhaProducao        = addField("linhaProducao", STypeLinhaProducaoDinamizado.class);
        final STypeList<STypeComposite<SIComposite>, SIComposite> formulasHomeopaticas = addFieldListOfComposite("formulasHomeopaticas", "formulaHomeopatica");

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

        final STypeComposite formaFarmaceutica = addFieldComposite("formaFarmaceutica");

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

    private void addAcondicionamentos() {
        STypeComposite<SIComposite> listaAcondicionamentos = addFieldComposite("listaAcondicionamento");
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

    private void addNomeComercial() {
        final STypeString nomeComercial = addFieldString("nomeComercial");
        nomeComercial
                .asAtr()
                .required()
                .label("Nome do medicamento")
                .asAtrBootstrap()
                .colPreference(12);
    }

    private void addListaFormulaProduto() {
        STypeComposite<SIComposite> listaFormulaProduto = addFieldComposite("listaFormulaProduto");
        listaFormulaProduto
                .asAtrAnnotation().setAnnotated();
        final STypeAttachmentList formulasProduto =
                listaFormulaProduto.addFieldListOfAttachment("formulasProduto", "formulaProduto");
        formulasProduto
                .withMiniumSizeOf(1);
//        addField("OutraFormula", STypeAttachment.class).asAtr().label("Outra Formula");

    }

    private void addListaLayoutFolheto() {
        STypeComposite<SIComposite> listaLayoutFolheto = addFieldComposite("listaLayoutFolheto");
        listaLayoutFolheto
                .asAtrAnnotation().setAnnotated();
        final STypeAttachmentList layoutsBula =
                listaLayoutFolheto.addFieldListOfAttachment("layoutsfolheto", "layoutfolheto");
        layoutsBula
                .withMiniumSizeOf(1);
    }

    private void addListaReferencias() {
        STypeComposite<SIComposite> listaReferencias = addFieldComposite("listaReferencias");
        listaReferencias
                .asAtrAnnotation().setAnnotated();
        final STypeAttachmentList indicacoesPropostas =
                listaReferencias.addFieldListOfAttachment("indicacoesPropostas", "indicacaoProposta");
        indicacoesPropostas
                .asAtr().label("Referências da indicação proposta");
        indicacoesPropostas
                .withMiniumSizeOf(1);
    }

    private void addIndicacaoTerapeutica() {
        final STypeComposite<SIComposite> indicacaoTerapeutica          = addFieldComposite("indicacaoTerapeutica");
        final STypeInteger                idIndicacaoTerapeutica        = indicacaoTerapeutica.addFieldInteger("id");
        final STypeSimple descricaoIndicacaoTerapeutica = indicacaoTerapeutica.addFieldString("descricao");
        indicacaoTerapeutica
                .asAtr()
                .label("Indicação")
                .required()
                .asAtrBootstrap()
                .colPreference(6);
        indicacaoTerapeutica
                .selection()
                .id(idIndicacaoTerapeutica)
                .display(descricaoIndicacaoTerapeutica)
                .simpleProvider((SSimpleProvider) builder -> {
                    dominioService(builder.getCurrentInstance()).indicacoesTerapeuticas().forEach(
                            it -> builder.add()
                                    .set(idIndicacaoTerapeutica, it.getLeft())
                                    .set(descricaoIndicacaoTerapeutica, it.getRight())
                    );
                });

        STypeBoolean informarOutraIndicacaoTerapeutica = addFieldBoolean("informarOutraIndicacaoTerapeutica");
        informarOutraIndicacaoTerapeutica
                .asAtr()
                .label("Informar outra indicação terapêutica")

                .asAtrBootstrap().colPreference(12);
        STypeSimple outraIndicacaoTerapeutica = addFieldString("outraIndicacaoTerapeutica");
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
