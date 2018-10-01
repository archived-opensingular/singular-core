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

import org.opensingular.form.AtrRef;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.calculation.SimpleValueCalculationInstanceOptional;
import org.opensingular.form.type.basic.AtrBasic;
import org.opensingular.form.type.util.STypeYearMonth;
import org.opensingular.form.view.SMultiSelectionByCheckboxView;
import org.opensingular.form.view.SMultiSelectionByPicklistView;
import org.opensingular.form.view.SMultiSelectionBySelectView;
import org.opensingular.form.view.SViewAttachmentList;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.view.SViewSelectionByRadio;
import org.opensingular.form.view.SViewSelectionBySelect;
import org.opensingular.form.view.SViewTextArea;

import javax.annotation.Nonnull;

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
    protected void onLoadPackage(@Nonnull PackageBuilder pb) {
        addAtrColumnPreference(pb, ATR_COL_PREFERENCE, "Largura preferencial")
//            .help("Valor entre <b>1 e 12</b> ou em branco")
                .maxLength(2);
        addAtrColumnPreference(pb, ATR_COL_XS_PREFERENCE, "Largura preferencial XS");
        addAtrColumnPreference(pb, ATR_COL_SM_PREFERENCE, "Largura preferencial SM");
        addAtrColumnPreference(pb, ATR_COL_MD_PREFERENCE, "Largura preferencial MD");
        addAtrColumnPreference(pb, ATR_COL_LG_PREFERENCE, "Largura preferencial LG");

        pb.createAttributeIntoType(SType.class, ATR_COL_ON_NEW_ROW).asAtr().label("Exibir em Nova Linha");

        final SimpleValueCalculationInstanceOptional<Integer> calcsForSingle = SimpleValueCalculationInstanceOptional.nil(Integer.class)
            .appendOnView(SViewSelectionByRadio.class, SPackageBootstrap.MAX_COL_PREFERENCE)
            .appendOnView(SViewSelectionBySelect.class, 6)
            .appendOnView(SViewAutoComplete.class, 6)
            .appendOnView(SViewSearchModal.class, 6);

        SimpleValueCalculationInstanceOptional<Integer> calcForSingleDefault2 = calcsForSingle.orElse(2);
        SimpleValueCalculationInstanceOptional<Integer> calcForSingleDefault3 = calcsForSingle.orElse(3);
        SimpleValueCalculationInstanceOptional<Integer> calcForSingleDefault4 = calcsForSingle.orElse(4);
        SimpleValueCalculationInstanceOptional<Integer> calcForSingleDefault6 = calcsForSingle.orElse(6);

        SimpleValueCalculationInstanceOptional<Integer> calcsForMultiple = SimpleValueCalculationInstanceOptional.nil(Integer.class)
            .appendOnView(SMultiSelectionByPicklistView.class, SPackageBootstrap.MAX_COL_PREFERENCE)
            .appendOnView(SMultiSelectionByCheckboxView.class, 4)
            .appendOnView(SMultiSelectionBySelectView.class, 4)
            .orElse(MAX_COL_PREFERENCE);

        pb.getType(STypeList.class).setAttributeCalculationInstanceOptional(ATR_COL_PREFERENCE, calcsForMultiple);

        pb.getType(STypeComposite.class).setAttributeCalculationInstanceOptional(ATR_COL_PREFERENCE, calcsForSingle
            .prependOnView(SViewAttachmentList.class, MAX_COL_PREFERENCE)
            .orElse(MAX_COL_PREFERENCE));

        pb.getType(STypeSimple.class).setAttributeCalculationInstanceOptional(ATR_COL_PREFERENCE, calcForSingleDefault4);

        pb.getType(STypeString.class).setAttributeCalculationInstanceOptional(ATR_COL_PREFERENCE, calcForSingleDefault6
            .prependOnView(SViewTextArea.class, SPackageBootstrap.MAX_COL_PREFERENCE));

        pb.getType(STypeInteger.class).setAttributeCalculationInstanceOptional(ATR_COL_PREFERENCE, calcForSingleDefault3);
        pb.getType(STypeLong.class).setAttributeCalculationInstanceOptional(ATR_COL_PREFERENCE, calcForSingleDefault3);
        pb.getType(STypeDate.class).setAttributeCalculationInstanceOptional(ATR_COL_PREFERENCE, calcForSingleDefault3);
        pb.getType(STypeTime.class).setAttributeCalculationInstanceOptional(ATR_COL_PREFERENCE, calcForSingleDefault2);
        pb.getType(STypeDateTime.class).setAttributeCalculationInstanceOptional(ATR_COL_PREFERENCE, calcForSingleDefault3);
        pb.getType(STypeYearMonth.class).setAttributeCalculationInstanceOptional(ATR_COL_PREFERENCE, calcForSingleDefault3);
    }

    private <T extends SType<I>, I extends SInstance, V> AtrBasic addAtrColumnPreference(PackageBuilder pb, AtrRef<T, I, V> atrRef, String label) {
        return pb.createAttributeIntoType(SType.class, atrRef).asAtr().label(label);
    }

}
