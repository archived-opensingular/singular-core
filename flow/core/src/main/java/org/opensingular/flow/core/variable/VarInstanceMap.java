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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.stream.Stream;

public interface VarInstanceMap<K extends VarInstance> extends VarServiceEnabled, Serializable, Iterable<K> {


    public K getVariable(String ref);

    public Collection<K> asCollection();

    public K addDefinition(VarDefinition def);

    public int size();

    public boolean isEmpty();

    public default void addDefinitions(VarDefinitionMap<?> definitions) {
        for (VarDefinition def : definitions) {
            addDefinition(def);
        }
    }

    public default K getVariableOrException(String ref) {
        K cp = getVariable(ref);
        if (cp == null) {
            throw new IllegalArgumentException("Variável '" + ref + "' não está definida");
        }
        return cp;
    }

    public default boolean contains(String ref) {
        return getVariable(ref) != null;
    }

    public default void setValue(String ref, Object valor) {
        getVariableOrException(ref).setValue(valor);
    }

    public default Stream<K> stream() {
        return asCollection().stream();
    }

    @Override
    public default Iterator<K> iterator() {
        return asCollection().iterator();
    }

    @SuppressWarnings("unchecked")
    public default <T extends Object> T getValue(String ref) {
        return (T) getVariableOrException(ref).getValue();
    }

    @SuppressWarnings("unchecked")
    public default <T> T getValue(String ref, T defaultValue) {
        Object v = getVariableOrException(ref).getValue();
        if (v == null) {
            return defaultValue;
        }
        return (T) v;
    }

    public default <T> T getValueType(String ref, Class<T> typeClass) {
        return getValueType(ref, typeClass, null);
    }

    public default <T> T getValueType(String ref, Class<T> typeClass, T defaultValue) {
        K cp = getVariableOrException(ref);
        Object o = cp.getValue();
        if (o == null) {
            return defaultValue;
        } else if (typeClass.isInstance(o)) {
            return typeClass.cast(o);
        }
        throw new SingularFlowException("'" + ref + "' é do tipo " + o.getClass().getName() + " e o esperado era " + typeClass.getName());
    }

    public default void addValues(VarInstanceMap<?> vars, boolean createMissingTypes) {
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

    public default void addValue(String ref, VarType type, Object value) {
        K var = getVariable(ref);
        if (var == null) {
            var = addDefinition(getVarService().newDefinition(ref, ref, type));
        }
        var.setValue(value);
    }

    public default void addValueString(String ref, String value) {
        K var = getVariable(ref);
        if (var == null) {
            var = addDefinition(getVarService().newDefinitionString(ref, ref, null));
        }
        var.setValue(value);
    }

    public default void addValueDate(String ref, Date value) {
        K var = getVariable(ref);
        if (var == null) {
            var = addDefinition(getVarService().newDefinitionDate(ref, ref));
        }
        var.setValue(value);
    }

    public default void addValueInteger(String ref, Integer value) {
        K var = getVariable(ref);
        if (var == null) {
            var = addDefinition(getVarService().newDefinitionInteger(ref, ref));
        }
        var.setValue(value);
    }

    public default void addValueBoolean(String ref, Boolean value) {
        K var = getVariable(ref);
        if (var == null) {
            var = addDefinition(getVarService().newDefinitionBoolean(ref, ref));
        }
        var.setValue(value);
    }

    // ----------------------------------------------------------
    // Métodos de conveniência para leitura
    // ----------------------------------------------------------

    public default String getValueString(String ref) {
        return getValueType(ref, String.class, null);
    }

    public default String getValueString(String ref, String defaultValue) {
        return getValueType(ref, String.class, defaultValue);
    }

    public default Integer getValueInteger(String ref) {
        return getValueType(ref, Integer.class);
    }

    public default Integer getValueInteger(String ref, Integer defaultValue) {
        return getValueType(ref, Integer.class, defaultValue);
    }

    public default Double getValueDouble(String ref) {
        return getValueType(ref, Double.class);
    }

    public default Double getValueDouble(String ref, Double defaultValue) {
        return getValueType(ref, Double.class, defaultValue);
    }

    public default Boolean getValueBoolean(String ref) {
        return getValueType(ref, Boolean.class);
    }

    public default boolean getValueBoolean(String ref, boolean defaultValue) {
        Boolean b = getValueType(ref, Boolean.class);
        if (b == null) {
            return defaultValue;
        }
        return b;
    }

    public default Date getValueDate(String ref) {
        return getValueType(ref, Date.class);
    }

    public default ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        for (VarInstance cp : this) {
            if (cp.isRequired() && cp.getValue() == null) {
                result.addErro(cp, "Campo  obrigatório");
            }
        }
        return result;
    }

    public void onValueChanged(VarInstance changedVar);

    public static VarInstanceMap<?> empty() {
        return EMPTY_INSTANCE;
    }

    public static final VarInstanceMap<?> EMPTY_INSTANCE = new VarInstanceMap<VarInstance>() {

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
    };
}
