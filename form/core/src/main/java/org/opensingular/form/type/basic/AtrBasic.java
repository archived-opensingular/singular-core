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

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.opensingular.form.AtrRef;
import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.SInstance;
import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.SType;
import org.opensingular.form.STypes;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.enums.PhraseBreak;
import org.opensingular.form.internal.freemarker.FormFreemarkerUtil;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.defaultString;


public class AtrBasic extends STranslatorForAttribute {

    private static final String DEPENDSON_NULL_PARAM_MSG       = "dependsOn do not allow null dependent types! Check if your variables are already initialized.";
    private static final String ALLOWED_FILE_TYPES_SPLIT_REGEX = "[,\\s\\|]";

    public AtrBasic() {
    }

    public AtrBasic(SAttributeEnabled target) {
        super(target);
    }

    /**
     * Defines the label attribute of the current type.
     *
     * @param value The String of the label.
     * @return this AtrBasic with ATR_LABEL.
     */
    public AtrBasic label(String value) {
        setAttributeValue(SPackageBasic.ATR_LABEL, value);
        return this;
    }

    /**
     * Defines the label attributes of the current type with HTML support.
     *
     * @param value The String of the label.
     * @return this AtrBasic with ATR_LABEL.
     */
    public AtrBasic labelWithHTML(String value) {
        label(value);
        setAttributeValue(SPackageBasic.ATR_ENABLE_HTML_IN_LABEL, Boolean.TRUE);
        return this;
    }

    /**
     * Defines the label attribute of the current type.
     *
     * @param valueCalculation The SimpleValueCalculation of the label.
     * @return this AtrBasic with ATR_LABEL.
     */
    public AtrBasic label(SimpleValueCalculation<String> valueCalculation) {
        setAttributeCalculation(SPackageBasic.ATR_LABEL, valueCalculation);
        return this;
    }

    /**
     * Defines the label attributes of the current type with HTML support.
     *
     * @param valueCalculation The SimpleValueCalculation of the label.
     * @return this AtrBasic with ATR_LABEL.
     */
    public AtrBasic labelWithHTML(SimpleValueCalculation<String> valueCalculation) {
        label(valueCalculation);
        setAttributeValue(SPackageBasic.ATR_ENABLE_HTML_IN_LABEL, Boolean.TRUE);
        return this;
    }

    /**
     * Defines an empty label for the current type.
     * @return this AtrBasic with empty ATR_LABEL.
     */
    public AtrBasic noLabel() {
        return label("");
    }

    /**
     * Returns the current type's label.
     *
     * @return The current type's ATR_LABEL value.
     */
    public String getLabel() {
        return getAttributeValue(SPackageBasic.ATR_LABEL);
    }

    /**
     * Returns if the current type's label has HTML support.
     *
     * @return The current type's ATR_ENABLE_HTML_IN_LABEL value.
     */
    public Boolean isEnabledHTMLInLabel() {
        return getAttributeValue(SPackageBasic.ATR_ENABLE_HTML_IN_LABEL);
    }

    /**
     * For usage on {{@link #(org.opensingular.form.STypeList)}} only.
     * Defines the item's label attribute of the current type.
     * It is shown on the "add item" button with "Adicionar item" by default.
     *
     * @param value The String of the item's label.
     * @return this AtrBasic with ATR_ITEM_LABEL.
     */
    public AtrBasic itemLabel(String value) {
        setAttributeValue(SPackageBasic.ATR_ITEM_LABEL, value);
        return this;
    }

    /**
     * Returns the current type's item label.
     *
     * @return The current type's ATR_ITEM_LABEL value.
     */
    public String getItemLabel() {
        return getAttributeValue(SPackageBasic.ATR_ITEM_LABEL);
    }

    /**
     * Defines the subtitle attribute of the current type.
     *
     * @param value The String of the subtitle.
     * @return this AtrBasic with ATR_SUBTITLE.
     */
    public AtrBasic subtitle(String value) {
        setAttributeValue(SPackageBasic.ATR_SUBTITLE, value);
        return this;
    }

    /**
     * Returns the current type's subtitle.
     *
     * @return The current type's ATR_SUBTITLE value.
     */
    public String getSubtitle() {
        return getAttributeValue(SPackageBasic.ATR_SUBTITLE);
    }

