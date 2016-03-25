/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeYearMonth;

public class CaseListByMasterDetailNestedPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        final STypeList<STypeComposite<SIComposite>, SIComposite> experiencias = testForm.addFieldListOfComposite("experienciasProfissionais", "experiencia");
        final STypeComposite<?> experiencia = experiencias.getElementsType();
        final STypeYearMonth dtInicioExperiencia = experiencia.addField("inicio", STypeYearMonth.class, true);
        final STypeYearMonth dtFimExperiencia = experiencia.addField("fim", STypeYearMonth.class);
        final STypeString empresa = experiencia.addFieldString("empresa", true);
        final STypeString atividades = experiencia.addFieldString("atividades");

        final STypeList<STypeComposite<SIComposite>, SIComposite> cargos = experiencia.addFieldListOfComposite("cargos", "cargo");
        final STypeComposite<?> cargo = cargos.getElementsType();
        final STypeString nome = cargo.addFieldString("nome", true);
        final STypeYearMonth dtInicioCargo = cargo.addField("inicio", STypeYearMonth.class, true);
        final STypeYearMonth dtFimCargo = cargo.addField("fim", STypeYearMonth.class);


        final STypeList<STypeComposite<SIComposite>, SIComposite> pets = cargo.addFieldListOfComposite("pets", "pet");
        final STypeComposite pet = pets.getElementsType();
        final STypeString nomeDoPet = pet.addFieldString("nome", true);
        final STypeString tipoDoPet = pet.addFieldString("tipo", true)
                .withSelectionOf("Gatinho", "Cachorrinho", "Papagaio");
        final STypeInteger idadePet = pet.addFieldInteger("idade");

        {
            //@destacar:bloco
            experiencias
                    .withView(SViewListByMasterDetail::new)
                    .asAtrBasic().label("Experiências profissionais");
            //@destacar:fim
            dtInicioExperiencia
                    .asAtrBasic().label("Data inicial")
                    .asAtrBootstrap().colPreference(2);
            dtFimExperiencia
                    .asAtrBasic().label("Data final")
                    .asAtrBootstrap().colPreference(2);
            empresa
                    .asAtrBasic().label("Empresa")
                    .asAtrBootstrap().colPreference(8);
            //@destacar:bloco
            cargos
                    .withView(SViewListByMasterDetail::new)
                    .asAtrBasic().label("Cargos na empresa");
            dtInicioCargo
                    .asAtrBasic().label("Data inicial")
                    .asAtrBootstrap().colPreference(4);
            dtFimCargo
                    .asAtrBasic().label("Data final")
                    .asAtrBootstrap().colPreference(4);
            nome
                    .asAtrBasic().label("Nome")
                    .asAtrBootstrap().colPreference(4);
            pets
                    .withView(new SViewListByMasterDetail()
                            .col(nomeDoPet)
                            .col(tipoDoPet))
                    .asAtrBasic().label("Animais de estimação no trabalho");
            nomeDoPet
                    .asAtrBasic().label("Nome")
                    .asAtrBootstrap().colPreference(4);
            tipoDoPet
                    .withSelectView()
                    .asAtrBasic().label("Tipo")
                    .asAtrBootstrap().colPreference(4);
            idadePet
                    .asAtrBasic().label("Idade")
                    .asAtrBootstrap().colPreference(4);
            //@destacar:fim
            atividades
                    .withTextAreaView()
                    .asAtrBasic().label("Atividades Desenvolvidas");
        }

    }
}
