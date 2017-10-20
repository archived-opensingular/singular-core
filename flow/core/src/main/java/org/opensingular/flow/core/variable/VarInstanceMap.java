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

package org.opensingular.flow.core.variable;

import org.opensingular.flow.core.SingularFlowException;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface VarInstanceMap<K extends VarInstance, SELF extends VarInstanceMap<K,SELF>> extends VarServiceEnabled, Serializable, Iterable<K> {


    K getVariable(String ref);

    Collection<K> asCollection();

    K addDefinition(VarDefinition def);

    int size();

    boolean isEmpty();

    default SELF self() {
        return (SELF) this;
    }

    default void addDefinitions(VarDefinitionMap<?> definitions) {
        for (VarDefinition def : definitions) {
            addDefinition(def);
        }
    }

    default K getVariableOrException(String ref) {
        K cp = getVariable(ref);
        if (cp == null) {
            throw new SingularFlowException("Variável '" + ref + "' não está definida");
        }
        return cp;
    }

    default boolean contains(String ref) {
        return getVariable(ref) != null;
    }

    /** Set o valor na variável ou lança exception se a variável for desconhecida (não definida). */
    default SELF setValue(String ref, Object value) {
        getVariableOrException(ref).setValue(value);
        return self();
    }

    default Stream<K> stream() {
        return asCollection().stream();
    }

    @Override
    default Iterator<K> iterator() {
        return asCollection().iterator();
    }

    @SuppressWarnings("unchecked")
    default <T extends Object> T getValue(String ref) {
        return (T) getVariableOrException(ref).getValue();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    default <T> T getValue(@Nonnull String ref, @Nonnull T defaultValue) {
        Objects.requireNonNull(defaultValue);
        Object v = getVariableOrException(ref).getValue();
        if (v == null) {
            return defaultValue;
        }
        return (T) v;
    }

    default <T> T getValueType(String ref, Class<T> typeClass) {
        return getValueType(ref, typeClass, null);
    }

    default <T> T getValueType(String ref, Class<T> typeClass, T defaultValue) {
        K cp = getVariableOrException(ref);
        Object o = cp.getValue();
        if (o == null) {
            return defaultValue;
        } else if (typeClass.isInstance(o)) {
            return typeClass.cast(o);
        }
        throw new SingularFlowException("'" + ref + "' é do tipo " + o.getClass().getName() + " e o esperado era " + typeClass.getName());
    }

    default void addValues(VarInstanceMap<?, ?> vars, boolean createMissingTypes) {
        for (VarInstance var : vars) {
            VarInstance localVar = getVariable(var.getRef());
            if (localVar == null) {
                if (createMissingTypes) {
                    localVar = addDefinition(var.getDefinition().copy());
                    localVar.setValue(var.getValue());
                }
            } else {
                localVar.setValue(var.getValue());
            }
        }
    }

    // ----------------------------------------------------------
    // Métodos de conveniência para criação dinâmica de váriáveis
    // ----------------------------------------------------------

    default SELF addValue(String ref, Object value, Supplier<VarDefinition> definitionCreator) {
        K var = getVariable(ref);
        if (var == null) {
            VarDefinition def = Objects.requireNonNull(definitionCreator.get());
            var = addDefinition(def);
        }
        var.setValue(value);
        return self();
    }

    /** Seta o valor na variável ou cria a variável senão existir. */
    default SELF addValue(String ref, VarType type, Object value) {
        return addValue(ref, value, () -> getVarService().newDefinition(ref, ref, type));
    }

    default SELF addValueString(String ref, String value) {
        return addValue(ref, value, () -> getVarService().newDefinitionString(ref, ref, null));
    }

    default SELF addValueDate(String ref, Date value) {
        return addValue(ref, value, () -> getVarService().newDefinitionDate(ref, ref));
    }

    default SELF addValueInteger(String ref, Integer value) {
        return addValue(ref, value, () -> getVarService().newDefinitionInteger(ref, ref));
    }

    default SELF addValueBoolean(String ref, Boolean value) {
        return addValue(ref, value, () -> getVarService().newDefinitionBoolean(ref, ref));
    }

    // ----------------------------------------------------------
    // Métodos de conveniência para leitura
    // ----------------------------------------------------------

    default String getValueString(String ref) {
        return getValueType(ref, String.class, null);
    }

    default String getValueString(String ref, String defaultValue) {
        return getValueType(ref, String.class, defaultValue);
    }

    default Integer getValueInteger(String ref) {
        return getValueType(ref, Integer.class);
    }

    default Integer getValueInteger(String ref, Integer defaultValue) {
        return getValueType(ref, Integer.class, defaultValue);
    }

    default Double getValueDouble(String ref) {
        return getValueType(ref, Double.class);
    }

    default Double getValueDouble(String ref, Double defaultValue) {
        return getValueType(ref, Double.class, defaultValue);
    }

    default BigDecimal getValueBigDecimal(String ref) {
        return getValueType(ref, BigDecimal.class);
    }

    default BigDecimal getValueBigDecimal(String ref, BigDecimal defaultValue) {
        return getValueType(ref, BigDecimal.class, defaultValue);
    }

    default Boolean getValueBoolean(String ref) {
        return getValueType(ref, Boolean.class);
    }

    default boolean getValueBoolean(String ref, boolean defaultValue) {
        Boolean b = getValueType(ref, Boolean.class);
        if (b == null) {
            return defaultValue;
        }
        return b;
    }

    default Date getValueDate(String ref) {
        return getValueType(ref, Date.class);
    }

    default ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        for (VarInstance cp : this) {
            if (cp.isRequired() && cp.getValue() == null) {
                result.addErro(cp, "Campo  obrigatório");
            }
        }
        return result;
    }

    void onValueChanged(VarInstance changedVar);

    static VarInstanceMap<?,?> empty() {
        return EMPTY_INSTANCE;
    }

    VarInstanceMap<?,?> EMPTY_INSTANCE = new VarInstanceMapEmpty();

    final class VarInstanceMapEmpty implements VarInstanceMap<VarInstance,VarInstanceMapEmpty> {

        @Override
        public VarInstance getVariable(String ref) {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void onValueChanged(VarInstance changedVar) {
            throw new SingularFlowException("Método não suportado");
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Collection<VarInstance> asCollection() {
            return Collections.emptyList();
        }

        @Override
        public VarInstance addDefinition(VarDefinition def) {
            throw new SingularFlowException("Método não suportado");
        }

        @Override
        public VarService getVarService() {
            throw new SingularFlowException("Método não suportado");
        }
    }
}
