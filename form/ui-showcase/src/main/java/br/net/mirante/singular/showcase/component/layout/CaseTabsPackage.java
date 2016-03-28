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
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;
import br.net.mirante.singular.form.mform.util.comuns.STypeYearMonth;

public class CaseTabsPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        STypeString nome;
        STypeInteger idade;
        STypeEMail email;
        (nome = testForm.addFieldString("nome"))
                .as(AtrBasic.class).label("Nome");
        (idade = testForm.addFieldInteger("idade"))
                .as(AtrBasic.class).label("Idade");
        (email = testForm.addFieldEmail("email"))
                .as(AtrBasic.class).label("E-mail");

        STypeList<STypeComposite<SIComposite>, SIComposite> experiencias = testForm.addFieldListOfComposite("experienciasProfissionais", "experiencia");
        STypeComposite<?> experiencia = experiencias.getElementsType();
        STypeYearMonth dtInicioExperiencia = experiencia.addField("inicio", STypeYearMonth.class, true);
        STypeYearMonth dtFimExperiencia = experiencia.addField("fim", STypeYearMonth.class);
        STypeString empresa = experiencia.addFieldString("empresa", true);
        STypeString cargo = experiencia.addFieldString("cargo", true);
        STypeString atividades = experiencia.addFieldString("atividades");

        {
            experiencias.withView(SViewListByMasterDetail::new)
                    .asAtrBasic().label("Experiências profissionais");
            dtInicioExperiencia
                    .asAtrBasic().label("Data inicial")
                    .asAtrBootstrap().colPreference(2);
            dtFimExperiencia
                    .asAtrBasic().label("Data final")
                    .asAtrBootstrap().colPreference(2);
            empresa
                    .asAtrBasic().label("Empresa")
                    .asAtrBootstrap().colPreference(8);
            cargo
                    .asAtrBasic().label("Cargo");
            atividades
                    .withTextAreaView()
                    .asAtrBasic().label("Atividades Desenvolvidas");
        }

        //@destacar:bloco
        SViewTab tabbed = new SViewTab();
        tabbed.addTab("informacoes", "Informações pessoais")
                .add(nome)
                .add(email)
                .add(idade);
        tabbed.addTab(experiencias);
        testForm.withView(tabbed);
        //@destacar:fim
    }
}
