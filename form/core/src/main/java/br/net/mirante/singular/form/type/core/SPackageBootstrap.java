/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core;

import static org.apache.commons.lang3.ObjectUtils.*;

import br.net.mirante.singular.form.AtrRef;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.calculation.SimpleValueCalculation;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.view.SMultiSelectionByCheckboxView;
import br.net.mirante.singular.form.view.SMultiSelectionByPicklistView;
import br.net.mirante.singular.form.view.SMultiSelectionBySelectView;
import br.net.mirante.singular.form.view.SViewAttachmentList;
import br.net.mirante.singular.form.view.SViewAutoComplete;
import br.net.mirante.singular.form.view.SViewSearchModal;
import br.net.mirante.singular.form.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.view.SViewSelectionBySelect;
import br.net.mirante.singular.form.view.SViewTextArea;

@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "plaf.bootstrap")
public class SPackageBootstrap extends SPackage {

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
            .appendOnView(SViewSelectionByRadio.class, 12)
            .appendOnView(SViewSelectionBySelect.class, 6)
            .appendOnView(SViewAutoComplete.class, 6)
            .appendOnView(SViewSearchModal.class, 6);

        final SimpleValueCalculation<Integer> calcsForMultiple = SimpleValueCalculation.nil(Integer.class)
            .appendOnView(SMultiSelectionByPicklistView.class, 12)
            .appendOnView(SMultiSelectionByCheckboxView.class, 4)
            .appendOnView(SMultiSelectionBySelectView.class, 4);

        pb.getType(STypeList.class).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, calcsForMultiple
            .orElse(12));

        pb.getType(STypeComposite.class).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, calcsForSingle
            .prependOnView(SViewAttachmentList.class, 12)
            .orElse(12));

        pb.getType(STypeSimple.class).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, calcsForSingle
            .orElse(4));

        pb.getType(STypeString.class).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, calcsForSingle
            .prependOnView(SViewTextArea.class, 12)
            .orElse(6));
    }

    private <T extends SType<I>, I extends SInstance, V extends Object> void adicionarDefinicaoColuna(PackageBuilder pb, AtrRef<T, I, V> atrRef, String label) {
        pb.createAttributeType(atrRef);
        pb.addAttribute(SType.class, atrRef);
        pb.getAttribute(atrRef)
            .asAtr().label(("Largura preferencial " + defaultIfNull(label, "")).trim());
    }

}
