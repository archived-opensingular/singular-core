/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.basic;

import org.opensingular.form.SInstance;
import org.opensingular.form.internal.freemarker.FormFreemarkerUtil;
import org.opensingular.singular.commons.lambda.IConsumer;
import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.SType;
import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.enums.PhraseBreak;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AtrBasic extends STranslatorForAttribute {

    public AtrBasic() {}

    public AtrBasic(SAttributeEnabled alvo) {
        super(alvo);
    }

    public AtrBasic label(String value) {
        setAttributeValue(SPackageBasic.ATR_LABEL, value);
        return this;
    }
    public AtrBasic noLabel() {
        return label("");
    }

    public AtrBasic itemLabel(String value) {
        setAttributeValue(SPackageBasic.ATR_ITEM_LABEL, value);
        return this;
    }

    public AtrBasic subtitle(String value) {
        setAttributeValue(SPackageBasic.ATR_SUBTITLE, value);
        return this;
    }

    public AtrBasic basicMask(String mask) {
        setAttributeValue(SPackageBasic.ATR_BASIC_MASK, mask);
        return this;
    }

    public AtrBasic editSize(Integer value) {
        setAttributeValue(SPackageBasic.ATR_EDIT_SIZE, value);
        return this;
    }

    public AtrBasic maxLength(Integer value) {
        setAttributeValue(SPackageBasic.ATR_MAX_LENGTH, value);
        return this;
    }

    public AtrBasic maxFileSize(Long value) {
        setAttributeValue(SPackageBasic.ATR_MAX_FILE_SIZE, value);
        return this;
    }

    public AtrBasic integerMaxLength(Integer value) {
        setAttributeValue(SPackageBasic.ATR_INTEGER_MAX_LENGTH, value);
        return this;
    }

    public AtrBasic fractionalMaxLength(Integer value) {
        setAttributeValue(SPackageBasic.ATR_FRACTIONAL_MAX_LENGTH, value);
        return this;
    }

    public AtrBasic visible(Boolean value) {
        setAttributeValue(SPackageBasic.ATR_VISIBLE, value);
        return this;
    }

    public AtrBasic visible(Predicate<SInstance> value) {
        setAttributeValue(SPackageBasic.ATR_VISIBLE_FUNCTION, value);
        return this;
    }

    public AtrBasic enabled(Boolean value) {
        setAttributeValue(SPackageBasic.ATR_ENABLED, value);
        return this;
    }

    public AtrBasic enabled(Predicate<SInstance> value) {
        setAttributeValue(SPackageBasic.ATR_ENABLED_FUNCTION, value);
        return this;
    }

    public AtrBasic dependsOn(Supplier<Collection<SType<?>>> value) {
        Supplier<Collection<SType<?>>> previous = ObjectUtils.defaultIfNull(getAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION), Collections::emptySet);
        setAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION, () -> {
            Set<SType<?>> union = new LinkedHashSet<>(previous.get());
            union.addAll(value.get());
            return union;
        });
        return this;
    }

    public Supplier<Collection<SType<?>>> dependsOn() {
        return ObjectUtils.defaultIfNull(getAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION), Collections::emptySet);
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

    public AtrBasic required(Predicate<SInstance> value) {
        setAttributeValue(SPackageBasic.ATR_REQUIRED_FUNCTION, value);
        return this;
    }

    public Boolean isRequired() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_REQUIRED));
    }

    public AtrBasic exists(Boolean value) {
        setAttributeValue(SPackageBasic.ATR_EXISTS, value);
        return this;
    }

    public AtrBasic exists(Predicate<SInstance> value) {
        setAttributeValue(SPackageBasic.ATR_EXISTS_FUNCTION, value);
        return this;
    }

    public boolean exists() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_EXISTS));
    }

    public AtrBasic updateListener(IConsumer<SInstance> listener) {
        setAttributeValue(SPackageBasic.ATR_UPDATE_LISTENER, listener);
        return this;
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

    public Integer getEditSize() {
        return getAttributeValue(SPackageBasic.ATR_EDIT_SIZE);
    }

    public Integer getMaxLength() {
        return getAttributeValue(SPackageBasic.ATR_MAX_LENGTH);
    }

    public Long getMaxFileSize() {
        return getAttributeValue(SPackageBasic.ATR_MAX_FILE_SIZE);
    }

    @SuppressWarnings("unchecked")
    public IConsumer<SInstance> getUpdateListener() {
        return getAttributeValue(SPackageBasic.ATR_UPDATE_LISTENER);
    }

    public boolean isVisible() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_VISIBLE));
    }

    public boolean isExists() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_EXISTS));
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
