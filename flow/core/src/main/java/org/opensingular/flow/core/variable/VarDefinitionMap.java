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

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

public interface VarDefinitionMap<K extends VarDefinition> extends VarServiceEnabled, Iterable<K> {

    public Collection<? extends K> asCollection();

    public K getDefinition(String ref);

    public VarInstanceMap<?> newInstanceMap();

    @SuppressWarnings("unchecked")
    public default Stream<K> stream() {
        return (Stream<K>) asCollection().stream();
    }

    @SuppressWarnings("unchecked")
    @Override
    public default Iterator<K> iterator() {
        return (Iterator<K>) asCollection().iterator();
    }

    public default boolean isEmpty() {
        return asCollection().isEmpty();
    }

    public default boolean hasRequired() {
        return stream().anyMatch(d -> d.isRequired());
    }

    public default boolean contains(String ref) {
        return stream().anyMatch(d -> d.getRef().equalsIgnoreCase(ref));
    }

    public K addVariable(VarDefinition defVar);

    public K addVariable(String ref, String name, VarType varType);

    public default K addVariable(String ref, VarType tipo) {
        return addVariable(ref, ref, tipo);
    }

    public default K addVariableBoolean(String ref, String name) {
        return addVariable(getVarService().newDefinitionBoolean(ref, name));
    }

    public default K addVariableDouble(String ref, String name) {
        return addVariable(getVarService().newDefinitionDouble(ref, name));
    }

    public default K addVariableInteger(String ref, String name) {
        return addVariable(getVarService().newDefinitionInteger(ref, name));
    }

    public default K addVariableInteger(String ref) {
        return addVariableInteger(ref, ref);
    }

    public default K addVariableDate(String ref, String name) {
        return addVariable(getVarService().newDefinitionDate(ref, name));
    }

    public default K addVariableDate(String ref) {
        return addVariableDate(ref, ref);
    }

    public default K addVariableStringMultipleLines(String ref, String name, Integer tamanhoMaximo) {
        return addVariable(getVarService().newDefinitionMultiLineString(ref, name, tamanhoMaximo));
    }

    public default K addVariableStringMultipleLines(String ref, String name) {
        return addVariableStringMultipleLines(ref, name, null);
    }

    public default K addVariableString(String ref, String name, Integer tamanhoMaximo) {
        return addVariable(getVarService().newDefinitionString(ref, name, tamanhoMaximo));
    }

    public default K addVariableString(String ref, String name) {
        return addVariableString(ref, name, null);
    }

    public default K addVariableString(String ref) {
        return addVariableString(ref, ref, null);
    }


}
