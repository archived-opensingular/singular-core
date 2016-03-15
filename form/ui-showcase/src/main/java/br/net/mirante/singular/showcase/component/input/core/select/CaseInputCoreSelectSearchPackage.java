/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySearchModal;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectSearchPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        final STypeString tipoContato = tipoMyForm.addFieldString("tipoContato", true)
                .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        tipoContato.withView(SViewSelectionBySearchModal::new);
        tipoContato.as(AtrBasic::new).label("Contato");

        /**
         * Neste caso vemos como tipos compostos podem ser usados na seleção por busca.
         */

        final STypeString degreeType = tipoMyForm.addFieldString("degree");
        degreeType.as(AtrBasic::new).label("Escolaridade");
        degreeType.withSelection()
                .add("Alfabetizado", "Alfabetização")
                .add("1º Grau", "Ensino Fundamental")
                .add("2º Grau", "Ensino Médio")
                .add("Técnico", "Escola Técnica")
                .add("Graduado", "Superior")
                .add("Pós", "Pós Graduação")
                .add("MsC", "Mestrado")
                .add("PhD", "Doutorado");
        degreeType.withView(SViewSelectionBySearchModal::new);

        /**
         *  No tipo composto é possível expandir a seleção para exibir outros campos além
         *  do valor de descrição, fornecendo maior flexibilidade e abrangência.
         **/

        final STypeComposite<?> planetType = tipoMyForm.addFieldComposite("planet");
        final STypeString id = planetType.addFieldString("id");
        final STypeString nome = planetType.addFieldString("nome");

        planetType.as(AtrBasic::new).label("Planeta Favorito");
        planetType.addFieldDecimal("radius").as(AtrBasic::new).label("Raio");
        planetType.addFieldString("atmosphericComposition").as(AtrBasic::new).label("Composição Atmosférica");
        planetType.withSelectionFromProvider("nome", (inst, lb) -> {
                    lb
                            .add().set(id, "1").set(nome, "Mercury").set("radius", 2439.64).set("atmosphericComposition", "He, Na+, P+")
                            .add().set(id, "2").set(nome, "Venus").set("radius", 6051.59).set("atmosphericComposition", "CO2, N2")
                            .add().set(id, "3").set(nome, "Earth").set("radius", 6378.1).set("atmosphericComposition", "N2, O2, Ar")
                            .add().set(id, "4").set(nome, "Mars").set("radius", 3397.00).set("atmosphericComposition", "CO2, N2, Ar");
                }
        );
        planetType.setView(SViewSelectionBySearchModal::new)
                .setAdditionalFields("radius", "atmosphericComposition");
    }

}
