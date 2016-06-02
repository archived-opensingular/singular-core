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
 * Campo de texto simples
 */
@CaseItem(componentName = "String", subCaseName = "Simples", group = Group.INPUT)
public class CaseInputCoreStringPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldString("nomeCompleto")
                .asAtr().label("Nome Completo").tamanhoMaximo(100);

        tipoMyForm.addFieldString("endereco")
                .asAtr().label("Endereço").tamanhoMaximo(250);

    }
}
