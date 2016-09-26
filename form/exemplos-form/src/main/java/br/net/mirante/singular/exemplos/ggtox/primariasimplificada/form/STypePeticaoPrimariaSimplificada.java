package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.TipoPeticaoPrimariaGGTOX;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.*;
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

    public final static boolean OBRIGATORIO       = false;
    public final static int     QUANTIDADE_MINIMA = 0;

    public final static String ESTUDOS_RESIDUOS_PATH     = "estudosResiduos";
    public final static String NIVEL_PATH                = "nivel";
    public static final String INFORMACOES_PROCESSO_PATH = "informacoesProcesso";
    public final static String TIPO_PETICAO              = "tipoPeticao";
    public final static String ID_TIPO_PETICAO           = "id";
    public final static String NOME_TIPO_PETICAO         = "nome";
    public static final String REQUERENTE                = "requerente";
    public static final String INGREDIENTE_ATIVO_PETICAO = "ingredienteAtivoPeticao";
    public static final String PRODUTO_FORMULADO         = "produtoFormulado";
    public static final String PRODUTO_TECNICO_PETICAO   = "produtoTecnicoPeticao";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this
                .asAtr()
//                .label("Petição primária Simplificada")
                .displayString("Petição de ${tipoPeticao.nome}, nível ${nivel}");

        this.addInstanceValidator(new AtivoAmostraValidator());

        final STypeComposite<SIComposite>                      tipoPeticao             = this.addFieldComposite(TIPO_PETICAO);
        final STypeInteger                                     idTipoPeticao           = tipoPeticao.addFieldInteger(ID_TIPO_PETICAO);
        final STypeString                                      descricaoTipoPeticao    = tipoPeticao.addFieldString(NOME_TIPO_PETICAO);
        final STypeString                                      nivel                   = this.addFieldString(NIVEL_PATH);
        final STypeDadosGeraisPeticaoPrimariaSimplificada      dadosGerais             = this.addField("dadosGerais", STypeDadosGeraisPeticaoPrimariaSimplificada.class);
        final STypeRequerente                                  requerente              = this.addField(REQUERENTE, STypeRequerente.class);
        final STypeRepresentanteLegal                          representanteLegal      = this.addField("representanteLegal", STypeRepresentanteLegal.class);
        final STypeIngredienteAtivoPeticaoPrimariaSimplificada ingredienteAtivoPeticao = this.addField(INGREDIENTE_ATIVO_PETICAO, STypeIngredienteAtivoPeticaoPrimariaSimplificada.class);
        final STypeProdutoTecnicoPeticaoPrimariaSimplificada   produtoTecnicoPeticao   = this.addField(PRODUTO_TECNICO_PETICAO, STypeProdutoTecnicoPeticaoPrimariaSimplificada.class);
        final STypeProdutoFormuladoPeticaoPrimariaSimplificada produtoFormulado        = this.addField(PRODUTO_FORMULADO, STypeProdutoFormuladoPeticaoPrimariaSimplificada.class);
        final STypeEstudosResiduos                             estudosResiduos         = this.addField(ESTUDOS_RESIDUOS_PATH, STypeEstudosResiduos.class);
        final STypeInformacoesProcesso                         informacoesProcesso     = this.addField(INFORMACOES_PROCESSO_PATH, STypeInformacoesProcesso.class);
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
        final List<Integer> naoPossuiEmbalagem        = Arrays.asList(PRE_MISTURA.getId(), PT.getId(), PTE.getId());
        final List<Integer> produtoTecnicoMultiplo    = Arrays.asList(PF.getId(), PFE.getId());
        final List<Integer> precisaEstudoResiduos     = Arrays.asList(PF.getId(), PFE.getId());
        final List<Integer> possuiFabricante          = Arrays.asList(PRE_MISTURA.getId(), PT.getId(), PTE.getId());


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
                .required(OBRIGATORIO)
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
                .required(OBRIGATORIO)
                .label("Petição primária simplificada de nível");

        dadosGerais
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValueIsNotNull(tipoPeticao));

        ingredienteAtivoPeticao
                .asAtr()
                .label("Ingrediente Ativo");

        produtoTecnicoPeticao
                .asAtr()
                .dependsOn(nivel, tipoPeticao)
                .exists(typeValueIsNotNull(nivel));

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
                .exists(allMatches(
                        typeValueIsIn(idTipoPeticao, possuiFabricante)
                ));

        produtoTecnicoPeticao
                .produtosTecnicos
                .getElementsType()
                .fabricantes
                .asAtr()
                .dependsOn(nivel, produtoTecnicoPeticao.produtosTecnicos.getElementsType().fabricante)
                .exists(allMatches(
                        typeValueIsIn(idTipoPeticao, possuiFabricante),
                        anyMatches(
                                typeValueIsFalse(produtoTecnicoPeticao.produtosTecnicos.getElementsType().fabricante),
                                typeValueIsNull(produtoTecnicoPeticao.produtosTecnicos.getElementsType().fabricante)
                        )
                ));

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

        informacoesProcesso
                .embalagens
                .asAtr()
                .dependsOn(tipoPeticao)
                .exists(typeValueIsNotIn(idTipoPeticao, naoPossuiEmbalagem));

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
