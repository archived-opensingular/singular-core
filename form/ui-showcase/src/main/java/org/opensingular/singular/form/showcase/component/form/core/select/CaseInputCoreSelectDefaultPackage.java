/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component.form.core.select;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Se a view não for definida, então define o componente dependendo da quantidade de dados e da obrigatoriedade.
 */
@CaseItem(componentName = "Select", subCaseName = "Default", group = Group.INPUT)
public class CaseInputCoreSelectDefaultPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> root = pb.createCompositeType("testForm");

        addSelection(root, 3, true);
        addSelection(root, 3, false);
        addSelection(root, 10, false);

        final STypeString favoriteFruit = root.addFieldString("favoriteFruit");
        favoriteFruit.withSelectView();
        favoriteFruit.asAtr().label("Fruta Favorita");
        favoriteFruit.selectionOf("Maçã", "Laranja", "Banana", "Goiaba");

    }

    private static void addSelection(STypeComposite<?> tipoMyForm, int sizeOptions, boolean required) {
        STypeString tipoSelection = tipoMyForm.addFieldString("opcoes" + sizeOptions + required);
        tipoSelection.selectionOf(createOptions(sizeOptions));
        tipoSelection.withRequired(required);
        tipoSelection.asAtr().label("Seleção de " + sizeOptions);
    }

    private static String[] createOptions(int sizeOptions) {
        String[] options = new String[sizeOptions];
        for (int i = 1; i <= sizeOptions; i++) {
            options[i - 1] = "Opção " + i;
        }
        return options;
    }
}