    /**
     * For usage on {{@link #(org.opensingular.form.type.core.STypeString)}} only.
     * Allows adding a mask to an {{@link #(AtrBasic)}} using @see <a href="https://github.com/RobinHerbots/Inputmask">Inputmask</a>.
     *
     * @param mask The mask pattern used to validate the mask.
     * @return this AtrBasic with ATR_BASIC_MASK.
     */
    public AtrBasic basicMask(String mask) {
        setAttributeValue(SPackageBasic.ATR_BASIC_MASK, mask);
        return this;
    }

    /**
     * For usage on {{@link #(org.opensingular.form.type.core.STypeString)}} only.
     * Allows adding a mask to an {{@link #(AtrBasic)}} using a simple regular expression.
     * It only works for validating the whole field's pattern. It's not possible to set a regex defining specific amount of characters.
     * Compliant {@param pattern}: "[a-zA-Z ]".
     * Non compliant {@param pattern}: "[a-zA-Z ]{3}".
     *
     * @param pattern The regular expression pattern used to validate the mask.
     * @return this AtrBasic with ATR_REGEX_MASK.
     */
    public AtrBasic regexMask(String pattern) {
        setAttributeValue(SPackageBasic.ATR_REGEX_MASK, pattern);
        return this;
    }

    /**
     * Defines the maximum length of an {{@link #(org.opensingular.form.type.core.STypeString)}}.
     *
     * @param value The maximum size in characters of the String value.
     * @return this AtrBasic with ATR_MAX_LENGTH.
     */
    public AtrBasic maxLength(Integer value) {
        setAttributeValue(SPackageBasic.ATR_MAX_LENGTH, value);
        return this;
    }

    /**
     * Returns the current type's maximum length in characters.
     *
     * @return The current type's ATR_MAX_LENGTH value.
     */
    public Integer getMaxLength() {
        return getAttributeValue(SPackageBasic.ATR_MAX_LENGTH);
    }

    /**
     * For usage on {{@link #(org.opensingular.form.type.core.attachment.STypeAttachment)}} only.
     * Defines the maximum file size of the current attachment type.
     *
     * @param value Maximum file size in Bytes.
     * @return this AtrBasic with ATR_MAX_FILE_SIZE.
     */
    public AtrBasic maxFileSize(Long value) {
        setAttributeValue(SPackageBasic.ATR_MAX_FILE_SIZE, value);
        return this;
    }

    /**
     * Returns the current type's maximum file size in bytes.
     *
     * @return The current type's ATR_MAX_FILE_SIZE value.
     */
    public Long getMaxFileSize() {
        return getAttributeValue(SPackageBasic.ATR_MAX_FILE_SIZE);
    }

    /**
     * For usage on {{@link #(org.opensingular.form.type.core.attachment.STypeAttachment)}} only.
     * Defines the allowed file types of the current attachment type.
     *
     * @param value The allowed file types.
     * @return this AtrBasic with ATR_ALLOWED_FILE_TYPES.
     */
    public AtrBasic allowedFileTypes(String... value) {
        setAttributeValue(SPackageBasic.ATR_ALLOWED_FILE_TYPES,
                Stream.of(value)
                        .map(String::toLowerCase)
                        .flatMap(it -> Stream.<String>of(it.split(ALLOWED_FILE_TYPES_SPLIT_REGEX)))
                        .collect(joining(",")));
        return this;
    }

    /**
     * Returns the current type's allowed file types.
     *
     * @return The current type's ATR_ALLOWED_FILE_TYPES value.
     */
    public List<String> getAllowedFileTypes() {
        return Optional.ofNullable(getAttributeValue(SPackageBasic.ATR_ALLOWED_FILE_TYPES)).map(in -> Arrays.asList(defaultString(
                getAttributeValue(SPackageBasic.ATR_ALLOWED_FILE_TYPES))
                .split(ALLOWED_FILE_TYPES_SPLIT_REGEX)))
                .orElse(Collections.emptyList());
    }

    /**
     * For usage on {{@link #(org.opensingular.form.type.core.STypeDecimal)}} and {{@link #(org.opensingular.form.type.core.STypeMonetary)}} only.
     * Defines the maximum number of integer digits.
     *
     * @param value Maximum number of integer digits.
     * @return this AtrBasic with ATR_INTEGER_MAX_LENGTH.
     */
    public AtrBasic integerMaxLength(Integer value) {
        setAttributeValue(SPackageBasic.ATR_INTEGER_MAX_LENGTH, value);
        return this;
    }

