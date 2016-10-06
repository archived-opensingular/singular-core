/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component.form.core.multiselect;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.enums.PhraseBreak;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SMultiSelectionByPicklistView;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Permite a seleção múltipla no formato de uma pick list.
 */
@CaseItem(componentName = "Multi Select", subCaseName = "Pick List", group = Group.INPUT)
public class CaseInputCoreMultiSelectPickListPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        final STypeList<STypeString, SIString> frutas = tipoMyForm.addFieldListOf("frutas", STypeString.class);
        frutas.asAtr().phraseBreak(PhraseBreak.BREAK_LINE);
        frutas.selectionOf(String.class, new SMultiSelectionByPicklistView())
                .selfIdAndDisplay()
                .simpleProviderOf("Amora", "Banana", "Maçã", "Laranja", "Manga", "Melão", "Morango");
    }

}
