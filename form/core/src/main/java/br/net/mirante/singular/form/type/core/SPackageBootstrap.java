/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core;

import java.util.Optional;

import br.net.mirante.singular.form.AtrRef;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.calculation.CalculationContext;
import br.net.mirante.singular.form.calculation.SimpleValueCalculation;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.util.STypeEMail;
import br.net.mirante.singular.form.type.util.STypeYearMonth;
import br.net.mirante.singular.form.view.SMultiSelectionByCheckboxView;
import br.net.mirante.singular.form.view.SMultiSelectionBySelectView;
import br.net.mirante.singular.form.view.SView;
import br.net.mirante.singular.form.view.SViewTextArea;

@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "plaf.bootstrap")
public class SPackageBootstrap extends SPackage {

    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_PREFERENCE    = new AtrRef<>(SPackageBootstrap.class, "larguraColuna", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_XS_PREFERENCE = new AtrRef<>(SPackageBootstrap.class, "larguraColunaXS", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_SM_PREFERENCE = new AtrRef<>(SPackageBootstrap.class, "larguraColunaSM", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_MD_PREFERENCE = new AtrRef<>(SPackageBootstrap.class, "larguraColunaMD", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_LG_PREFERENCE = new AtrRef<>(SPackageBootstrap.class, "larguraColunaLG", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_COL_ON_NEW_ROW    = new AtrRef<>(SPackageBootstrap.class, "newRow", STypeBoolean.class, SIBoolean.class, Boolean.class);

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        adicionarDefinicaoColuna(pb, ATR_COL_PREFERENCE, null);
        adicionarDefinicaoColuna(pb, ATR_COL_XS_PREFERENCE, "XS");
        adicionarDefinicaoColuna(pb, ATR_COL_SM_PREFERENCE, "SM");
        adicionarDefinicaoColuna(pb, ATR_COL_MD_PREFERENCE, "MD");
        adicionarDefinicaoColuna(pb, ATR_COL_LG_PREFERENCE, "LG");

        pb.createAttributeIntoType(SType.class, ATR_COL_ON_NEW_ROW);

        //@formatter:off
        pb.getType(STypeEMail.class    ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 6);
        pb.getType(STypeSimple.class   ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 6);
        pb.getType(STypeDateTime.class ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 6);
        pb.getType(STypeDate.class     ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeTime.class     ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeYearMonth.class).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeBoolean.class  ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeDecimal.class  ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeInteger.class  ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeMonetary.class ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeString.class   ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, IntCalc.nil()
            .forView(SViewTextArea.class, 12)
            .orElse(6));
        //@formatter:on
    }

    private static interface IntCalc extends SimpleValueCalculation<Integer> {
        static IntCalc nil() {
            return c -> null;
        }
        default IntCalc forView(Class<? extends SView> view, int val) {
            return c -> Optional.ofNullable(this.calculate(c))
                .orElseGet(() -> ((c.instance().getType().getView() != null) && (view.isAssignableFrom(c.instance().getType().getView().getClass())))
                    ? val
                    : null);
        }
        default IntCalc orElse(int val) {
            return c -> Optional.ofNullable(this.calculate(c)).orElse(val);
        }
    }

    private void adicionarDefinicaoColuna(PackageBuilder pb, AtrRef<?, ?, ?> atrRef, String label) {
        Optional<String> labelOp = Optional.ofNullable(label);
        pb.createAttributeType(atrRef);
        pb.addAttribute(SType.class, atrRef);
        pb.getAttribute(atrRef).as(SPackageBasic.aspect()).label(("Largura preferencial " + labelOp.orElse("")).trim());
    }

}
