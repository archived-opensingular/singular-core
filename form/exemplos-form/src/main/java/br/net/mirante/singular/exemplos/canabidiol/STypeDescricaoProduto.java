/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import java.util.HashMap;
import java.util.Map;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;

@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypeDescricaoProduto extends STypeComposite<SIComposite> {


    private static Map<Integer, String> composicoes = new HashMap<>();
    private static Map<Integer, String> enderecos = new HashMap<>();

    static {
        composicoes.put(1, "Aguardando informação");
        composicoes.put(2, "Aguardando informação");
        composicoes.put(3, "Aguardando informação");
        composicoes.put(4, "Canabidiol - 8,33mg/mL");
        composicoes.put(5, "Canabidiol - 14 a 25%");
        composicoes.put(6, "Canabidiol - 14 a 25%");
        composicoes.put(7, "22:1 Canabidiol/THC");
    }

    static {
        enderecos.put(1, "Hempmeds 12255, Crosthwaite Circle - Poway, CA, 92064 (EUA)");
        enderecos.put(2, "Hempmeds 12255, Crosthwaite Circle - Poway, CA, 92064 (EUA)");
        enderecos.put(3, "Hempmeds 12255, Crosthwaite Circle - Poway, CA, 92064 (EUA)");
        enderecos.put(4, "580, Burbank St. Broomfield, CO 80020 (EUA)");
        enderecos.put(5, "Hempmeds 12255, Crosthwaite Circle - Poway, CA, 92064 (EUA)");
        enderecos.put(6, "Hempmeds 12255, Crosthwaite Circle - Poway, CA, 92064 (EUA)");
        enderecos.put(7, "2560, Paragon Dr. Colorado Springs, CO 80918 (EUA)");
    }

    private STypeInteger nomeComercial;
    private STypeString composicao;
    private STypeString enderecoFabricante;
    private STypeString descricaoQuantidade;
    private STypeString outroComposicao;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        nomeComercial = this.addFieldInteger("nomeComercial");

        nomeComercial
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Nome Comercial");

        nomeComercial
                .withSelection()
.add(1, "Cibdex Hemp CBD Complex 1oz (Gotas) – Fabricante: Cibdex Inc.")
                .add(2, "Cibdex Hemp CBD Complex 2oz (Gotas) – Fabricante: Cibdex Inc.")
                .add(3, "Cibdex Hemp CBD Complex (Cápsulas) – Fabricante: Cibdex Inc.")
                .add(4, "Hemp CBD Oil 2000mg Canabidiol - 240mL - Fabricante: Bluebird Botanicals")
                .add(5, "Real Scientific Hemp Oil (RSHO) CBD 14-25% - 3g (Pasta) – Fabricante: Hemp Meds Px")
                .add(6, "Real Scientific Hemp Oil (RSHO) CBD 14-25% - 10g (Pasta) – Fabricante: Hemp Meds Px")
                .add(7, "Revivid LLC Hemp Tinctute 500mg (22:1 CBD/THC) - 30mL (Gotas) – Fabricante: Revivid")
                .add(8, "Outro");

        nomeComercial
                .withView(new SViewSelectionByRadio().verticalLayout());


        composicao = this.addFieldString("composicao");

        composicao
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
.label("Composição")
                .visivel(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) < 8)
                .dependsOn(nomeComercial)
                .as(AtrBootstrap::new)
                .colPreference(6);

        composicao
                .withView(new SViewSelectionByRadio().verticalLayout());

        composicao.withSelectionFromProvider(
                (optionsInstance, filter ) -> {
                    SIList<?> lista = composicao.newList();
                    String value = composicoes.get(Value.of(optionsInstance, nomeComercial));
                    if (value != null) {
                        lista.addValue(value);
                    }
                    return lista;
                }
        );


        enderecoFabricante = this.addFieldString("enderecoFabricante");

        enderecoFabricante
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
.label("Endereço do fabricante")

                .visivel(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) < 8)
                .dependsOn(nomeComercial)
                .as(AtrBootstrap::new)
                .colPreference(6);

        enderecoFabricante
                .withView(new SViewSelectionByRadio().verticalLayout());

        enderecoFabricante.withSelectionFromProvider(
                (optionsInstance, filter) -> {
                    SIList<?> lista = enderecoFabricante.newList();
                    String value = enderecos.get(Value.of(optionsInstance, nomeComercial));
                    if (value != null) {
                        lista.addValue(value);
                    }
                    return lista;
                }
        );


        STypeComposite<?> outroMedicamento = addFieldComposite("outro");
        outroMedicamento
                .as(AtrCore::new)
                .as(AtrBasic::new)
                .label("Outro Medicamento")
                .dependsOn(nomeComercial)

                .visivel(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) == 8);

        outroMedicamento
                .addFieldString("outroNome")
                .as(AtrCore::new)
                .obrigatorio(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) == 8)
                .as(AtrBasic::new)
                .label("Nome Comercial")
                .as(AtrBootstrap::new)
                .colPreference(6);

        outroComposicao = outroMedicamento.addFieldString("outroComposicao");

        outroComposicao
                .as(AtrCore::new)
                .obrigatorio(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) == 8)
                .as(AtrBasic::new)
.label("Composição")
                .as(AtrBootstrap::new)
                .colPreference(6);

        outroMedicamento
                .addFieldString("outroEndereco")
                .as(AtrCore::new)
                .obrigatorio(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) == 8)
                .as(AtrBasic::new)
.label("Endereço do Fabricante")
                .as(AtrBootstrap::new)
                .colPreference(12);


        descricaoQuantidade = this.addFieldString("descricaoQuantidade");

        descricaoQuantidade
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Quantidade solicitada a ser importada no período de 1 (um) ano")
                .subtitle("Informar as unidades do produto (ex: quantidade de frascos, tubos, caixas)");

    }

    public STypeInteger getNomeComercial() {
        return nomeComercial;
    }

    public STypeString getComposicao() {
        return composicao;
    }

    public STypeString getEnderecoFabricante() {
        return enderecoFabricante;
    }

    public STypeString getDescricaoQuantidade() {
        return descricaoQuantidade;
    }

    public STypeString getOutroComposicao() {
        return outroComposicao;
    }
}
