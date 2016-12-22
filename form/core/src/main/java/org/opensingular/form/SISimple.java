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

import java.io.Serializable;
import java.util.Objects;

import org.opensingular.form.calculation.CalculationContext;
import org.opensingular.form.calculation.SimpleValueCalculation;

public class SISimple<TIPO_NATIVO extends Serializable> extends SInstance {

    private TIPO_NATIVO value;

    private SimpleValueCalculation<? extends TIPO_NATIVO> valueCalculation;

    protected SISimple() {}

    @Override
    public TIPO_NATIVO getValue() {
        if (valueCalculation != null && value != null) {
            return valueCalculation.calculate(new CalculationContext(this));
        }
        return value;
    }

    @Override
    <V extends Object> V getValueInTheContextOf(SInstance contextInstance, Class<V> resultClass) {
        if (valueCalculation != null) {
            return convert(valueCalculation.calculate(new CalculationContext(contextInstance)), resultClass);
        }
        return convert(value, resultClass);
    }

    public SimpleValueCalculation<? extends TIPO_NATIVO> getValueCalculation() {
        return valueCalculation;
    }

    public void setValueCalculation(SimpleValueCalculation<? extends TIPO_NATIVO> valueCalculation) {
        this.valueCalculation = valueCalculation;
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
            throw new SingularFormException("Não ser aplica path a um tipo simples");
        }
        return getValueWithDefault(classeDestino);
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
    public final String toStringDisplayDefault() {
        return getType().toStringDisplayDefault(getValue());
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
        TIPO_NATIVO v1 = getValue();
        Object v2 = other.getValue();
        return Objects.equals(v1, v2);
    }

    @Override
    StringBuilder toStringInternal() {
        return super.toStringInternal().append("; value=").append(getValue());
    }
}
