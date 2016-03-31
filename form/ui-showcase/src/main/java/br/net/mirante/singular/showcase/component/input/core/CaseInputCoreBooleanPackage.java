/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;

//@formatter:off
public class CaseInputCoreBooleanPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldBoolean("aceitaTermos")
            .asAtrBasic().label("Aceito os termos e condições");

        tipoMyForm.addFieldBoolean("receberNotificacoes")
            //@destacar
            .withRadioView()
            .asAtrBasic().label("Receber notificações");

        tipoMyForm.addFieldBoolean("aceitaTermos2")
            //@destacar
            .withRadioView("Aceito", "Rejeito")
            .asAtrBasic().label("Aceito os termos e condições");
    }
}