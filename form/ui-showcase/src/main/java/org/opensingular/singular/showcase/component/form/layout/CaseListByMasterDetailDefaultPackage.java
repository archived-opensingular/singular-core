/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.layout;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.util.STypeYearMonth;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

/**
 * List by Master Detail
 */
@CaseItem(componentName = "List by Master Detail", subCaseName = "Default", group = Group.LAYOUT)
public class CaseListByMasterDetailDefaultPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        STypeList<STypeComposite<SIComposite>, SIComposite> experiencias        = testForm.addFieldListOfComposite("experienciasProfissionais", "experiencia");
        STypeComposite<?>                                   experiencia         = experiencias.getElementsType();
        STypeYearMonth                                      dtInicioExperiencia = experiencia.addField("inicio", STypeYearMonth.class, true);
        STypeYearMonth                                      dtFimExperiencia    = experiencia.addField("fim", STypeYearMonth.class);
        STypeString                                         empresa             = experiencia.addFieldString("empresa", true);
        STypeString                                         cargo               = experiencia.addFieldString("cargo", true);
        STypeString atividades = experiencia.addFieldString("atividades");

        {
            //@destacar:bloco
            experiencias
                    .withView(SViewListByMasterDetail::new)
            //@destacar:fim
                    .asAtr().label("Experiências profissionais").itemLabel("Experiência Profissional");
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

    }
}
