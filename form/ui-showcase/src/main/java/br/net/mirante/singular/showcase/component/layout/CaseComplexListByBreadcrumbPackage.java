/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.view.SViewBreadcrumb;

public class CaseComplexListByBreadcrumbPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

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
