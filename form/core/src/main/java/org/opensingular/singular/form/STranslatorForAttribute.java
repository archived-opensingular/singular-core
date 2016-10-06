/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form;

import org.opensingular.singular.form.calculation.SimpleValueCalculation;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public abstract class STranslatorForAttribute implements SAttributeEnabled {

    private SAttributeEnabled target;

    static <T extends STranslatorForAttribute> T of(SAttributeEnabled original, Class<T> aspectClass) {
        T instance;
        try {
            instance = aspectClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Erro criando classe de aspecto '" + aspectClass.getName() + "'", e);
        }
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
            throw new RuntimeException("O objeto alvo dos atributos não foi definido");
        }
        return target;
    }

    public SType<?> getTipo() {
        if (target == null) {
            throw new RuntimeException("O objeto alvo dos atributos não foi definido");
        }
        if (target instanceof SType) {
            return (SType<?>) target;
        }
        return ((SInstance) target).getType();
    }

    /**
     * Lista todos os atributos com valor associado diretamente ao objeto atual.
     * @return Nunca null
     */
    public Collection<SInstance> getAttributes() {
        return getTarget().getAttributes();
    }

    /** Retorna a instancia do atributo se houver uma associada diretamente ao objeto atual. */
    public Optional<SInstance> getAttribute(String fullName) {
        return getTarget().getAttribute(fullName);
    }

    //-----------------------------------------------------------
    // Implementando métodos de SAttributeEnabled
    //-----------------------------------------------------------

    @Override
    public <TR> TR as(Function<SAttributeEnabled, TR> wrapper) {
        return wrapper.apply(getTarget());
    }

    @Override
    public <V> void setAttributeCalculation(AtrRef<?, ?, V> atr, SimpleValueCalculation<V> value) {
        getTarget().setAttributeCalculation(atr, value);
    }

    @Override
    public <V> void setAttributeCalculation(String attributeFullName, String subPath, SimpleValueCalculation<V> value) {
        getTarget().setAttributeCalculation(attributeFullName, subPath, value);
    }

    @Override
    public <V> void setAttributeValue(AtrRef<?, ?, V> atr, V value) {
        getTarget().setAttributeValue(atr, value);
    }

    @Override
    public <V> void setAttributeValue(SType<?> defAttribute, Object value) {
        getTarget().setAttributeValue(defAttribute, value);
    }

    @Override
    public void setAttributeValue(String attributeName, Object value) {
        getTarget().setAttributeValue(attributeName, value);
    }

    @Override
    public void setAttributeValue(String attributeFullName, String subPath, Object value) {
        getTarget().setAttributeValue(attributeFullName, subPath, value);
    }

    @Override
    public <V> V getAttributeValue(String attributeFullName, Class<V> resultClass) {
        return getTarget().getAttributeValue(attributeFullName, resultClass);
    }

    @Override
    public <T> T getAttributeValue(AtrRef<?, ?, ?> atr, Class<T> resultClass) {
        return getTarget().getAttributeValue(atr, resultClass);
    }

    @Override
    public <V> V getAttributeValue(AtrRef<?, ?, V> atr) {
        return getTarget().getAttributeValue(atr);
    }

    @Override
    public Object getAttributeValue(String attributeFullName) {
        return getTarget().getAttributeValue(attributeFullName);
    }

    @Override
    public SDictionary getDictionary() {
        return getTarget().getDictionary();
    }

}
