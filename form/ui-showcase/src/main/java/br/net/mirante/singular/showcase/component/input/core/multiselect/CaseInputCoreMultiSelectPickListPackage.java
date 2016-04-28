/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionByPicklistView;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreMultiSelectPickListPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        tipoMyForm.asAtrBasic().label("Salada de Frutas");
        tipoMyForm.addFieldListOf("frutas", STypeString.class).multiselectionOf(String.class, SMultiSelectionByPicklistView::new)
                .selfIdAndDisplay()
                .newSimpleProviderOf("Amora", "Banana", "Maçã", "Laranja", "Manga", "Melão", "Morango");
    }

}
