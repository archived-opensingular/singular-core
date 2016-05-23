/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Campo Text Area
 */
@CaseItem(componentName = "String", subCaseName = "Text Area", group = Group.INPUT)
public class CaseInputCoreTextAreaPackage extends SPackage {

    @Override
    //@formatter:off
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldString("observacao1")
                .withTextAreaView()
                .asAtr().label("Observação (default)");

        tipoMyForm.addFieldString("observacao2")
            .withTextAreaView(view->view.setLines(2))
            .asAtr()
            .label("Observação (2 linhas e 500 de limite)")
            .tamanhoMaximo(500);

        tipoMyForm.addFieldString("observacao3")
                .withTextAreaView(view->view.setLines(10))
                .asAtr()
                .label("Observação (10 linhas e 5000 de limite)")
                .tamanhoMaximo(5000);
    }
}
