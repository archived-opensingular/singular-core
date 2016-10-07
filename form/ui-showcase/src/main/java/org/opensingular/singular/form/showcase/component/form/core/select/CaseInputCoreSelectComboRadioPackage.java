/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component.form.core.select;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewSelectionByRadio;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Radio
 */
@CaseItem(componentName = "Select", subCaseName = "Combo e Radio", group = Group.INPUT)
public class CaseInputCoreSelectComboRadioPackage extends SPackage {

    //@formatter:off
    @Override
    protected void onLoadPackage(PackageBuilder pb) {
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
