/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectCompositePackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        final STypeComposite<SIComposite> tipoMyForm         = pb.createCompositeType("testForm");
        final STypeComposite<SIComposite> ingredienteQuimico = tipoMyForm.addFieldComposite("ingredienteQuimico");

        ingredienteQuimico.asAtr().label("Ingrediente Quimico");

        final STypeString formulaQuimica = ingredienteQuimico.addFieldString("formulaQuimica");
        final STypeString nome           = ingredienteQuimico.addFieldString("nome");

        ingredienteQuimico.selection()
                .id(formulaQuimica)
                .display("${nome} - ${formulaQuimica}")
                .provider(listaBuilder -> {
                    listaBuilder.add().set(formulaQuimica, "H20").set(nome, "Água");
                    listaBuilder.add().set(formulaQuimica, "H2O2").set(nome, "Água Oxigenada");
                    listaBuilder.add().set(formulaQuimica, "O2").set(nome, "Gás Oxigênio");
                    listaBuilder.add().set(formulaQuimica, "C12H22O11").set(nome, "Açúcar");
                });

    }

}
