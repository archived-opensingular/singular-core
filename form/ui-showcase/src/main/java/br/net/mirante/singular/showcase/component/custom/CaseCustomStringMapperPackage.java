/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.custom;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;

public class CaseCustomStringMapperPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<SIComposite> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldString("nomeCompleto")
                //@destacar
                .withCustomMapper(MaterialDesignInputMapper::new)
                .asAtrBasic().label("Nome Completo");

    }
}
