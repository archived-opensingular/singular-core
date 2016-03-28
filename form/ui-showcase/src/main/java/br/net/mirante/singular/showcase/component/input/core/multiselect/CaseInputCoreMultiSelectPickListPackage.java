/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionByPicklistView;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreMultiSelectPickListPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        STypeString contato = pb.createType("contato", STypeString.class);
        contato.withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        STypeList<STypeString, SIString> contatos = tipoMyForm.addFieldListOf("contatos", contato);

        contatos
            .withView(SMultiSelectionByPicklistView::new)
            .asAtrBasic().label("Informações Públicas");
    }

}
