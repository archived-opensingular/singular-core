/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectDefaultPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> root = pb.createCompositeType("testForm");
//
//        addSelection(root, 3, true);
//        addSelection(root, 3, false);
//        addSelection(root, 10, false);
//
//        final STypeString favoriteFruit = root.addFieldString("favoriteFruit");
//        favoriteFruit.withSelectView();
//        favoriteFruit.asAtrBasic().label("Fruta Favorita");
//        favoriteFruit.asAtrProvider().fixedOptionsProvider((SimpleSimpleProvider<String>) i -> {
//            return Arrays.asList("Maçã", "Laranja", "Banana", "Goiaba");
//        });

    }

    private static void addSelection(STypeComposite<?> tipoMyForm, int sizeOptions, boolean required) {
        STypeString tipoSelection = tipoMyForm.addFieldString("opcoes" + sizeOptions + required);

        tipoSelection.withSelectionOf(createOptions(sizeOptions));
        tipoSelection.withRequired(required);

        tipoSelection.asAtrBasic().label("Seleção de " + sizeOptions);
    }

    private static String[] createOptions(int sizeOptions) {
        String[] options = new String[sizeOptions];
        for (int i = 1; i <= sizeOptions; i++) {
            options[i - 1] = "Opção " + i;
        }
        return options;
    }
}
