package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common.*;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.provider.SSimpleProvider;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.view.SViewByBlock;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;


public class SPackagePeticaoPrimariaSimplificada extends SPackage {


    public static final String PACOTE        = "mform.peticao";
    public static final String TIPO          = "PeticaoPrimariaSimplificada";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public SPackagePeticaoPrimariaSimplificada() {
        super(PACOTE);
    }


    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        pb.getDictionary().loadPackage(SPackagePPSCommon.class);

        final STypeComposite<SIComposite>                      peticaoSimplificada     = pb.createCompositeType(TIPO);
        final STypeComposite<SIComposite>                      tipoPeticao             = peticaoSimplificada.addFieldComposite("tipoPeticao");
        final STypeInteger                                     idTipoPeticao           = tipoPeticao.addFieldInteger("id");
        final STypeString                                      descricaoTipoPeticao    = tipoPeticao.addFieldString("nome");
        final STypeString                                      nivel                   = peticaoSimplificada.addFieldString("nivel");
        final STypeDadosGeraisPeticaoPrimariaSimplificada      dadosGerais             = peticaoSimplificada.addField("dadosGerais", STypeDadosGeraisPeticaoPrimariaSimplificada.class);
        final STypeRequerente                                  requerente              = peticaoSimplificada.addField("requerente", STypeRequerente.class);
        final STypeRepresentanteLegal                          representanteLegal      = peticaoSimplificada.addField("representanteLegal", STypeRepresentanteLegal.class);
        final STypeIngredienteAtivoPeticaoPrimariaSimplificada ingredienteAtivoPeticao = peticaoSimplificada.addField("ingredienteAtivoPeticao", STypeIngredienteAtivoPeticaoPrimariaSimplificada.class);
        final STypeProdutoTecnicoPeticaoPrimariaSimplificada   produtoTecnicoPeticao   = peticaoSimplificada.addField("produtoTecnicoPeticao", STypeProdutoTecnicoPeticaoPrimariaSimplificada.class);
        final STypeProdutoFormuladoPeticaoPrimariaSimplificada produtoFormulado        = peticaoSimplificada.addField("produtoFormulado", STypeProdutoFormuladoPeticaoPrimariaSimplificada.class);
        final STypeAnexosPeticaoPrimariaSimplificada           anexos                  = peticaoSimplificada.addField("anexos", STypeAnexosPeticaoPrimariaSimplificada.class);

        tipoPeticao
                .selection()
                .id(idTipoPeticao)
                .display(descricaoTipoPeticao)
                .simpleProvider((SSimpleProvider) builder -> {
                    builder.add().set(idTipoPeticao, 1).set(descricaoTipoPeticao, "Feromônio, produtos biológicos, bioquímicos e outros");
                    builder.add().set(idTipoPeticao, 2).set(descricaoTipoPeticao, "Pré-mistura");
                    builder.add().set(idTipoPeticao, 3).set(descricaoTipoPeticao, "Preservativo de madeira");
                    builder.add().set(idTipoPeticao, 4).set(descricaoTipoPeticao, "Produto de uso não agrícola");
                    builder.add().set(idTipoPeticao, 5).set(descricaoTipoPeticao, "Produto formulado novo (PFN)");
                    builder.add().set(idTipoPeticao, 6).set(descricaoTipoPeticao, "Produto formulado com base em produto técnico equivalente (PTN)");
                    builder.add().set(idTipoPeticao, 7).set(descricaoTipoPeticao, "Produto técnico novo (PTN)");
                    builder.add().set(idTipoPeticao, 8).set(descricaoTipoPeticao, "Produto técnico equivalente (PTE)");
                });


        final List<Integer> apenasNivel1              = Arrays.asList(3, 7, 8);
        final List<Integer> numeroProcessoIgualMatriz = Arrays.asList(7, 8);
        final List<Integer> naoPossuiProdutoFormulado = Arrays.asList(7, 8);
        final List<Integer> produtoTecnicoOpcional    = Arrays.asList(1, 3, 4);
        final List<Integer> naoTemRotuloBula          = Arrays.asList(2, 7, 8);
        final List<Integer> produtoTecnicoMultiplo    = Arrays.asList(5, 6);

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

        nivel
                .selectionOf("I", "II", "III", "IV")
                .withRadioView()
                .asAtr()
                .exists(si -> Value.notNull(si, tipoPeticao))
                .dependsOn(tipoPeticao);

        nivel
                .withUpdateListener(si -> {
                    if (apenasNivel1.contains(Value.of(si, idTipoPeticao))) {
                        si.setValue("I");
                    }
                })
                .asAtr()
                .enabled(si -> !apenasNivel1.contains(Value.of(si, idTipoPeticao)))
                .required()
                .label("Nível");

        dadosGerais
                .asAtr()
                .dependsOn(tipoPeticao)
                .visible(si -> Value.notNull(si, tipoPeticao));


