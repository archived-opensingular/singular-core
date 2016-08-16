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

import static br.net.mirante.singular.form.util.SingularPredicates.*;

@SInfoType(name = "STypePeticaoPrimariaSimplificada", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypePeticaoPrimariaSimplificada extends STypePersistentComposite {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .asAtr()
                .label("Petição primaria Simplificada")
                .displayString("Petição de ${tipoPeticao.nome}, nível ${nivel}");

        this.addInstanceValidator(new AtivoAmostraValidator());

        final STypeComposite<SIComposite>                      tipoPeticao             = this.addFieldComposite("tipoPeticao");
        final STypeInteger                                     idTipoPeticao           = tipoPeticao.addFieldInteger("id");
        final STypeString                                      descricaoTipoPeticao    = tipoPeticao.addFieldString("nome");
        final STypeString                                      nivel                   = this.addFieldString("nivel");
        final STypeDadosGeraisPeticaoPrimariaSimplificada      dadosGerais             = this.addField("dadosGerais", STypeDadosGeraisPeticaoPrimariaSimplificada.class);
        final STypeRequerente                                  requerente              = this.addField("requerente", STypeRequerente.class);
        final STypeRepresentanteLegal                          representanteLegal      = this.addField("representanteLegal", STypeRepresentanteLegal.class);
        final STypeIngredienteAtivoPeticaoPrimariaSimplificada ingredienteAtivoPeticao = this.addField("ingredienteAtivoPeticao", STypeIngredienteAtivoPeticaoPrimariaSimplificada.class);
        final STypeProdutoTecnicoPeticaoPrimariaSimplificada   produtoTecnicoPeticao   = this.addField("produtoTecnicoPeticao", STypeProdutoTecnicoPeticaoPrimariaSimplificada.class);
        final STypeProdutoFormuladoPeticaoPrimariaSimplificada produtoFormulado        = this.addField("produtoFormulado", STypeProdutoFormuladoPeticaoPrimariaSimplificada.class);
        final STypeEstudosResiduos                             estudosResiduos         = this.addField("estudosResiduos", STypeEstudosResiduos.class);
        final STypeInformacoesProcesso                         informacoesProcesso     = this.addField("informacoesProcesso", STypeInformacoesProcesso.class);
        final STypeAnexosPeticaoPrimariaSimplificada           anexos                  = this.addField("anexos", STypeAnexosPeticaoPrimariaSimplificada.class);

        tipoPeticao
                .selection()
                .id(idTipoPeticao)
                .display(descricaoTipoPeticao)
                .simpleProvider(builder -> {
                    Stream.of(TipoPeticaoPrimariaGGTOX.values()).forEach(tp -> builder.add().set(idTipoPeticao, tp.getId()).set(descricaoTipoPeticao, tp.getDescricao()));
                });

        final List<Integer> apenasNivel1              = Arrays.asList(7, 8);
        final List<Integer> numeroProcessoIgualMatriz = Arrays.asList(7, 8);
        final List<Integer> naoPossuiProdutoFormulado = Arrays.asList(7, 8);
        final List<Integer> produtoTecnicoOpcional    = Arrays.asList(1, 3, 4);
        final List<Integer> naoTemRotuloBula          = Arrays.asList(2, 7, 8);
        final List<Integer> produtoTecnicoMultiplo    = Arrays.asList(5, 6);
        final List<Integer> precisaEstudoResiduos     = Arrays.asList(1, 4, 5, 6);

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
                .exists(typeValIsNotNull(tipoPeticao))
                .dependsOn(tipoPeticao);

        nivel
                .withUpdateListener(si -> {
                    if (apenasNivel1.contains(Value.of(si, idTipoPeticao))) {
                        si.setValue("I");
                    }
                })
                .asAtr()
                .enabled(typeValIsNotIn(idTipoPeticao, apenasNivel1))
                .required()
                .label("Petição primária simplificada de nível");

        dadosGerais
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValIsNotNull(tipoPeticao));

        ingredienteAtivoPeticao
                .asAtr()
                .label("Ingrediente Ativo");

        ingredienteAtivoPeticao
                .ingredientesAtivos
                .withUpdateListener(new IngredienteAtivoUpdateListener<>());

        produtoTecnicoPeticao
                .asAtr()
                .dependsOn(nivel, tipoPeticao)
                .exists(typeValMatches(nivel, StringUtils::isNotEmpty));

        produtoTecnicoPeticao
                .produtoTecnicoNaoSeAplica
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValIsIn(idTipoPeticao, produtoTecnicoOpcional));
        produtoTecnicoPeticao
                .produtosTecnicos
                .asAtr()
                .dependsOn(tipoPeticao, produtoTecnicoPeticao.produtoTecnicoNaoSeAplica)
                .exists(anyMatches(typeValIsNull(produtoTecnicoPeticao.produtoTecnicoNaoSeAplica), typeValIsFalse(produtoTecnicoPeticao.produtoTecnicoNaoSeAplica)));

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
                .enabled(typeValIsNotIn(idTipoPeticao, numeroProcessoIgualMatriz));

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
                .exists(typeValIsIn(nivel, "I", "II"));

        produtoTecnicoPeticao
                .produtosTecnicos
                .getElementsType()
                .fabricantes
                .asAtr()
                .dependsOn(nivel)
                .exists(typeValIsNotIn(nivel, "I", "II"));

        produtoFormulado
                .asAtr()
                .dependsOn(nivel)
                .exists(allMatches(typeValMatches(nivel, StringUtils::isNotEmpty), typeValIsNotIn(idTipoPeticao, naoPossuiProdutoFormulado)));

        produtoFormulado
                .formulador
                .asAtr()
                .dependsOn(nivel)
                .exists(typeValIsEqualsTo(nivel, "I"));

        produtoFormulado
                .formuladores
                .asAtr()
                .dependsOn(nivel)
                .exists(typeValIsNotEqualsTo(nivel, "I"));

        estudosResiduos
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValIsIn(idTipoPeticao, precisaEstudoResiduos));


        informacoesProcesso
                .asAtr()
                .dependsOn(nivel)
                .exists(typeValIsNotNull(nivel));

        informacoesProcesso
                .modeloRotulo
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValIsNotIn(idTipoPeticao, naoTemRotuloBula));


        informacoesProcesso
                .modeloBula
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValIsNotIn(idTipoPeticao, naoTemRotuloBula));

        anexos
                .asAtr()
                .dependsOn(nivel)
                .exists(typeValIsEqualsTo(nivel, "IV"));

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
