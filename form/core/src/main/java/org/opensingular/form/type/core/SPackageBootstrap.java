/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.type.core;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import org.opensingular.form.AtrRef;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.view.SMultiSelectionByCheckboxView;
import org.opensingular.form.view.SMultiSelectionByPicklistView;
import org.opensingular.form.view.SViewAttachmentList;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.view.SViewSelectionByRadio;
import org.opensingular.form.view.SViewSelectionBySelect;
import org.opensingular.form.SType;
import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.type.util.STypeYearMonth;
import org.opensingular.form.view.SMultiSelectionBySelectView;
import org.opensingular.form.view.SViewTextArea;

@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "plaf.bootstrap")
public class SPackageBootstrap extends SPackage {

    public static final int MAX_COL_PREFERENCE = 12;
    
    //@formatter:off
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_PREFERENCE    = new AtrRef<>(SPackageBootstrap.class, "larguraColuna"  , STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_XS_PREFERENCE = new AtrRef<>(SPackageBootstrap.class, "larguraColunaXS", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_SM_PREFERENCE = new AtrRef<>(SPackageBootstrap.class, "larguraColunaSM", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_MD_PREFERENCE = new AtrRef<>(SPackageBootstrap.class, "larguraColunaMD", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_LG_PREFERENCE = new AtrRef<>(SPackageBootstrap.class, "larguraColunaLG", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_COL_ON_NEW_ROW    = new AtrRef<>(SPackageBootstrap.class, "newRow"         , STypeBoolean.class, SIBoolean.class, Boolean.class);
    //@formatter:on

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        adicionarDefinicaoColuna(pb, ATR_COL_PREFERENCE, null);
        adicionarDefinicaoColuna(pb, ATR_COL_XS_PREFERENCE, "XS");
        adicionarDefinicaoColuna(pb, ATR_COL_SM_PREFERENCE, "SM");
        adicionarDefinicaoColuna(pb, ATR_COL_MD_PREFERENCE, "MD");
        adicionarDefinicaoColuna(pb, ATR_COL_LG_PREFERENCE, "LG");

        pb.createAttributeIntoType(SType.class, ATR_COL_ON_NEW_ROW);

        final SimpleValueCalculation<Integer> calcsForSingle = SimpleValueCalculation.nil(Integer.class)
            .appendOnView(SViewSelectionByRadio.class, SPackageBootstrap.MAX_COL_PREFERENCE)
            .appendOnView(SViewSelectionBySelect.class, 6)
            .appendOnView(SViewAutoComplete.class, 6)
            .appendOnView(SViewSearchModal.class, 6);

        final SimpleValueCalculation<Integer> calcsForMultiple = SimpleValueCalculation.nil(Integer.class)
            .appendOnView(SMultiSelectionByPicklistView.class, SPackageBootstrap.MAX_COL_PREFERENCE)
            .appendOnView(SMultiSelectionByCheckboxView.class, 4)
            .appendOnView(SMultiSelectionBySelectView.class, 4);

        pb.getType(STypeList.class).setAttributeCalculation(ATR_COL_PREFERENCE, calcsForMultiple.orElse(12));

        pb.getType(STypeComposite.class).setAttributeCalculation(ATR_COL_PREFERENCE, calcsForSingle
            .prependOnView(SViewAttachmentList.class, SPackageBootstrap.MAX_COL_PREFERENCE)
            .orElse(12));

        pb.getType(STypeSimple.class).setAttributeCalculation(ATR_COL_PREFERENCE, calcsForSingle.orElse(4));

        pb.getType(STypeString.class).setAttributeCalculation(ATR_COL_PREFERENCE, calcsForSingle
            .prependOnView(SViewTextArea.class, SPackageBootstrap.MAX_COL_PREFERENCE)
            .orElse(6));

        pb.getType(STypeDate.class).setAttributeCalculation(ATR_COL_PREFERENCE, calcsForSingle.orElse(3));
        pb.getType(STypeYearMonth.class).setAttributeCalculation(ATR_COL_PREFERENCE, calcsForSingle.orElse(3));
    }

    private <T extends SType<I>, I extends SInstance, V extends Object> void adicionarDefinicaoColuna(PackageBuilder pb, AtrRef<T, I, V> atrRef, String label) {
        pb.createAttributeType(atrRef);
        pb.addAttribute(SType.class, atrRef);
        pb.getAttribute(atrRef)
            .asAtr().label(("Largura preferencial " + defaultIfNull(label, "")).trim());
    }

}
