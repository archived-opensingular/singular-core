/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core.select;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Pemite a seleção de valores compostos de varios tipos diferentes.
 */
@CaseItem(componentName = "Select", subCaseName = "Tipo Composto", group = Group.INPUT)
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
                .simpleProvider(listaBuilder -> {
                    listaBuilder.add().set(formulaQuimica, "H20").set(nome, "Água");
                    listaBuilder.add().set(formulaQuimica, "H2O2").set(nome, "Água Oxigenada");
                    listaBuilder.add().set(formulaQuimica, "O2").set(nome, "Gás Oxigênio");
                    listaBuilder.add().set(formulaQuimica, "C12H22O11").set(nome, "Açúcar");
                });

    }

}
