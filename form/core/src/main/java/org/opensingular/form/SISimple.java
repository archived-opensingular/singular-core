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

import javax.annotation.Nullable;
import org.opensingular.form.calculation.CalculationContext;
import org.opensingular.form.calculation.CalculationContextInstanceOptional;
import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.calculation.SimpleValueCalculationInstanceOptional;
import org.opensingular.form.internal.PathReader;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

public class SISimple<NATIVE_TYPE extends Serializable> extends SInstance {

    private NATIVE_TYPE value;

    private SimpleValueCalculationInstanceOptional<? extends NATIVE_TYPE> valueCalculation;

    protected SISimple() {
    }

    @Override
    public NATIVE_TYPE getValue() {
        if (value == null && valueCalculation != null) {
            CalculationContextInstanceOptional ctx;
            if (isAttribute()) {
                if (getAttributeInstanceInfo().getInstanceOwner() != null) {
                    ctx = new CalculationContext(getAttributeInstanceInfo().getInstanceOwner(), this);
                } else {
                    ctx = new CalculationContextInstanceOptional(getAttributeInstanceInfo().getTypeOwner(), null, this);
                }
            } else {
                ctx = new CalculationContext(this, this);
            }
            return valueCalculation.calculate(ctx);
        }
        return value;
    }

    @Override
    <V> V getValueInTheContextOf(@Nonnull CalculationContextInstanceOptional context, @Nullable Class<V> resultClass) {
        NATIVE_TYPE v = value;
        if (v == null && valueCalculation != null) {
            v = valueCalculation.calculate(context.asCalculatingFor(this));
        }
        return convert(v, resultClass);
    }

    public SimpleValueCalculationInstanceOptional<? extends NATIVE_TYPE> getValueCalculation() {
        return valueCalculation;
    }

    public void setValueCalculation(SimpleValueCalculation<? extends NATIVE_TYPE> valueCalculation) {
        this.valueCalculation = SimpleValueCalculationInstanceOptional.of(valueCalculation);
    }

    public void setValueCalculationInstanceOptional(
            SimpleValueCalculationInstanceOptional<? extends NATIVE_TYPE> valueCalculation) {
        this.valueCalculation = valueCalculation;
    }

    @Override
    public void clearInstance() {
        setValue(null);
    }

    @Override
    public NATIVE_TYPE getValueWithDefault() {
        NATIVE_TYPE v = getValue();
        if (v == null) {
            return getType().convert(getType().getAttributeValueOrDefaultValueIfNull());
        }
        return v;
    }

    @Override
    final <T> T getValueWithDefaultIfNull(PathReader reader, Class<T> destinyClass) {
        if (!reader.isEmpty()) {
            throw new SingularFormException("Não ser aplica path a um tipo simples", this);
        }
        return getValueWithDefault(destinyClass);
    }

    @Override
    public boolean isEmptyOfData() {
        return getValue() == null;
    }


    @SuppressWarnings("unchecked")
    @Override
    public final void setValue(Object value) {
        NATIVE_TYPE       oldValue = this.getValue();
        final NATIVE_TYPE newValue;
        if (value instanceof SISimple<?>){
            newValue = ((SISimple<NATIVE_TYPE>)value).getValue();
        } else {
            newValue = getType().convert(value);
        }
        this.value = onSetValue(oldValue, newValue);
        if (!Objects.equals(oldValue, newValue)) {
            if (isAttribute()) {
                getDocument().getInstanceListeners().fireInstanceAttributeChanged(getAttributeOwner(), this, oldValue, newValue);
            } else {
                getDocument().getInstanceListeners().fireInstanceValueChanged(this, oldValue, newValue);
            }
        }
    }

    protected NATIVE_TYPE onSetValue(NATIVE_TYPE oldValue, NATIVE_TYPE newValue) {
        return newValue;
    }

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public STypeSimple<?, NATIVE_TYPE> getType() {
        return (STypeSimple<?, NATIVE_TYPE>) super.getType();
    }

    @Override
    public final String toStringDisplayDefault() {
        return getType().toStringDisplayDefault(getValue());
    }

    @Nullable
    public String toStringPersistence() {
        NATIVE_TYPE v = getValue();
        if (v == null) {
            return null;
        }
        return getType().toStringPersistence(v);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getType().hashCode();
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
        STypeSimple<?, NATIVE_TYPE> type = getType();
        STypeSimple<?, ?> otherType = other.getType();
        if (!type.equals(otherType)
                && !type.getName().equals(otherType.getName())) {
            return false;
        }
        NATIVE_TYPE v1 = getValue();
        Object v2 = other.getValue();
        return Objects.equals(v1, v2);
    }

    @Override
    StringBuilder toStringInternal() {
        return super.toStringInternal().append("; value=").append(getValue());
    }
}
