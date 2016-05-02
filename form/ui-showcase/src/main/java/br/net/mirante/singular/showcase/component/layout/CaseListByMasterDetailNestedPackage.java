/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeYearMonth;

public class CaseListByMasterDetailNestedPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        STypeList<STypeComposite<SIComposite>, SIComposite> experiencias = testForm.addFieldListOfComposite("experienciasProfissionais", "experiencia");
        STypeComposite<?> experiencia = experiencias.getElementsType();
        STypeYearMonth dtInicioExperiencia = experiencia.addField("inicio", STypeYearMonth.class, true);
        STypeYearMonth dtFimExperiencia = experiencia.addField("fim", STypeYearMonth.class);
        STypeString empresa = experiencia.addFieldString("empresa", true);
        STypeString atividades = experiencia.addFieldString("atividades");

        STypeList<STypeComposite<SIComposite>, SIComposite> cargos = experiencia.addFieldListOfComposite("cargos", "cargo");
        STypeComposite<?> cargo = cargos.getElementsType();
        STypeString nome = cargo.addFieldString("nome", true);
        STypeYearMonth dtInicioCargo = cargo.addField("inicio", STypeYearMonth.class, true);
        STypeYearMonth dtFimCargo = cargo.addField("fim", STypeYearMonth.class);


        STypeList<STypeComposite<SIComposite>, SIComposite> pets = cargo.addFieldListOfComposite("pets", "pet");
        STypeComposite<?> pet = pets.getElementsType();
        STypeString nomeDoPet = pet.addFieldString("nome", true);
        STypeString tipoDoPet = pet.addFieldString("tipo", true);
        tipoDoPet.selectionOf("Gatinho", "Cachorrinho", "Papagaio");
        STypeInteger idadePet = pet.addFieldInteger("idade");

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
                    .asAtrBasic()
                    .label("Animais de estimação no trabalho");
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
