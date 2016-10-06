/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.type.core;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import org.opensingular.singular.form.AtrRef;
import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SDictionary;
import org.opensingular.singular.form.SInfoPackage;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.STypeSimple;
import org.opensingular.singular.form.calculation.SimpleValueCalculation;
import org.opensingular.singular.form.type.util.STypeYearMonth;
import org.opensingular.singular.form.view.SMultiSelectionByCheckboxView;
import org.opensingular.singular.form.view.SMultiSelectionByPicklistView;
import org.opensingular.singular.form.view.SMultiSelectionBySelectView;
import org.opensingular.singular.form.view.SViewAttachmentList;
import org.opensingular.singular.form.view.SViewAutoComplete;
import org.opensingular.singular.form.view.SViewSearchModal;
import org.opensingular.singular.form.view.SViewSelectionByRadio;
import org.opensingular.singular.form.view.SViewSelectionBySelect;
import org.opensingular.singular.form.view.SViewTextArea;

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
