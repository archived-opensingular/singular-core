/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseInputCoreTextAreaPackage extends SPackage {

    @Override
    //@formatter:off
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldString("observacao1")
                .withTextAreaView()
                .asAtrBasic().label("Observação (default)");

        tipoMyForm.addFieldString("observacao2")
            .withTextAreaView(view->view.setLines(2))
            .asAtrBasic()
            .label("Observação (2 linhas e 500 de limite)")
            .tamanhoMaximo(500);

        tipoMyForm.addFieldString("observacao3")
                .withTextAreaView(view->view.setLines(10))
                .asAtrBasic()
                .label("Observação (10 linhas e 5000 de limite)")
                .tamanhoMaximo(5000);
    }
}