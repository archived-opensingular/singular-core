/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.basic;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import br.net.mirante.singular.form.AtrRef;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SAttributeEnabled;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIPredicate;
import br.net.mirante.singular.form.SISupplier;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeBehavior;
import br.net.mirante.singular.form.STypePredicate;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.STypeSupplier;
import br.net.mirante.singular.form.type.core.SIBoolean;
import br.net.mirante.singular.form.type.core.SIInteger;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.type.core.STypeDate;
import br.net.mirante.singular.form.type.core.STypeDecimal;
import br.net.mirante.singular.form.type.core.STypeFormula;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;

@SuppressWarnings({"unchecked", "rawtypes"})
@SInfoPackage(name = SPackageBasic.NAME)
public class SPackageBasic extends SPackage {

    public static final String NAME = SDictionary.SINGULAR_PACKAGES_PREFIX + "basic";

    //@formatter:off
    public static final AtrRef<STypeString, SIString, String>    ATR_LABEL                  = new AtrRef<>(SPackageBasic.class, "label", STypeString.class, SIString.class, String.class);
    public static final AtrRef<?, ?, Object>                     ATR_DEFAULT_IF_NULL        = AtrRef.ofSelfReference(SPackageBasic.class, "defaultIfNull");
    public static final AtrRef<?, ?, Object>                     ATR_VALOR_INICIAL          = AtrRef.ofSelfReference(SPackageBasic.class, "valorInicial");
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean>                  ATR_TRIM                   = new AtrRef<>(SPackageBasic.class, "trim", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeFormula, SIComposite, Object>                 ATR_FORMULA                = new AtrRef<>(SPackageBasic.class, "formula", STypeFormula.class, SIComposite.class, Object.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_EMPTY_TO_NULL          = new AtrRef<>(SPackageBasic.class, "emptyToNull", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeString, SIString, String>    ATR_SUBTITLE               = new AtrRef<>(SPackageBasic.class, "subtitle", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeString, SIString, String>    ATR_BASIC_MASK             = new AtrRef<>(SPackageBasic.class, "basicMask", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_TAMANHO_MAXIMO         = new AtrRef<>(SPackageBasic.class, "tamanhoMaximo", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_TAMANHO_INTEIRO_MAXIMO = new AtrRef<>(SPackageBasic.class, "tamanhoInteiroMaximo", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_TAMANHO_DECIMAL_MAXIMO = new AtrRef<>(SPackageBasic.class, "tamanhoDecimalMaximo", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer>                  ATR_TAMANHO_EDICAO         = new AtrRef<>(SPackageBasic.class, "tamanhoEdicao", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean>                  ATR_VISIVEL                = new AtrRef<>(SPackageBasic.class, "visible", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean>                  ATR_ENABLED                = new AtrRef<>(SPackageBasic.class, "enabled", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer>                  ATR_ORDEM                  = new AtrRef<>(SPackageBasic.class, "ordemExibicao", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance>> ATR_VISIBLE_FUNCTION       = new AtrRef(SPackageBasic.class, "visivelFunction", STypePredicate.class, SIPredicate.class, Predicate.class);
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance>> ATR_ENABLED_FUNCTION       = new AtrRef(SPackageBasic.class, "enabledFunction", STypePredicate.class, SIPredicate.class, Predicate.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean>                  ATR_ANNOTATED              = new AtrRef<>(SPackageBasic.class, "anotated", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeString, SIString, String>                     ATR_ANNOTATION_LABEL       = new AtrRef<>(SPackageBasic.class, "annotation_label", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeString, SIString, String>                     ATR_DISPLAY_STRING         = new AtrRef<>(SPackageBasic.class, "displayString", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean>                  ATR_REQUIRED               = new AtrRef<>(SPackageBasic.class, "obrigatorio", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean>                  ATR_EXISTS                 = new AtrRef<>(SPackageBasic.class, "exists", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance>> ATR_EXISTS_FUNCTION        = new AtrRef(SPackageBasic.class, "existsFunction", STypePredicate.class, SIPredicate.class, Predicate.class);
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance>> ATR_OBRIGATORIO_FUNCTION   = new AtrRef(SPackageBasic.class, "obrigatorioFunction", STypePredicate.class, SIPredicate.class, Predicate.class);

    public static final AtrRef<STypeSupplier<Collection<SType<?>>>, SISupplier<Collection<SType<?>>>, Supplier<Collection<SType<?>>>>
            ATR_DEPENDS_ON_FUNCTION = new AtrRef(SPackageBasic.class, "dependsOnFunction", STypeSupplier.class, SISupplier.class, Supplier.class);

    //    public static final AtrRef<MTipoBehavior, MIBehavior, IBehavior<MInstancia>>   ATR_ONCHANGE_BEHAVIOR = new AtrRef(MPacoteBasic.class, "onchangeBehavior", MTipoBehavior.class, MIBehavior.class, IBehavior.class);
    //@formatter:on

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {


        pb.createType(STypeBehavior.class);
        pb.createType(STypeSupplier.class);

        pb.createAttributeIntoType(SType.class, ATR_DEFAULT_IF_NULL);
        pb.createAttributeIntoType(SType.class, ATR_REQUIRED);
        pb.createAttributeIntoType(SType.class, ATR_OBRIGATORIO_FUNCTION);
        pb.createAttributeIntoType(SType.class, ATR_EXISTS);
        pb.createAttributeIntoType(SType.class, ATR_EXISTS_FUNCTION);
        pb.createAttributeIntoType(STypeSimple.class, ATR_VALOR_INICIAL);
        pb.createAttributeIntoType(STypeSimple.class, ATR_FORMULA);
        pb.createAttributeIntoType(STypeString.class, ATR_TRIM).withDefaultValueIfNull(true);
        pb.createAttributeIntoType(STypeString.class, ATR_EMPTY_TO_NULL).withDefaultValueIfNull(true);

        pb.getAttribute(ATR_REQUIRED).withDefaultValueIfNull(false);
        pb.getAttribute(ATR_EXISTS).withDefaultValueIfNull(true);

        // Cria os tipos de atributos
        pb.createAttributeType(ATR_TAMANHO_MAXIMO);
        pb.createAttributeType(ATR_TAMANHO_INTEIRO_MAXIMO);
        pb.createAttributeType(ATR_TAMANHO_DECIMAL_MAXIMO);
        pb.createAttributeType(ATR_TAMANHO_EDICAO);

        // Aplica os atributos ao tipos
        pb.createAttributeIntoType(SType.class, ATR_LABEL);
        pb.createAttributeIntoType(SType.class, ATR_SUBTITLE);
        pb.createAttributeIntoType(SType.class, ATR_BASIC_MASK);
        pb.createAttributeIntoType(SType.class, ATR_VISIVEL);
        pb.createAttributeIntoType(SType.class, ATR_ENABLED);
        pb.createAttributeIntoType(SType.class, ATR_VISIBLE_FUNCTION);
        pb.createAttributeIntoType(SType.class, ATR_ENABLED_FUNCTION);
        pb.createAttributeIntoType(SType.class, ATR_DEPENDS_ON_FUNCTION);
        //        pb.createTipoAtributo(MTipo.class, ATR_ONCHANGE_BEHAVIOR);
        pb.createAttributeIntoType(SType.class, ATR_ORDEM);
        pb.createAttributeIntoType(SType.class, ATR_ANNOTATED);
        pb.createAttributeIntoType(SType.class, ATR_ANNOTATION_LABEL);

        pb.createAttributeIntoType(SType.class, ATR_DISPLAY_STRING);

        pb.addAttribute(STypeString.class, ATR_TAMANHO_MAXIMO, 100);
        pb.addAttribute(STypeString.class, ATR_TAMANHO_EDICAO, 50);

        pb.addAttribute(STypeInteger.class, ATR_TAMANHO_MAXIMO);
        pb.addAttribute(STypeInteger.class, ATR_TAMANHO_EDICAO);

        pb.addAttribute(STypeDate.class, ATR_TAMANHO_EDICAO, 10);

        pb.addAttribute(STypeDecimal.class, ATR_TAMANHO_INTEIRO_MAXIMO, 9);
        pb.addAttribute(STypeDecimal.class, ATR_TAMANHO_DECIMAL_MAXIMO, 2);

        pb.getDictionary().getType(SType.class).asAtr()
                .displayString(ctx -> ctx.instance().toStringDisplayDefault());

        // defina o meta dado do meta dado
        pb.getAttribute(ATR_LABEL).as(AtrBasic.class).label("Label").tamanhoEdicao(30).tamanhoMaximo(50);
        pb.getAttribute(ATR_SUBTITLE).as(AtrBasic.class).label("Subtítulo").tamanhoEdicao(30).tamanhoMaximo(50);
        pb.getAttribute(ATR_BASIC_MASK).as(AtrBasic.class).label("Máscara básica").tamanhoEdicao(10).tamanhoMaximo(20);
        pb.getAttribute(ATR_TAMANHO_MAXIMO).as(AtrBasic.class).label("Tamanho maximo").tamanhoEdicao(3).tamanhoMaximo(4);
        pb.getAttribute(ATR_TAMANHO_INTEIRO_MAXIMO).as(AtrBasic.class).label("Tamanho inteiro maximo").tamanhoEdicao(3).tamanhoMaximo(4);
        pb.getAttribute(ATR_TAMANHO_DECIMAL_MAXIMO).as(AtrBasic.class).label("Tamanho decimal maximo").tamanhoEdicao(3).tamanhoMaximo(4);
        pb.getAttribute(ATR_TAMANHO_EDICAO).as(AtrBasic.class).label("Tamanho edição").tamanhoEdicao(3).tamanhoMaximo(3);
        pb.getAttribute(ATR_VISIVEL).as(AtrBasic.class).label("Visível");
        pb.getAttribute(ATR_ENABLED).as(AtrBasic.class).label("Habilitado");
        pb.getAttribute(ATR_VISIBLE_FUNCTION).as(AtrBasic.class).label("Visível (função)");
        pb.getAttribute(ATR_ENABLED_FUNCTION).as(AtrBasic.class).label("Habilitado (função)");
        pb.getAttribute(ATR_DEPENDS_ON_FUNCTION).as(AtrBasic.class).label("Depende de (função)");
        //        pb.getAtributo(ATR_ONCHANGE_BEHAVIOR).as(AtrBasic.class).label("On change (comportamento)");
        pb.getAttribute(ATR_ORDEM).as(AtrBasic.class).label("Ordem");
    }

    public static Function<SAttributeEnabled, AtrBasic> aspect() {
        return AtrBasic::new;
    }
}