    /**
     * For usage on {{@link #(org.opensingular.form.type.core.STypeDecimal)}} and {{@link #(org.opensingular.form.type.core.STypeMonetary)}} only.
     * Defines the maximum number of fractional digits. Default 2.
     *
     * @param value Maximum number of fractional digits.
     * @return this AtrBasic with ATR_FRACTIONAL_MAX_LENGTH.
     */
    public AtrBasic fractionalMaxLength(Integer value) {
        setAttributeValue(SPackageBasic.ATR_FRACTIONAL_MAX_LENGTH, value);
        return this;
    }

    /**
     * Defines the visibility of the current type.
     * Simply hides or shows the SType depending on {@param value}.
     *
     * @param value The boolean value that defines visibility.
     * @return this AtrBasic with ATR_VISIBLE.
     */
    public AtrBasic visible(boolean value) {
        setAttributeValue(SPackageBasic.ATR_VISIBLE, value);
        return this;
    }

    /**
     * Defines the visibility of the current type.
     * Simply hides or shows the SType depending on {@param value}.
     *
     * @param value The Predicate that defines visibility.
     * @return this AtrBasic with ATR_VISIBLE_FUNCTION.
     */
    public AtrBasic visible(Predicate<SInstance> value) {
        setAttributeValue(SPackageBasic.ATR_VISIBLE_FUNCTION, value);
        return this;
    }

