/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.core.SIInteger;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.SimpleProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypeDescricaoProduto extends STypeComposite<SIComposite> {


    private static Map<Integer, String>        composicoes     = new LinkedHashMap<>();
    private static Map<Integer, String>        enderecos       = new LinkedHashMap<>();
    private static List<Pair> nomesComerciais = new ArrayList<>();

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

    static {
        nomesComerciais.add(Pair.of(1, "Cibdex Hemp CBD Complex 1oz (Gotas) – Fabricante: Cibdex Inc."));
        nomesComerciais.add(Pair.of(2, "Cibdex Hemp CBD Complex 2oz (Gotas) – Fabricante: Cibdex Inc."));
        nomesComerciais.add(Pair.of(3, "Cibdex Hemp CBD Complex (Cápsulas) – Fabricante: Cibdex Inc."));
        nomesComerciais.add(Pair.of(4, "Hemp CBD Oil 2000mg Canabidiol - 240mL - Fabricante: Bluebird Botanicals"));
        nomesComerciais.add(Pair.of(5, "Real Scientific Hemp Oil (RSHO) CBD 14-25% - 3g (Pasta) – Fabricante: Hemp Meds Px"));
        nomesComerciais.add(Pair.of(6, "Real Scientific Hemp Oil (RSHO) CBD 14-25% - 10g (Pasta) – Fabricante: Hemp Meds Px"));
        nomesComerciais.add(Pair.of(7, "Revivid LLC Hemp Tinctute 500mg (22:1 CBD/THC) - 30mL (Gotas) – Fabricante: Revivid"));
        nomesComerciais.add(Pair.of(8, "Outro"));
    }

    private STypeInteger nomeComercial;
    private STypeString  composicao;
    private STypeString  enderecoFabricante;
    private STypeString  descricaoQuantidade;
    private STypeString  outroComposicao;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        nomeComercial = this.addFieldInteger("nomeComercial");

        nomeComercial
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("Nome Comercial");

        nomeComercial.selectionOf(Pair.class)
                .id("${left}")
                .display("${right}")
                .converter(new SInstanceConverter<Pair, SIInteger>() {
                    @Override
                    public void fillInstance(SIInteger ins, Pair obj) {
                        ins.setValue(obj.getLeft());
                    }

                    @Override
                    public Pair toObject(SIInteger ins) {
                        return nomesComerciais.get(ins.getValue() - 1);
                    }
                }).simpleProvider(i -> nomesComerciais);

        nomeComercial
                .withView(new SViewSelectionByRadio().verticalLayout());

        composicao = this.addFieldString("composicao");

        composicao
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("Composição")
                .visible(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) < 8)
                .dependsOn(nomeComercial)
                .asAtrBootstrap()
                .colPreference(6);

        composicao
                .withView(new SViewSelectionByRadio().verticalLayout());

        composicao.selectionOf(String.class, new SViewSelectionByRadio().verticalLayout())
                .selfIdAndDisplay()
                .simpleProvider((SimpleProvider<String, SIString>) ins -> {
                    Optional<Integer> nm = ins.findNearestValue(nomeComercial);
                    if(nm.isPresent()) {
                        return Collections.singletonList(composicoes.get(nm.get()));
                    }
                    return null;
                });

        enderecoFabricante = this.addFieldString("enderecoFabricante");

        enderecoFabricante
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("Endereço do fabricante")

                .visible(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) < 8)
                .dependsOn(nomeComercial)
                .asAtrBootstrap()
                .colPreference(6);

        enderecoFabricante.withView(new SViewSelectionByRadio().verticalLayout());
        enderecoFabricante.selectionOf(String.class, new SViewSelectionByRadio().verticalLayout())
                .selfIdAndDisplay()
                .simpleProvider((SimpleProvider<String, SIString>) ins -> {
                    Optional<Integer> nm = ins.findNearestValue(nomeComercial);
                    if(nm.isPresent()) {
                        return Collections.singletonList(enderecos.get(nm.get()));
                    }
                    return null;
                });
        STypeComposite<?> outroMedicamento = addFieldComposite("outro");
        outroMedicamento
                .asAtrBasic()
                .label("Outro Medicamento")
                .dependsOn(nomeComercial)

                .visible(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) == 8);

        outroMedicamento
                .addFieldString("outroNome")
                .asAtrBasic()
                .required(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) == 8)
                .asAtrBasic()
                .label("Nome Comercial")
                .asAtrBootstrap()
                .colPreference(6);

        outroComposicao = outroMedicamento.addFieldString("outroComposicao");

        outroComposicao
                .asAtrBasic()
                .required(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) == 8)
                .asAtrBasic()
                .label("Composição")
                .asAtrBootstrap()
                .colPreference(6);

        outroMedicamento
                .addFieldString("outroEndereco")
                .asAtrBasic()
                .required(instancia -> Value.of(instancia, nomeComercial) != null && Value.of(instancia, nomeComercial) == 8)
                .asAtrBasic()
                .label("Endereço do Fabricante")
                .asAtrBootstrap()
                .colPreference(12);


        descricaoQuantidade = this.addFieldString("descricaoQuantidade");

        descricaoQuantidade
                .asAtrBasic()
                .required()
                .asAtrBasic()
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