        ingredienteAtivoPeticao
                .asAtr()
                .label("Ingrediente Ativo");


        produtoTecnicoPeticao
                .asAtr()
                .dependsOn(nivel)
                .visible(si -> StringUtils.isNotEmpty(Value.of(si, nivel)));

        produtoTecnicoPeticao
                .produtoTecnicoNaoSeAplica
                .asAtr()
                .dependsOn(tipoPeticao)
                .visible(si -> produtoTecnicoOpcional.contains(Value.of(si, idTipoPeticao)));

        produtoTecnicoPeticao
                .produtosTecnicos
                .asAtr()
                .dependsOn(tipoPeticao)
                .visible(si -> produtoTecnicoMultiplo.contains(Value.of(si, idTipoPeticao)));

        produtoTecnicoPeticao
                .produtoTecnico
                .asAtr()
                .dependsOn(tipoPeticao, produtoTecnicoPeticao.produtoTecnicoNaoSeAplica)
                .visible(si ->
                        !produtoTecnicoMultiplo.contains(Value.of(si, idTipoPeticao))
                                &&
                                (!Value.notNull(si, produtoTecnicoPeticao.produtoTecnicoNaoSeAplica) || !Value.of(si, produtoTecnicoPeticao.produtoTecnicoNaoSeAplica))

                );


        final STypeProdutoTecnico[] tiposProdutoTecnicos = new STypeProdutoTecnico[]{produtoTecnicoPeticao.produtoTecnico, produtoTecnicoPeticao.produtosTecnicos.getElementsType()};

        for (STypeProdutoTecnico pt : tiposProdutoTecnicos) {


            pt
                    .numeroProcessoProdutoTecnico
                    .withUpdateListener(si -> {
                        if (numeroProcessoIgualMatriz.contains(Value.of(si, idTipoPeticao))) {
                            si.setValue(Value.of(si, dadosGerais.numeroProcessoPeticaoMatriz));
                        }
                    })
                    .asAtr()
                    .dependsOn(dadosGerais.numeroProcessoPeticaoMatriz, tipoPeticao)
                    .enabled(si -> !numeroProcessoIgualMatriz.contains(Value.of(si, idTipoPeticao)));


            pt
                    .fabricante
                    .asAtr()
                    .dependsOn(nivel)
                    .visible(si -> "I".equals(Value.of(si, nivel)) || "II".equals(Value.of(si, nivel)));

            pt
                    .fabricantes
                    .asAtr()
                    .dependsOn(nivel)
                    .visible(si -> !("I".equals(Value.of(si, nivel)) || "II".equals(Value.of(si, nivel))));
        }


        produtoFormulado
                .asAtr()
                .dependsOn(nivel)
                .visible(si -> StringUtils.isNotEmpty(Value.of(si, nivel)) && !naoPossuiProdutoFormulado.contains(Value.of(si, idTipoPeticao)));

        produtoFormulado
                .formulador
                .asAtr()
                .dependsOn(nivel)
                .visible(si -> "I".equals(Value.of(si, nivel)));

        produtoFormulado
                .formuladores
                .asAtr()
                .dependsOn(nivel)
                .visible(si -> !"I".equals(Value.of(si, nivel)));

        anexos
                .asAtr()
                .dependsOn(nivel)
                .visible(si -> Value.notNull(si, nivel));
        anexos
                .documentacaoI
                .asAtr()
                .dependsOn(nivel)
                .visible(si -> "I".equals(Value.of(si, nivel)));

        anexos.documentacaoI
                .modelosBulas
                .asAtr()
                .dependsOn(tipoPeticao)
                .visible(si -> !naoTemRotuloBula.contains(Value.of(si, idTipoPeticao)));

        anexos.documentacaoI
                .modelosRotulos
                .asAtr()
                .dependsOn(tipoPeticao)
                .visible(si -> !naoTemRotuloBula.contains(Value.of(si, idTipoPeticao)));


        anexos
                .documentacaoII
                .asAtr()
                .dependsOn(nivel)
                .visible(si -> "II".equals(Value.of(si, nivel)));

        anexos
                .documentacaoIII
                .asAtr()
                .dependsOn(nivel)
                .visible(si -> "III".equals(Value.of(si, nivel)));

        anexos
                .documentacaoIV
                .asAtr()
                .dependsOn(nivel)
                .visible(si -> "IV".equals(Value.of(si, nivel)));

        peticaoSimplificada.withView(new SViewByBlock(), blocks -> {
            blocks
                    .newBlock().add(tipoPeticao).add(nivel)
                    .newBlock().add(dadosGerais)
                    .newBlock().add(requerente)
                    .newBlock().add(representanteLegal)
                    .newBlock().add(ingredienteAtivoPeticao)
                    .newBlock().add(produtoTecnicoPeticao)
                    .newBlock().add(produtoFormulado)
                    .newBlock().add(anexos);

        });

    }

}

