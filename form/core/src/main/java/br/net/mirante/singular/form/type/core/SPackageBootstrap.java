/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core;

import static org.apache.commons.lang3.ObjectUtils.*;

import java.util.function.Predicate;

import br.net.mirante.singular.form.AtrRef;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.calculation.CalculationContext;
import br.net.mirante.singular.form.calculation.SimpleValueCalculation;
import br.net.mirante.singular.form.provider.Provider;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.util.STypeEMail;
import br.net.mirante.singular.form.type.util.STypeYearMonth;
import br.net.mirante.singular.form.view.SView;
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
    protected void carregarDefinicoes(PackageBuilder pb) {
        adicionarDefinicaoColuna(pb, ATR_COL_PREFERENCE, null);
        adicionarDefinicaoColuna(pb, ATR_COL_XS_PREFERENCE, "XS");
        adicionarDefinicaoColuna(pb, ATR_COL_SM_PREFERENCE, "SM");
        adicionarDefinicaoColuna(pb, ATR_COL_MD_PREFERENCE, "MD");
        adicionarDefinicaoColuna(pb, ATR_COL_LG_PREFERENCE, "LG");

        pb.createAttributeIntoType(SType.class, ATR_COL_ON_NEW_ROW);

        //@formatter:off
        pb.getType(STypeEMail.class    ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 6);
        pb.getType(STypeDateTime.class ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 6);
        pb.getType(STypeDate.class     ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeTime.class     ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeYearMonth.class).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeBoolean.class  ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeDecimal.class  ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeInteger.class  ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeMonetary.class ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, c -> 4);
        pb.getType(STypeSimple.class   ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, IntCalc.nil()
            .onHasProvider(12)
            .orElse(6));
        pb.getType(STypeString.class   ).asAtrBootstrap().setAttributeCalculation(ATR_COL_PREFERENCE, IntCalc.nil()
            .onHasProvider(12)
            .onView(SViewTextArea.class, 12)
            .orElse(6));
        //@formatter:on
    }

    public static interface IntCalc extends SimpleValueCalculation<Integer> {
        static IntCalc nil() {
            return c -> null;
        }
        default IntCalc on(Predicate<CalculationContext> condition, int val) {
            return c -> {
                Integer res = this.calculate(c);
                if (res != null)
                    return res;
                return (condition.test(c)) ? val : null;
            };
        }
        default IntCalc onView(Class<? extends SView> viewClass, int val) {
            return on(c -> (c.instance().getType().getView() != null) && (viewClass.isAssignableFrom(c.instance().getType().getView().getClass())), val);
        }
        default IntCalc onHasProvider(int val) {
            return on(c -> c.instance().getType().asAtrProvider().getProvider() != null, val);
        }
        default IntCalc orElse(int val) {
            return c -> defaultIfNull(this.calculate(c), val);
        }
    }

    private <T extends SType<I>, I extends SInstance, V extends Object> void adicionarDefinicaoColuna(PackageBuilder pb, AtrRef<T, I, V> atrRef, String label) {
        pb.createAttributeType(atrRef);
        pb.addAttribute(SType.class, atrRef);
        pb.getAttribute(atrRef)
            .as(SPackageBasic.aspect()).label(("Largura preferencial " + defaultIfNull(label, "")).trim());
    }

}
