/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.layout;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.type.util.STypeEMail;
import org.opensingular.singular.form.type.util.STypeYearMonth;
import org.opensingular.singular.form.view.SViewListByMasterDetail;
import org.opensingular.singular.form.view.SViewTab;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

/**
 * Tabs
 */
@CaseItem(componentName = "Tabs", group = Group.LAYOUT)
public class CaseTabsPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        STypeString nome;
        STypeInteger idade;
        STypeEMail email;
        (nome = testForm.addFieldString("nome"))
                .asAtr().label("Nome");
        (idade = testForm.addFieldInteger("idade"))
                .asAtr().label("Idade");
        (email = testForm.addFieldEmail("email"))
                .asAtr().label("E-mail");

        STypeList<STypeComposite<SIComposite>, SIComposite> experiencias        = testForm.addFieldListOfComposite("experienciasProfissionais", "experiencia");
        STypeComposite<?>                                   experiencia         = experiencias.getElementsType();
        STypeYearMonth                                      dtInicioExperiencia = experiencia.addField("inicio", STypeYearMonth.class, true);
        STypeYearMonth                                      dtFimExperiencia    = experiencia.addField("fim", STypeYearMonth.class);
        STypeString                                         empresa             = experiencia.addFieldString("empresa", true);
        STypeString                                         cargo               = experiencia.addFieldString("cargo", true);
        STypeString atividades = experiencia.addFieldString("atividades");

        {
            experiencias.withView(SViewListByMasterDetail::new)
                    .asAtr().label("Experiências profissionais");
            dtInicioExperiencia
                    .asAtr().label("Data inicial")
                    .asAtrBootstrap().colPreference(2);
            dtFimExperiencia
                    .asAtr().label("Data final")
                    .asAtrBootstrap().colPreference(2);
            empresa
                    .asAtr().label("Empresa")
                    .asAtrBootstrap().colPreference(8);
            cargo
                    .asAtr().label("Cargo");
            atividades
                    .withTextAreaView()
                    .asAtr().label("Atividades Desenvolvidas");
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
