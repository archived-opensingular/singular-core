/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseInputCoreStringPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldString("nomeCompleto")
                .as(AtrBasic::new).label("Nome Completo")
                .as(AtrBasic::new).tamanhoMaximo(100);

        tipoMyForm.addFieldString("endereco")
                .as(AtrBasic::new).label("Endereço")
                .as(AtrBasic::new).tamanhoMaximo(250);

    }
}
