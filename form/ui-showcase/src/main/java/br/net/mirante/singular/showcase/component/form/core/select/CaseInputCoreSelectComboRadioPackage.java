/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core.select;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewSelectionByRadio;

public class CaseInputCoreSelectComboRadioPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        //View por Select
        STypeString tipoContato1 = tipoMyForm.addFieldString("tipoContato1");
        tipoContato1.selectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");
        //@destacar:bloco
        //@destacar:fim

        tipoContato1
                .withSelectView()
                .asAtr().label("Tipo Contato (Combo)");

        //@destacar:bloco
        //View por Radio
        STypeString tipoContato2 = tipoMyForm.addFieldString("tipoContato2");
        tipoContato2.selectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");
        //@destacar:fim

        tipoContato2
                .withRadioView()
                .asAtr().label("Tipo Contato (Radio) - Horizontal");



        STypeString tipoContato3 = tipoMyForm.addFieldString("tipoContato3");
        tipoContato3.selectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");


        tipoContato3
                .asAtr()
                .label("Tipo Contato (Radio) - Vertical");

        //@destacar:bloco
        //View por Radio com layout vertical
        tipoContato3
                .withView(new SViewSelectionByRadio().verticalLayout());
        //@destacar:fim

    }
}
