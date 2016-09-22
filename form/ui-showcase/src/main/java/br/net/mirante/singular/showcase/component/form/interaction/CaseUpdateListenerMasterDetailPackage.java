/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.interaction;

import java.math.BigDecimal;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.type.core.STypeMonetary;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

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