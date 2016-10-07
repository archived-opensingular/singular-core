/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component.form.interaction;

import java.math.BigDecimal;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Listener que atualiza valores de itens de um mestre-detalhe.
 */
@CaseItem(componentName = "Listeners", subCaseName = "Master/detail", group = Group.INTERACTION)
public class CaseUpdateListenerMasterDetailPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        STypeMonetary salarioMaximo = testForm.addFieldMonetary("salarioMaximo");

        STypeList<STypeComposite<SIComposite>, SIComposite> funcionarios = testForm.addFieldListOfComposite("funcionarios", "funcionario");
        STypeComposite<?> funcionario = funcionarios.getElementsType();
        STypeString nome = funcionario.addFieldString("nome", true);
        STypeMonetary salario = funcionario.addFieldMonetary("salario");

        {
            salarioMaximo.asAtr().label("Teto salarial");

            funcionarios
                .withView(new SViewListByMasterDetail()
                    .col(nome)
                    .col(salario))
                .asAtr().label("Experiências profissionais");
            nome
                .asAtr().label("Nome")
                .asAtrBootstrap().colPreference(8);
            salario
                .withUpdateListener(iSalario -> iSalario.findNearest(salarioMaximo)
                    .ifPresent(iSalarioMaximo -> {
                        BigDecimal vs = iSalario.getValue();
                        BigDecimal vsm = iSalarioMaximo.getValue();
                        if ((vs != null) && (vsm != null) && (vs.compareTo(vsm) > 0))
                            iSalario.setValue(iSalarioMaximo.getValue());
                    }))
                .asAtr().label("Salário")
                .dependsOn(salarioMaximo);
        }
    }
}
