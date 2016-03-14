/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform;

import java.util.Objects;

public class SISimple<TIPO_NATIVO> extends SInstance {

    private TIPO_NATIVO value;

    protected SISimple() {}

    @Override
    public TIPO_NATIVO getValue() {
        return value;
    }

    @Override
    public void clearInstance() {
        setValue(null);
    }

    @Override
    public TIPO_NATIVO getValueWithDefault() {
        TIPO_NATIVO v = getValue();
        if (v == null) {
            return getType().convert(getType().getAttributeValueOrDefaultValueIfNull());
        }
        return v;
    }

    @Override
    final <T extends Object> T getValueWithDefaultIfNull(PathReader leitor, Class<T> classeDestino) {
        if (!leitor.isEmpty()) {
            throw new RuntimeException("Não ser aplica path a um tipo simples");
        }
        return getValueWithDefault(classeDestino);
    }

    @Override
    protected void resetValue() {
        setValue(null);
    }

    /** Indica que o valor da instância atual é null. */
    public boolean isNull() {
        return getValue() == null;
    }

    @Override
    public boolean isEmptyOfData() {
        return getValue() == null;
    }


    @Override
    public final void setValue(Object valor) {
        TIPO_NATIVO oldValue = this.getValue();
        TIPO_NATIVO newValue = getType().convert(valor);
        this.value = onSetValor(oldValue, newValue);
        if (getDocument() != null && !Objects.equals(oldValue, newValue)) {
            if (isAttribute()) {
                getDocument().getInstanceListeners().fireInstanceAttributeChanged(getAttributeOwner(), this, oldValue, newValue);
            } else {
                getDocument().getInstanceListeners().fireInstanceValueChanged(this, oldValue, newValue);
            }
        }
    }

    protected TIPO_NATIVO onSetValor(TIPO_NATIVO oldValue, TIPO_NATIVO newValue) {
        return newValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public STypeSimple<?, TIPO_NATIVO> getType() {
        return (STypeSimple<?, TIPO_NATIVO>) super.getType();
    }

    @Override
    public String toStringDisplay() {
        if (getType().getOptionsProvider() != null) {
            String key = getOptionsConfig().getKeyFromOption(this);
            return getOptionsConfig().getLabelFromKey(key);
        } else {
            return getType().toStringDisplay(getValue());
        }
    }

    public String toStringPersistence() {
        if (getValue() == null) {
            return null;
        }
        return getType().toStringPersistence(getValue());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SISimple<?> other = (SISimple<?>) obj;
        if (!getType().equals(other.getType())
                && !getType().getName().equals(other.getType().getName())) {
            return false;
        }
        if (getValue() == null) {
            if (other.getValue() != null)
                return false;
        } else if (!getValue().equals(other.getValue()))
            return false;
        return true;
    }

}
