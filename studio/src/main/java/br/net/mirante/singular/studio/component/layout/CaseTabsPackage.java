/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.layout;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.util.STypeEMail;
import br.net.mirante.singular.form.type.util.STypeYearMonth;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.view.SViewTab;

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
