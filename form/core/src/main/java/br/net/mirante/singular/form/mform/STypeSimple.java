/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import br.net.mirante.singular.form.mform.basic.view.SView;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySelect;
import br.net.mirante.singular.form.mform.builder.selection.SelectionBuilder;
import br.net.mirante.singular.form.mform.core.AtrFormula;
import br.net.mirante.singular.form.mform.core.SPackageCore;

@SuppressWarnings("rawtypes")
@SInfoType(name = "STypeSimple", spackage = SPackageCore.class)
public class STypeSimple<I extends SISimple<VALUE>, VALUE> extends SType<I> {

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

    public VALUE fromString(String value) {
        throw new RuntimeException("Não implementado");
    }

    @Override
    public <T extends Object> T convert(Object value, Class<T> resultClass) {
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
        } else {
            Converter converter = ConvertUtils.lookup(value.getClass(), resultClass);
            if (converter != null) {
                return resultClass.cast(converter.convert(resultClass, value));
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