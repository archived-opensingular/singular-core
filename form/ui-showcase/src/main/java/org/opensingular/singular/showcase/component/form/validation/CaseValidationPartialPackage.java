/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.validation;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;
import org.opensingular.singular.showcase.component.Resource;

/**
 * É possível validar somente uma parte do formulário, no exemplo a seguir somente o campo "Obrigatório 1" será validado ao acionar a validação parcial.
 */
@CaseItem(componentName = "Partial", group = Group.VALIDATION,
    resources = @Resource(PartialValidationButton.class),
    customizer = CaseValidationPartialCustomizer.class)
public class CaseValidationPartialPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        //@destacar
        testForm.addFieldString("obrigatorio_1")
                .asAtr().label("Obrigatorio 1")
                .asAtr().required();
        testForm.addFieldInteger("obrigatorio_2")
                .asAtr().label("Obrigatorio 2")
                .asAtr().required();
        testForm.addFieldString("obrigatorio_3")
                .asAtr().label("Obrigatorio 3")
                .asAtr().required();

    }
}
