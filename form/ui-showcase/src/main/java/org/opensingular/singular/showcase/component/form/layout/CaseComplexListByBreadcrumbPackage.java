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
import org.opensingular.form.view.SViewBreadcrumb;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

/**
 * Breadcrumb
 */
@CaseItem(componentName = "Breadcrumb", subCaseName = "Complexo", group = Group.LAYOUT)
public class CaseComplexListByBreadcrumbPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        STypeList<STypeComposite<SIComposite>, SIComposite> componentes = testForm.addFieldListOfComposite("componentes", "componente");
        STypeComposite<SIComposite>                         componente  = componentes.getElementsType();
        componente.asAtr().label("Componente");

        componente.addFieldString("nome")
                .asAtr().label("Nome");

        STypeList<STypeComposite<SIComposite>, SIComposite> testes = componente.addFieldListOfComposite("testes", "teste");
        testes
                .withView(SViewBreadcrumb::new)
                .asAtr().label("Testes de componente");

        STypeComposite<SIComposite> teste = testes.getElementsType();
        teste.asAtr().label("Teste de Componentes");

        teste.addFieldString("nome")
                .asAtr().label("Nome");


        componentes
                .withView(SViewBreadcrumb::new)
                .asAtr().label("Componentes");

    }
}
