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

import org.opensingular.form.calculation.SimpleValueCalculation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public abstract class STranslatorForAttribute implements SAttributeEnabled {

    private SAttributeEnabled target;

    static <T extends STranslatorForAttribute> T of(SAttributeEnabled original, Class<T> aspectClass) {
        if (! STranslatorForAttribute.class.isAssignableFrom(aspectClass)) {
            throw new SingularFormException("Classe '" + aspectClass + "' não funciona como aspecto. Deve extender " +
                    STranslatorForAttribute.class.getName());
        }
        T instance = SFormUtil.newInstance(aspectClass);
        return of(original, instance);
    }

    static <T extends STranslatorForAttribute> T of(SAttributeEnabled original, T instance) {
        instance.setTarget(original);
        return instance;
    }

    protected STranslatorForAttribute() {}

    protected STranslatorForAttribute(SAttributeEnabled target) {
        this.target = target;
    }

    final void setTarget(SAttributeEnabled target) {
        this.target = target;
    }

    public SAttributeEnabled getTarget() {
        if (target == null) {
            throw new SingularFormException("O objeto alvo dos atributos não foi definido");
        }
        return target;
    }

    public SType<?> getType() {
        SAttributeEnabled t = getTarget();
        if (t instanceof SType) {
            return (SType<?>) t;
        }
        return ((SInstance) t).getType();
    }

    /**
     * Lista todos os atributos com valor associado diretamente ao objeto atual.
     * @return Nunca null
     */
    @Nonnull
    public Collection<SInstance> getAttributes() {
        return getTarget().getAttributes();
    }

    /** Retorna a instancia do atributo se houver uma associada diretamente ao objeto atual. */
    @Nonnull
    public Optional<SInstance> getAttributeDirectly(@Nonnull String fullName) {
        return getTarget().getAttributeDirectly(fullName);
    }

    //-----------------------------------------------------------
    // Implementando métodos de SAttributeEnabled
    //-----------------------------------------------------------

    @Override
    public <TR> TR as(Function<SAttributeEnabled, TR> wrapper) {
        return wrapper.apply(getTarget());
    }

    @Override
    public <V> void setAttributeCalculation(@Nonnull AtrRef<?, ?, V> atr, @Nullable SimpleValueCalculation<V> value) {
        getTarget().setAttributeCalculation(atr, value);
    }

    @Override
    public <V> void setAttributeCalculation(@Nonnull String attributeFullName, @Nullable String subPath,
            @Nullable SimpleValueCalculation<V> value) {
        getTarget().setAttributeCalculation(attributeFullName, subPath, value);
    }

    @Override
    public <V> void setAttributeValue(@Nonnull AtrRef<?, ?, V> atr, @Nullable V value) {
        getTarget().setAttributeValue(atr, value);
    }

    @Override
    public void setAttributeValue(SType<?> defAttribute, Object value) {
        getTarget().setAttributeValue(defAttribute, value);
    }

    @Override
    public void setAttributeValue(String attributeName, Object value) {
        getTarget().setAttributeValue(attributeName, value);
    }

    @Override
    public void setAttributeValue(@Nonnull String attributeFullName, @Nullable String subPath, @Nullable Object value) {
        getTarget().setAttributeValue(attributeFullName, subPath, value);
    }

    @Override
    @Nullable
    public <V> V getAttributeValue(@Nonnull String attributeFullName, @Nullable Class<V> resultClass) {
        return getTarget().getAttributeValue(attributeFullName, resultClass);
    }

    @Override
    @Nullable
    public <T> T getAttributeValue(@Nonnull AtrRef<?, ?, ?> atr, @Nullable Class<T> resultClass) {
        return getTarget().getAttributeValue(atr, resultClass);
    }

    @Override
    @Nullable
    public <V> V getAttributeValue(@Nonnull AtrRef<?, ?, V> atr) {
        return getTarget().getAttributeValue(atr);
    }

    @Override
    public boolean hasAttributeValueDirectly(@Nonnull AtrRef<?, ?, ?> atr) {
        return getTarget().hasAttributeValueDirectly(atr);
    }

    @Override
    public boolean hasAttributeDefinedDirectly(@Nonnull AtrRef<?, ?, ?> atr) {
        return getTarget().hasAttributeDefinedDirectly(atr);
    }

    @Override
    public Object getAttributeValue(String attributeFullName) {
        return getTarget().getAttributeValue(attributeFullName);
    }

    @Override
    public SDictionary getDictionary() {
        return getTarget().getDictionary();
    }

    @Nullable
    public SAttributeEnabled getParentAttributeContext() {
        return getTarget().getParentAttributeContext();
    }
}
