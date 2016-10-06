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
import org.opensingular.singular.form.type.core.STypeDate;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.type.util.STypeYearMonth;
import org.opensingular.singular.form.view.SViewListByTable;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

/**
 * List by Table
 */
@CaseItem(componentName = "List by Table", subCaseName = "Tamanho mínimo e máximo", group = Group.LAYOUT)
public class CaseListByTableMinimiumAndMaximumPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        STypeList<STypeComposite<SIComposite>, SIComposite> certificacoes = testForm.addFieldListOfComposite("certificacoes", "certificacao");
        certificacoes.asAtr().label("Certificações");
        STypeComposite<?> certificacao = certificacoes.getElementsType();
        STypeYearMonth dataCertificacao = certificacao.addField("data", STypeYearMonth.class, true);
        STypeString entidadeCertificacao = certificacao.addFieldString("entidade", true);
        STypeDate validadeCertificacao = certificacao.addFieldDate("validade");
        STypeString nomeCertificacao = certificacao.addFieldString("nome", true);
        {
            certificacoes
                    //@destacar:bloco
                    .withMiniumSizeOf(2)
                    .withMaximumSizeOf(3)
                     //@destacar:fim
                    .withView(SViewListByTable::new)
                    .asAtr().label("Certificações");
            certificacao
                    .asAtr().label("Certificação");
            dataCertificacao
                    .asAtr().label("Data")
                    .asAtrBootstrap().colPreference(2);
            entidadeCertificacao
                    .asAtr().label("Entidade")
                    .asAtrBootstrap().colPreference(4);
            validadeCertificacao
                    .asAtr().label("Validade")
                    .asAtrBootstrap().colPreference(2);
            nomeCertificacao
                    .asAtr().label("Nome")
                    .asAtrBootstrap().colPreference(4);
        }
    }
}
