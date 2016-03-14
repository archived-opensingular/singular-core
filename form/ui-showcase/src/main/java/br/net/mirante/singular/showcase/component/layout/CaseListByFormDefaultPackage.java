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
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.SViewListByForm;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeYearMonth;

public class CaseListByFormDefaultPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        final STypeList<STypeComposite<SIComposite>, SIComposite> experiencias = testForm.addFieldListOfComposite("experienciasProfissionais", "experiencia");
        final STypeComposite<?> experiencia = experiencias.getElementsType();
        final STypeYearMonth dtInicioExperiencia = experiencia.addField("inicio", STypeYearMonth.class, true);
        final STypeYearMonth dtFimExperiencia = experiencia.addField("fim", STypeYearMonth.class);
        final STypeString empresa = experiencia.addFieldString("empresa", true);
        final STypeString cargo = experiencia.addFieldString("cargo", true);
        final STypeString atividades = experiencia.addFieldString("atividades");

        {
            experiencias
                    .withView(SViewListByForm::new)
                    .as(AtrBasic::new).label("Experiências profissionais");
            dtInicioExperiencia
                    .as(AtrBasic::new).label("Data inicial")
                    .as(AtrBootstrap::new).colPreference(2);
            dtFimExperiencia
                    .as(AtrBasic::new).label("Data final")
                    .as(AtrBootstrap::new).colPreference(2);
            empresa
                    .as(AtrBasic::new).label("Empresa")
                    .as(AtrBootstrap::new).colPreference(8);
            cargo
                    .as(AtrBasic::new).label("Cargo");
            atividades
                    .withTextAreaView()
                    .as(AtrBasic::new).label("Atividades Desenvolvidas");
        }

    }
}
