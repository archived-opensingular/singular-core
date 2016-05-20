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
 * Campo para inserção de dados booleanos.
 */
//@formatter:off
@CaseItem(componentName = "Boolean", group = Group.INPUT)
public class CaseInputCoreBooleanPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldBoolean("aceitaTermos")
            .asAtr().label("Aceito os termos e condições");

        tipoMyForm.addFieldBoolean("receberNotificacoes")
            //@destacar
            .withRadioView()
            .asAtr().label("Receber notificações");

        tipoMyForm.addFieldBoolean("aceitaTermos2")
            //@destacar
            .withRadioView("Aceito", "Rejeito")
            .asAtr().label("Aceito os termos e condições");
    }
}
