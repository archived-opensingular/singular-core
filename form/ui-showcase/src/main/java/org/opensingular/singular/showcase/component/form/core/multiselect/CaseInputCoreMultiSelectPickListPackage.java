/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.core.multiselect;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.enums.PhraseBreak;
import org.opensingular.singular.form.type.core.SIString;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.view.SMultiSelectionByPicklistView;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

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
