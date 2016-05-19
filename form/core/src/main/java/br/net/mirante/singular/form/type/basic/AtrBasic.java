/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.basic;

import br.net.mirante.singular.form.SAttributeEnabled;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STranslatorForAttribute;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.calculation.SimpleValueCalculation;
import br.net.mirante.singular.form.enums.PhraseBreak;
import br.net.mirante.singular.form.internal.freemarker.FormFreemarkerUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AtrBasic extends STranslatorForAttribute {

    public AtrBasic() {
    }

    public AtrBasic(SAttributeEnabled alvo) {
        super(alvo);
    }

    public AtrBasic label(String valor) {
        setAttributeValue(SPackageBasic.ATR_LABEL, valor);
        return this;
    }

    public AtrBasic itemLabel(String valor) {
        setAttributeValue(SPackageBasic.ATR_ITEM_LABEL, valor);
        return this;
    }

    public AtrBasic subtitle(String valor) {
        setAttributeValue(SPackageBasic.ATR_SUBTITLE, valor);
        return this;
    }

    public AtrBasic basicMask(String mask) {
        setAttributeValue(SPackageBasic.ATR_BASIC_MASK, mask);
        return this;
    }

    public AtrBasic tamanhoEdicao(Integer valor) {
        setAttributeValue(SPackageBasic.ATR_TAMANHO_EDICAO, valor);
        return this;
    }

    public AtrBasic tamanhoMaximo(Integer valor) {
        setAttributeValue(SPackageBasic.ATR_TAMANHO_MAXIMO, valor);
        return this;
    }

    public AtrBasic tamanhoInteiroMaximo(Integer valor) {
        setAttributeValue(SPackageBasic.ATR_TAMANHO_INTEIRO_MAXIMO, valor);
        return this;
    }

    public AtrBasic tamanhoDecimalMaximo(Integer valor) {
        setAttributeValue(SPackageBasic.ATR_TAMANHO_DECIMAL_MAXIMO, valor);
        return this;
    }

    public AtrBasic visible(Boolean valor) {
        setAttributeValue(SPackageBasic.ATR_VISIVEL, valor);
        return this;
    }

    public AtrBasic visible(Predicate<SInstance> valor) {
        setAttributeValue(SPackageBasic.ATR_VISIBLE_FUNCTION, valor);
        return this;
    }

    public AtrBasic enabled(Boolean valor) {
        setAttributeValue(SPackageBasic.ATR_ENABLED, valor);
        return this;
    }

    public AtrBasic enabled(Predicate<SInstance> valor) {
        setAttributeValue(SPackageBasic.ATR_ENABLED_FUNCTION, valor);
        return this;
    }

    public AtrBasic dependsOn(Supplier<Collection<SType<?>>> valor) {
        setAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION, valor);
        return this;
    }

    public AtrBasic dependsOn(SType<?>... tipos) {
        return dependsOn(() -> Arrays.asList(tipos));
    }

    public AtrBasic required() {
        return required(true);
    }

    public AtrBasic required(Boolean value) {
        setAttributeValue(SPackageBasic.ATR_REQUIRED, value);
        return this;
    }

    public AtrBasic required(Predicate<SInstance> valor) {
        setAttributeValue(SPackageBasic.ATR_OBRIGATORIO_FUNCTION, valor);
        return this;
    }

    public Boolean isRequired() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_REQUIRED));
    }

    public AtrBasic exists(Boolean valor) {
        setAttributeValue(SPackageBasic.ATR_REQUIRED, valor);
        return this;
    }

    public AtrBasic exists(Predicate<SInstance> valor) {
        setAttributeValue(SPackageBasic.ATR_OBRIGATORIO_FUNCTION, valor);
        return this;
    }

    public boolean exists() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_REQUIRED));
    }
    //    public AtrBasic onChange(Function<IBehavior<MInstancia>, IBehavior<MInstancia>> behaviorFunction) {
    //        IBehavior<MInstancia> existingBehavior = getOnChange();
    //        IBehavior<MInstancia> newBehavior = behaviorFunction.apply(IBehavior.noopIfNull(existingBehavior));
    //        getAlvo().setValorAtributo(MPacoteBasic.ATR_ONCHANGE_BEHAVIOR, newBehavior);
    //        return this;
    //    }
    //    public AtrBasic onChange(IBehavior<MInstancia> behavior) {
    //        onChange(old -> old.andThen(behavior));
    //        return this;
    //    }
    //    public boolean hasOnChange() {
    //        return getOnChange() != null;
    //    }
    //    @SuppressWarnings("unchecked")
    //    public IBehavior<MInstancia> getOnChange() {
    //        return (IBehavior<MInstancia>) getAlvo().getValorAtributo(MPacoteBasic.ATR_ONCHANGE_BEHAVIOR.getNomeCompleto());
    //    }

    public String getLabel() {
        return getAttributeValue(SPackageBasic.ATR_LABEL);
    }

    public String getItemLabel() {
        return getAttributeValue(SPackageBasic.ATR_ITEM_LABEL);
    }

    public String getSubtitle() {
        return getAttributeValue(SPackageBasic.ATR_SUBTITLE);
    }

    public Integer getTamanhoEdicao() {
        return getAttributeValue(SPackageBasic.ATR_TAMANHO_EDICAO);
    }

    public Integer getTamanhoMaximo() {
        return getAttributeValue(SPackageBasic.ATR_TAMANHO_MAXIMO);
    }

    public boolean isVisible() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_VISIVEL));
    }

    public boolean isEnabled() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_ENABLED));
    }

    public AtrBasic displayString(String displayStringTemplate) {
        return displayString(FormFreemarkerUtil.createInstanceCalculation(displayStringTemplate));
    }

    public AtrBasic displayString(SimpleValueCalculation<String> valueCalculation) {
        setAttributeCalculation(SPackageBasic.ATR_DISPLAY_STRING, valueCalculation);
        return this;
    }

    public String getDisplayString() {
        return getAttributeValue(SPackageBasic.ATR_DISPLAY_STRING);
    }

    public PhraseBreak phraseBreak() {
        return getAttributeValue(SPackageBasic.ATR_PHRASE_BREAK);
    }

    public AtrBasic phraseBreak(PhraseBreak phraseBreak) {
        setAttributeValue(SPackageBasic.ATR_PHRASE_BREAK, phraseBreak);
        return this;
    }
}
