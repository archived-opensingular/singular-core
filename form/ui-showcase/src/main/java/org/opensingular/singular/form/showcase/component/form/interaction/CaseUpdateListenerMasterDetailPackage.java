/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