    /**
     * Tells if the current type is visible on page or not.
     *
     * @return The current type ATR_VISIBLE value.
     */
    public boolean isVisible() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_VISIBLE));
    }

    /**
     * Defines if the current type's value is enabled for editing or not.
     *
     * @param value The boolean value that defines enabled or not.
     * @return this AtrBasic with ATR_ENABLED.
     */
    public AtrBasic enabled(boolean value) {
        setAttributeValue(SPackageBasic.ATR_ENABLED, value);
        return this;
    }

    /**
     * Defines if the current type's value is enabled for editing or not.
     *
     * @param value The Predicate that defines enabled or not.
     * @return this AtrBasic with ATR_ENABLED.
     */
    public AtrBasic enabled(Predicate<SInstance> value) {
        setAttributeValue(SPackageBasic.ATR_ENABLED_FUNCTION, value);
        return this;
    }

    /**
     * Tells if the current type's value is enabled for editing or not.
     *
     * @return The current type ATR_ENABLED value.
     */
    public boolean isEnabled() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_ENABLED));
    }

    public AtrBasic dependsOn(Supplier<Collection<DelayedDependsOnResolver>> value) {
        assertNoNull(DEPENDSON_NULL_PARAM_MSG, value);
        Supplier<Collection<DelayedDependsOnResolver>> previous = ObjectUtils.defaultIfNull(getAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION), Collections::emptySet);
        setAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION, () -> {
            Set<DelayedDependsOnResolver> union = new LinkedHashSet<>(previous.get());
            union.addAll(value.get());
            return union;
        });
        return this;
    }

    /**
     * Configures the current type to depend on all STypes passed.
     *
     * @param types All types that the current SType depends on.
     * @return
     */
    public AtrBasic dependsOn(SType<?>... types) {
        assertNoNull(DEPENDSON_NULL_PARAM_MSG, types);
        return dependsOn(() -> Arrays.asList(types).stream().map((SType<?> t) -> (DelayedDependsOnResolver) (root, current) -> Lists.newArrayList(t)).collect(Collectors.toList()));
    }

    /**
     * Configures the current type to depend on all STypes created from the given {@param typeClass} or its subclasses.
     * This dependency should be used with caution since it can let do unwanted dependencies and apparently unpredictable behavior.
     * Prefer using the {{@link #dependsOn(SType[])}} alternative.
     *
     * @param typeClass
     * @return
     */
    public AtrBasic dependsOn(Class<? extends SType<?>> typeClass) {
        assertNoNull(DEPENDSON_NULL_PARAM_MSG, typeClass);
        return dependsOn(typeClass, stype -> stype);
    }

    public <T extends SType<?>> AtrBasic dependsOn(Class<T> typeClass, IFunction<T, ? extends SType> typefinder) {
        assertNoNull(DEPENDSON_NULL_PARAM_MSG, typefinder);
        Supplier<Collection<DelayedDependsOnResolver>> previous = ObjectUtils.defaultIfNull(getAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION), Collections::emptySet);
        setAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION, () -> {
            Set<DelayedDependsOnResolver> union = new LinkedHashSet<>(previous.get());
            union.add((root, current) -> {
                final List<SType<?>> dependentTypes = new ArrayList<SType<?>>();
                STypes.visitAll(root, stype -> {
                    if (typeClass.isAssignableFrom(stype.getClass())) {
                        dependentTypes.add(typefinder.apply((T) stype));
                    }
                });
                return dependentTypes;
            });
            return union;
        });
        return this;
    }

    public Supplier<Collection<DelayedDependsOnResolver>> dependsOn() {
        return ObjectUtils.defaultIfNull(getAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION), Collections::emptySet);
    }


    private void assertNoNull(String msg, Object... o) {
        boolean paramNull = o == null;
        if (!paramNull) {
            for (Object item : o) {
                paramNull |= item == null;
                if (paramNull) {
                    break;
                }
            }
        }
        if (paramNull) {
            throw new SingularFormException(msg);
        }
    }

    /**
     * Defines that the current type's value is required.
     * The type cannot be submitted with null value.
     *
     * @return this AtrBasic with ATR_REQUIRED.
     */
    public AtrBasic required() {
        return required(true);
    }

    /**
     * Defines that the current type's value is required or not.
     * The type can be submitted with null value depending on this attribute.
     *
     * @param value The boolean value that defines required or not.
     * @return this AtrBasic with ATR_REQUIRED.
     */
    public AtrBasic required(boolean value) {
        setAttributeValue(SPackageBasic.ATR_REQUIRED, value);
        return this;
    }

    /**
     * Defines that the current type's value is required or not.
     * The type can be submitted with null value depending on this attribute.
     *
     * @param value The Predicate that defines required or not.
     * @return this AtrBasic with ATR_REQUIRED.
     */
    public AtrBasic required(Predicate<SInstance> value) {
        setAttributeValue(SPackageBasic.ATR_REQUIRED_FUNCTION, value);
        return this;
    }

    /**
     * Tells if the current type is required or not.
     *
     * @return The current type ATR_REQUIRED value.
     */
    public Boolean isRequired() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_REQUIRED));
    }

    /**
     * Defines the existence on page of the current type.
     *
     * @param value The boolean value that defines existence.
     * @return this AtrBasic with ATR_EXISTS.
     */
    public AtrBasic exists(boolean value) {
        setAttributeValue(SPackageBasic.ATR_EXISTS, value);
        return this;
    }

    /**
     * Defines the existence on page of the current type.
     *
     * @param value The Predicate that defines existence.
     * @return this AtrBasic with ATR_EXISTS_FUNCTION.
     */
    public AtrBasic exists(Predicate<SInstance> value) {
        setAttributeValue(SPackageBasic.ATR_EXISTS_FUNCTION, value);
        return this;
    }

    public AtrBasic replaceExists(IFunction<Predicate<SInstance>, Predicate<SInstance>> replacementFunction) {
        Predicate<SInstance> currentExists = getAttributeValue(SPackageBasic.ATR_EXISTS_FUNCTION);
        if (currentExists == null) {
            currentExists = (i) -> Boolean.TRUE.equals(getAttributeValue(SPackageBasic.ATR_EXISTS));
        }
        return exists(replacementFunction.apply(currentExists));
    }

    /**
     * Tells if the current type exists on page or not.
     *
     * @return The current type ATR_EXISTS value.
     */
    public boolean exists() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_EXISTS));
    }

    /**
     * Tells if the current type exists on page or not.
     *
     * @return The current type ATR_EXISTS value.
     */
    public boolean isExists() {
        return !Boolean.FALSE.equals(getAttributeValue(SPackageBasic.ATR_EXISTS));
    }

    /**
     * Listener is invoked when the field of which the type depends
     * is updated (dependency is expressed via depends on)
     */
    public AtrBasic updateListener(IConsumer<SInstance> listener) {
        setAttributeValue(SPackageBasic.ATR_UPDATE_LISTENER, listener);
        return this;
    }

    /**
     * Returns the current type's update listener IConsumer.
     *
     * @return The current type's ATR_UPDATE_LISTENER value.
     */
    @SuppressWarnings("unchecked")
    public IConsumer<SInstance> getUpdateListener() {
        return getAttributeValue(SPackageBasic.ATR_UPDATE_LISTENER);
    }

    public <V> AtrBasic withAttribute(@Nonnull AtrRef<?, ?, V> atr, @Nullable V value) {
        setAttributeValue(atr, value);
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

    //    public Integer getEditSize() {
    //        return getAttributeValue(SPackageBasic.ATR_EDIT_SIZE);
    //    }

    /**
     * Defines the display String attribute of the current type.
     *
     * @param displayStringTemplate The String of the display String attribute.
     * @return this AtrBasic with ATR_DISPLAY_STRING.
     */
    public AtrBasic displayString(String displayStringTemplate) {
        return displayString(FormFreemarkerUtil.get().createInstanceCalculation(displayStringTemplate, false, true));
    }

    /**
     * Defines the display String attribute of the current type.
     *
     * @param valueCalculation The SimpleValueCalculation of the display String attribute.
     * @return this AtrBasic with ATR_DISPLAY_STRING.
     */
    public AtrBasic displayString(SimpleValueCalculation<String> valueCalculation) {
        setAttributeCalculation(SPackageBasic.ATR_DISPLAY_STRING, valueCalculation);
        return this;
    }

    /**
     * Returns the current type's display String.
     *
     * @return The current type's ATR_DISPLAY_STRING value.
     */
    public String getDisplayString() {
        return getAttributeValue(SPackageBasic.ATR_DISPLAY_STRING);
    }

    /**
     * Defines the help attribute of the current type.
     * Is's shown as a hint text about the current type in a question mark icon.
     *
     * @param val The String of the help.
     * @return this AtrBasic with ATR_HELP.
     */
    public AtrBasic help(String val) {
        setAttributeValue(SPackageBasic.ATR_HELP, val);
        return this;
    }

    /**
     * Defines the help attribute of the current type.
     *
     * @param valueCalculation The SimpleValueCalculation of the help.
     * @return this AtrBasic with ATR_HELP.
     */
    public AtrBasic help(SimpleValueCalculation<String> valueCalculation) {
        setAttributeCalculation(SPackageBasic.ATR_HELP, valueCalculation);
        return this;
    }


    /**
     * Returns the current type's help attribute.
     *
     * @return The current type's ATR_HELP value.
     */
    public String getHelp() {
        return getAttributeValue(SPackageBasic.ATR_HELP);
    }

    public AtrBasic instruction(String val) {
        setAttributeValue(SPackageBasic.ATR_INSTRUCTION, val);
        return this;
    }

    public String getInstruction() {
        return getAttributeValue(SPackageBasic.ATR_INSTRUCTION);
    }

    /**
     * For usage on {{@link #(org.opensingular.form.STypeList)}}
     * with SMultiSelectionBySelectView, SMultiSelectionByCheckboxView or SMultiSelectionByPicklistView only.
     * Defines if the current type's way of phrase braking.
     *
     * @param phraseBreak The PhraseBreak that defines the current type's way of phrase breaking.
     * @return this AtrBasic with ATR_PHRASE_BREAK.
     */
    public AtrBasic phraseBreak(PhraseBreak phraseBreak) {
        setAttributeValue(SPackageBasic.ATR_PHRASE_BREAK, phraseBreak);
        return this;
    }

    /**
     * Returns the current type's phrase break attribute.
     *
     * @return The current type's ATR_PHRASE_BREAK value.
     */
    public PhraseBreak phraseBreak() {
        return getAttributeValue(SPackageBasic.ATR_PHRASE_BREAK);
    }

    /**
     * For usage on {{@link #(org.opensingular.form.type.core.STypeString)}} only.
     * Makes the current type's value upper case.
     *
     * @return this AtrBasic with ATR_UPPER_CASE_TEXT.
     */
    public AtrBasic upperCaseText() {
        setAttributeValue(SPackageBasic.ATR_UPPER_CASE_TEXT, Boolean.TRUE);
        return this;
    }

    /**
     * Tells if the current type is upper case or not.
     *
     * @return The current type ATR_UPPER_CASE_TEXT value.
     */
    public Boolean isUpperCaseText() {
        return getAttributeValue(SPackageBasic.ATR_UPPER_CASE_TEXT);
    }

    public interface DelayedDependsOnResolver {

        public List<SType<?>> resolve(SType<?> documentRoot, SType<?> current);
    }
}