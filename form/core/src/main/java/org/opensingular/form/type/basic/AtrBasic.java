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

package org.opensingular.form.type.basic;

import org.apache.commons.lang3.ObjectUtils;
import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.SInstance;
import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.SType;
import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.enums.PhraseBreak;
import org.opensingular.form.internal.freemarker.FormFreemarkerUtil;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IFunction;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.defaultString;

public class AtrBasic extends STranslatorForAttribute {

    private static final String ALLOWED_FILE_TYPES_SPLIT_REGEX = "[,\\s\\|]";

    public AtrBasic() {
    }

    public AtrBasic(SAttributeEnabled target) {
        super(target);
    }

    public AtrBasic label(String value) {
        setAttributeValue(SPackageBasic.ATR_LABEL, value);
        return this;
    }

    public AtrBasic label(SimpleValueCalculation<String> valueCalculation) {
        setAttributeCalculation(SPackageBasic.ATR_LABEL, valueCalculation);
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

//    public AtrBasic editSize(Integer value) {
//        setAttributeValue(SPackageBasic.ATR_EDIT_SIZE, value);
//        return this;
//    }

    public AtrBasic maxLength(Integer value) {
        setAttributeValue(SPackageBasic.ATR_MAX_LENGTH, value);
        return this;
    }

    public AtrBasic maxFileSize(Long value) {
        setAttributeValue(SPackageBasic.ATR_MAX_FILE_SIZE, value);
        return this;
    }

    public AtrBasic allowedFileTypes(String... value) {
        setAttributeValue(SPackageBasic.ATR_ALLOWED_FILE_TYPES,
                Stream.of(value)
                        .flatMap(it -> Stream.<String>of(it.split(ALLOWED_FILE_TYPES_SPLIT_REGEX)))
                        .collect(joining(",")));
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

    public AtrBasic visible(boolean value) {
        setAttributeValue(SPackageBasic.ATR_VISIBLE, value);
        return this;
    }

    public AtrBasic visible(Predicate<SInstance> value) {
        setAttributeValue(SPackageBasic.ATR_VISIBLE_FUNCTION, value);
        return this;
    }

    public AtrBasic enabled(boolean value) {
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

    public AtrBasic required(boolean value) {
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

    public AtrBasic exists(boolean value) {
        setAttributeValue(SPackageBasic.ATR_EXISTS, value);
        return this;
    }

    public AtrBasic exists(Predicate<SInstance> value) {
        setAttributeValue(SPackageBasic.ATR_EXISTS_FUNCTION, value);
        return this;
    }

    public AtrBasic replaceExists(IFunction<Predicate<SInstance>, Predicate<SInstance>> replacementFunction) {
        Predicate<SInstance> currentExists = getAttributeValue(SPackageBasic.ATR_EXISTS_FUNCTION);
        if(currentExists == null){
            currentExists = (i) -> Boolean.TRUE.equals(getAttributeValue(SPackageBasic.ATR_EXISTS));
        }
        return exists(replacementFunction.apply(currentExists));
    }

    public boolean exists() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_EXISTS));
    }

    /**
     * Listener é invocado quando o campo do qual o tipo depende
     * é atualizado ( a dependencia é expressa via depends on)
     */
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

//    public Integer getEditSize() {
//        return getAttributeValue(SPackageBasic.ATR_EDIT_SIZE);
//    }

    public Integer getMaxLength() {
        return getAttributeValue(SPackageBasic.ATR_MAX_LENGTH);
    }

    public Long getMaxFileSize() {
        return getAttributeValue(SPackageBasic.ATR_MAX_FILE_SIZE);
    }

    public List<String> getAllowedFileTypes() {
        return Optional.ofNullable(getAttributeValue(SPackageBasic.ATR_ALLOWED_FILE_TYPES)).map(in -> Arrays.asList(defaultString(
                getAttributeValue(SPackageBasic.ATR_ALLOWED_FILE_TYPES))
                .split(ALLOWED_FILE_TYPES_SPLIT_REGEX))).orElse(Collections.emptyList());
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

    public AtrBasic upperCaseText() {
        setAttributeValue(SPackageBasic.ATR_UPPER_CASE_TEXT, Boolean.TRUE);
        return this;
    }

    public Boolean isUpperCaseText() {
        return getAttributeValue(SPackageBasic.ATR_UPPER_CASE_TEXT);
    }

}