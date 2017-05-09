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

package org.opensingular.form;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.opensingular.form.builder.selection.SelectionBuilder;
import org.opensingular.form.type.core.AtrFormula;
import org.opensingular.form.type.core.SPackageCore;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.view.SViewSelectionByRadio;
import org.opensingular.form.view.SViewSelectionBySelect;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;

@SuppressWarnings("rawtypes")
@SInfoType(name = "STypeSimple", spackage = SPackageCore.class)
public class STypeSimple<I extends SISimple<VALUE>, VALUE extends Serializable> extends SType<I> {

    private final Class<VALUE> valueClass;

    private transient Converter converter;

    public STypeSimple() {
        this.valueClass = null;
    }

    protected STypeSimple(Class<? extends I> instanceClass, Class<VALUE> valueClass) {
        super(instanceClass);
        this.valueClass = valueClass;
    }

    /**
     * Configura o tipo para utilizar a view {@link SViewSelectionBySelect}
     */
    @SuppressWarnings("unchecked")
    public STypeSimple<I, VALUE> withSelectView() {
        return (STypeSimple<I, VALUE>) super.withView(SViewSelectionBySelect::new);
    }

    /**
     * Configura o tipo para utilizar a view {@link SViewSelectionByRadio}
     */
    @SuppressWarnings("unchecked")
    public STypeSimple<I, VALUE> withRadioView() {
        return (STypeSimple<I, VALUE>) super.withView(SViewSelectionByRadio::new);
    }

    public AtrFormula asFormula() {
        return STranslatorForAttribute.of(this, new AtrFormula());
    }

    public VALUE convert(Object value) {
        if (value == null) {
            return null;
        } else if (valueClass.isInstance(value)) {
            return valueClass.cast(value);
        } else if (value instanceof String) {
             return fromString((String) value);
        }
        return convertNotNativeNotString(value);
    }

    protected VALUE convertNotNativeNotString(Object value) {
        return convertUsingApache(value);
    }

    protected String toStringPersistence(VALUE originalValue) {
        if (originalValue == null) {
            return null;
        }
        return originalValue.toString();
    }

    public VALUE fromStringPersistence(String originalValue) {
        return convert(originalValue, valueClass);
    }

    public String toStringDisplayDefault(VALUE value) {
        return toStringPersistence(value);
    }

    @Nullable
    public VALUE fromString(@Nullable String value) {
        throw new UnsupportedOperationException("Não implementado");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T convert(Object value, Class<T> resultClass) {
        if (value == null) {
            return null;
        } else if (resultClass.isAssignableFrom(valueClass)) {
            return resultClass.cast(convert(value));
        } else if (resultClass.isInstance(value)) {
            return resultClass.cast(value);
        } else if (resultClass.isAssignableFrom(String.class)) {
            if (valueClass.isInstance(value)) {
                return resultClass.cast(toStringPersistence(valueClass.cast(value)));
            }
            return resultClass.cast(value.toString());
        } else if (Enum.class.isAssignableFrom(resultClass)) {
            Class<? extends Enum> enumClass = resultClass.asSubclass(Enum.class);
            return (T) Enum.valueOf(enumClass, value.toString());
        } else {
            Converter apacheConverter = ConvertUtils.lookup(value.getClass(), resultClass);
            if (apacheConverter != null) {
                return resultClass.cast(apacheConverter.convert(resultClass, value));
            }
        }

        throw createConversionError(value, resultClass);
    }

    protected final VALUE convertUsingApache(Object value) {
        if (converter == null) {
            converter = ConvertUtils.lookup(valueClass);
            if (converter == null) {
                throw createConversionError(value);
            }
        }
        return valueClass.cast(converter.convert(valueClass, value));
    }

    public final Class<VALUE> getValueClass() {
        return valueClass;
    }

    protected final RuntimeException createConversionError(Object value) {
        return createConversionError(value, null, null, null);
    }

    protected final RuntimeException createConversionError(Object valor, Class<?> resultClass) {
        return createConversionError(valor, resultClass, null, null);
    }

    protected final RuntimeException createConversionError(Object value, Class<?> resultClass, String complement, Exception e) {
        String msg = "O tipo '" + getClass().getName() + "' não consegue converter o valor '" + value + "' do tipo "
                + value.getClass().getName();
        if (resultClass != null) {
            msg += " para o tipo '" + resultClass.getName() + "'";
        }
        if (complement != null) {
            msg += complement;
        }
        if (e != null) {
            return new RuntimeException(msg, e);
        }
        return new RuntimeException(msg);
    }

    @SafeVarargs
    public final STypeSimple<I, VALUE> selectionOf(VALUE... os) {
        new SelectionBuilder<>(this)
                .selfIdAndDisplay()
                .simpleProviderOf((Serializable[]) os);
        return this;
    }

    @SafeVarargs
    public final STypeSimple<I, VALUE> autocompleteOf(VALUE... os) {
        this.setView(SViewAutoComplete::new);
        new SelectionBuilder<>(this)
                .selfIdAndDisplay()
                .simpleProviderOf((Serializable[]) os);
        return this;
    }

    public <T extends Enum<T>> SType selectionOfEnum(Class<T> enumType) {
        this.selectionOf(Enum.class)
                .id(Enum::name)
                .display(Enum::toString)
                .enumConverter(enumType)
                .simpleProvider(ins -> Arrays.asList(enumType.getEnumConstants()));
        return this;
    }

    public <T extends Serializable> SelectionBuilder<T, I, I> selectionOf(Class<T> clazz, SView view) {
        this.setView(() -> view);
        return new SelectionBuilder<>(this);
    }
    
    public SelectionBuilder<VALUE, I, I> selection() {
        return selectionOf(valueClass, new SViewSelectionBySelect());
    }

    public <T extends Serializable> SelectionBuilder<T, I, I> selectionOf(Class<T> clazz) {
        return selectionOf(clazz, new SViewSelectionBySelect());
    }

    public <T extends Serializable> SelectionBuilder<T, I, I> autocompleteOf(Class<T> clazz) {
        return selectionOf(clazz, new SViewAutoComplete());
    }

    public <T extends Serializable> SelectionBuilder<T, I, I> lazyAutocompleteOf(Class<T> clazz) {
        return selectionOf(clazz, new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
    }

}