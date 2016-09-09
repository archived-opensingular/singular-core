package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.TipoPeticaoPrimariaGGTOX;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.*;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.listeners.IngredienteAtivoUpdateListener;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.validators.AtivoAmostraValidator;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.provider.SimpleProvider;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.view.SViewByBlock;
import br.net.mirante.singular.form.view.SViewSelectionByRadio;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.TipoPeticaoPrimariaGGTOX.*;
import static br.net.mirante.singular.form.util.SingularPredicates.*;

@SInfoType(name = "STypePeticaoPrimariaSimplificada", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypePeticaoPrimariaSimplificada extends STypePersistentComposite {

    public final static String ESTUDOS_RESIDUOS_PATH = "estudosResiduos";
    public final static String NIVEL_PATH            = "nivel";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .asAtr()
//                .label("Petição primária Simplificada")
                .displayString("Petição de ${tipoPeticao.nome}, nível ${nivel}");

        this.addInstanceValidator(new AtivoAmostraValidator());

        final STypeComposite<SIComposite>                      tipoPeticao             = this.addFieldComposite("tipoPeticao");
        final STypeInteger                                     idTipoPeticao           = tipoPeticao.addFieldInteger("id");
        final STypeString                                      descricaoTipoPeticao    = tipoPeticao.addFieldString("nome");
        final STypeString                                      nivel                   = this.addFieldString(NIVEL_PATH);
        final STypeDadosGeraisPeticaoPrimariaSimplificada      dadosGerais             = this.addField("dadosGerais", STypeDadosGeraisPeticaoPrimariaSimplificada.class);
        final STypeRequerente                                  requerente              = this.addField("requerente", STypeRequerente.class);
        final STypeRepresentanteLegal                          representanteLegal      = this.addField("representanteLegal", STypeRepresentanteLegal.class);
        final STypeIngredienteAtivoPeticaoPrimariaSimplificada ingredienteAtivoPeticao = this.addField("ingredienteAtivoPeticao", STypeIngredienteAtivoPeticaoPrimariaSimplificada.class);
        final STypeProdutoTecnicoPeticaoPrimariaSimplificada   produtoTecnicoPeticao   = this.addField("produtoTecnicoPeticao", STypeProdutoTecnicoPeticaoPrimariaSimplificada.class);
        final STypeProdutoFormuladoPeticaoPrimariaSimplificada produtoFormulado        = this.addField("produtoFormulado", STypeProdutoFormuladoPeticaoPrimariaSimplificada.class);
        final STypeEstudosResiduos                             estudosResiduos         = this.addField(ESTUDOS_RESIDUOS_PATH, STypeEstudosResiduos.class);
        final STypeInformacoesProcesso                         informacoesProcesso     = this.addField("informacoesProcesso", STypeInformacoesProcesso.class);
        final STypeAnexosPeticaoPrimariaSimplificada           anexos                  = this.addField("anexos", STypeAnexosPeticaoPrimariaSimplificada.class);

        tipoPeticao
                .selection()
                .id(idTipoPeticao)
                .display(descricaoTipoPeticao)
                .simpleProvider(builder -> {
                    Stream.of(TipoPeticaoPrimariaGGTOX.values()).forEach(tp -> builder.add().set(idTipoPeticao, tp.getId()).set(descricaoTipoPeticao, tp.getDescricao()));
                });

        final List<Integer> apenasNivel1              = Arrays.asList(PT.getId(), PTE.getId());
        final List<Integer> numeroProcessoIgualMatriz = Arrays.asList(PT.getId(), PTE.getId());
        final List<Integer> naoPossuiProdutoFormulado = Arrays.asList(PT.getId(), PTE.getId(), PRE_MISTURA.getId());
        final List<Integer> produtoTecnicoOpcional    = Arrays.asList(BIOLOGICO.getId(), PRESERVATIVO_MADEIRA.getId(), NAO_AGRICOLA.getId());
        final List<Integer> naoTemRotuloBula          = Arrays.asList(PRE_MISTURA.getId(), PT.getId(), PTE.getId());
        final List<Integer> produtoTecnicoMultiplo    = Arrays.asList(PF.getId(), PFE.getId());
        final List<Integer> precisaEstudoResiduos     = Arrays.asList(PF.getId(), PFE.getId());

        tipoPeticao
                .withUpdateListener(si -> {
                    si.findNearest(nivel).ifPresent(i -> {
                        if (!apenasNivel1.contains(Value.of(si, idTipoPeticao))) {
                            i.clearInstance();
                        }
                    });
                })
                .asAtrBootstrap()
                .colPreference(6)
                .asAtr()
                .label(" Tipo de Petição ");


        nivel.selectionOf(String.class, new SViewSelectionByRadio())
                .selfIdAndDisplay()
                .simpleProvider((SimpleProvider<String, SIString>) ins -> ins
                        .findNearestValue(idTipoPeticao)
                        .map(i -> (int) i)
                        .map(TipoPeticaoPrimariaGGTOX::getValueById)
                        .map(TipoPeticaoPrimariaGGTOX::niveis)
                        .orElseGet(Collections::emptyList));

        nivel
                .asAtr()
                .exists(typeValueIsNotNull(tipoPeticao))
                .dependsOn(tipoPeticao);

        nivel
                .withUpdateListener(si -> {
                    if (apenasNivel1.contains(Value.of(si, idTipoPeticao))) {
                        si.setValue("I");
                    }
                })
                .asAtr()
                .enabled(typeValueIsNotIn(idTipoPeticao, apenasNivel1))
                .required()
                .label("Petição primária simplificada de nível");

        dadosGerais
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValueIsNotNull(tipoPeticao));

        ingredienteAtivoPeticao
                .asAtr()
                .label("Ingrediente Ativo");

        ingredienteAtivoPeticao
                .ingredientesAtivos
                .withUpdateListener(new IngredienteAtivoUpdateListener<>());

        produtoTecnicoPeticao
                .asAtr()
                .dependsOn(nivel, tipoPeticao)
                .exists(typeValueMatches(nivel, StringUtils::isNotEmpty));

        produtoTecnicoPeticao
                .produtoTecnicoNaoSeAplica
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValueIsIn(idTipoPeticao, produtoTecnicoOpcional));
        produtoTecnicoPeticao
                .produtosTecnicos
                .asAtr()
                .dependsOn(tipoPeticao, produtoTecnicoPeticao.produtoTecnicoNaoSeAplica)
                .exists(anyMatches(typeValueIsNull(produtoTecnicoPeticao.produtoTecnicoNaoSeAplica), typeValueIsFalse(produtoTecnicoPeticao.produtoTecnicoNaoSeAplica)));

        produtoTecnicoPeticao
                .produtosTecnicos
                .addInstanceValidator(ValidationErrorLevel.ERROR, validatable -> {
                    if (!produtoTecnicoMultiplo.contains(Value.of(validatable.getInstance(), idTipoPeticao))) {
                        if (validatable.getInstance().size() > 1) {
                            validatable.error("Apenas um produto técnico deve ser informado para o tipo de petição escolhido.");
                        }
                    }
                    if (!produtoTecnicoOpcional.contains(Value.of(validatable.getInstance(), idTipoPeticao))) {
                        if (validatable.getInstance().size() < 1) {
                            validatable.error("Nenhum produto técnico foi informado.");
                        }
                    }
                });

        produtoTecnicoPeticao
                .produtosTecnicos
                .getElementsType()
                .numeroProcessoProdutoTecnico
                .asAtr()
                .dependsOn(dadosGerais.numeroProcessoPeticaoMatriz, tipoPeticao)
                .enabled(typeValueIsNotIn(idTipoPeticao, numeroProcessoIgualMatriz));

        produtoTecnicoPeticao
                .produtosTecnicos
                .getElementsType()
                .numeroProcessoProdutoTecnico
                .withUpdateListener(si -> {
                    if (numeroProcessoIgualMatriz.contains(Value.of(si, idTipoPeticao))) {
                        si.setValue(Value.of(si, dadosGerais.numeroProcessoPeticaoMatriz));
                    }
                });

        produtoTecnicoPeticao
                .produtosTecnicos
                .getElementsType()
                .numeroProcessoProdutoTecnico
                .withInitListener(si -> {
                    if (numeroProcessoIgualMatriz.contains(Value.of(si, idTipoPeticao))) {
                        si.setValue(Value.of(si, dadosGerais.numeroProcessoPeticaoMatriz));
                    }
                });

        produtoTecnicoPeticao
                .produtosTecnicos
                .withUpdateListener(si -> {
                    if (numeroProcessoIgualMatriz.contains(Value.of(si, idTipoPeticao))) {
                        for (SIComposite composite : si.getValues()) {
                            composite.findNearest(produtoTecnicoPeticao
                                    .produtosTecnicos
                                    .getElementsType()
                                    .numeroProcessoProdutoTecnico).get().setValue(
                                    Value.of(si, dadosGerais.numeroProcessoPeticaoMatriz));
                        }
                    }
                })
                .asAtr()
                .dependsOn(dadosGerais.numeroProcessoPeticaoMatriz);

        produtoTecnicoPeticao
                .produtosTecnicos
                .getElementsType()
                .fabricante
                .asAtr()
                .dependsOn(nivel)
                .exists(typeValueIsIn(nivel, "I", "II"));

        produtoTecnicoPeticao
                .produtosTecnicos
                .getElementsType()
                .fabricantes
                .asAtr()
                .dependsOn(nivel)
                .exists(typeValueIsNotIn(nivel, "I", "II"));

        produtoFormulado
                .asAtr()
                .dependsOn(nivel)
                .exists(allMatches(typeValueMatches(nivel, StringUtils::isNotEmpty), typeValueIsNotIn(idTipoPeticao, naoPossuiProdutoFormulado)));

        produtoFormulado
                .formulador
                .asAtr()
                .dependsOn(nivel)
                .exists(typeValueIsEqualsTo(nivel, "I"));

        produtoFormulado
                .formuladores
                .asAtr()
                .dependsOn(nivel)
                .exists(typeValueIsNotEqualsTo(nivel, "I"));

        estudosResiduos
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValueIsIn(idTipoPeticao, precisaEstudoResiduos));


        informacoesProcesso
                .asAtr()
                .dependsOn(nivel)
                .exists(typeValueIsNotNull(nivel));

        informacoesProcesso
                .modeloRotulo
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValueIsNotIn(idTipoPeticao, naoTemRotuloBula));


        informacoesProcesso
                .modeloBula
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValueIsNotIn(idTipoPeticao, naoTemRotuloBula));

        anexos
                .asAtr()
                .dependsOn(nivel)
                .exists(typeValueIsEqualsTo(nivel, "IV"));

        this.withView(new SViewByBlock(), blocks -> {
            blocks
                    .newBlock().add(tipoPeticao).add(nivel)
                    .newBlock().add(dadosGerais)
                    .newBlock().add(requerente)
                    .newBlock().add(representanteLegal)
                    .newBlock().add(ingredienteAtivoPeticao)
                    .newBlock().add(produtoTecnicoPeticao)
                    .newBlock().add(produtoFormulado)
                    .newBlock().add(estudosResiduos)
                    .newBlock().add(informacoesProcesso)
                    .newBlock().add(anexos);

        });

    }


}
