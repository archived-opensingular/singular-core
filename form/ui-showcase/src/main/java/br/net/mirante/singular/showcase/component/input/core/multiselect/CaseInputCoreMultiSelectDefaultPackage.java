/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreMultiSelectDefaultPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        addMultiSelection(pb, tipoMyForm, 3);
        addMultiSelection(pb, tipoMyForm, 15);
        addMultiSelection(pb, tipoMyForm, 25);
        
    }

    private static void addMultiSelection(PackageBuilder pb, STypeComposite<?> tipoMyForm, int size) {
        STypeString tipoSelection = pb.createType("opcoes" + size, STypeString.class);
        tipoSelection.withSelectionOf(createOptions(size));

        STypeList<STypeString, SIString> multiSelection = tipoMyForm.addFieldListOf("multiSelection" + size, tipoSelection);
        multiSelection.as(AtrBasic::new).label("Seleção de " + size);
    }

    private static String[] createOptions(int sizeOptions) {
        String[] options = new String[sizeOptions];
        for(int i = 1; i <= sizeOptions; i++) {
            options[i - 1] = "Opção " + i;
        }
        return options;
    }
}
