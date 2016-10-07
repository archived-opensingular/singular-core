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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Spliterator;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;

public class STypeRecursiveSpliterator implements Spliterator<SType<?>> {
    private final Deque<SType<?>> deque = new ArrayDeque<>();
    public STypeRecursiveSpliterator(SType<?> root, boolean includeRoot) {
        if (includeRoot)
            this.deque.add(root);

        Collection possibleTypes = STypes.containedTypes(root);
        Collection<SType> newTypes = newArrayList();
        do {
            addTypesNotYetPresent(possibleTypes, newTypes);
            Collection<SType> childTypes = createNewPossibleTypesToInspect(newTypes);
            newTypes = newArrayList();
            possibleTypes = childTypes;
        }while(!possibleTypes.isEmpty());
    }

    private Collection<SType> createNewPossibleTypesToInspect(Collection<SType> newTypes) {
        Collection<SType> childTypes = newArrayList();
        for(SType t: newTypes){
            childTypes.addAll(STypes.containedTypes(t));
        }
        return childTypes;
    }

    private void addTypesNotYetPresent(Collection<SType> possibleTypes, Collection<SType> newTypes) {
        for(SType  t: possibleTypes){
            if(!this.deque.contains(t)){
                this.deque.add(t);
                newTypes.add(t);
            }
        }
    }

    @Override
    public boolean tryAdvance(Consumer<? super SType<?>> action) {
        if (deque.isEmpty())
            return false;

        final SType<?> node = deque.removeFirst();
        action.accept(node);
        return true;
    }
    @Override
    public Spliterator<SType<?>> trySplit() {
        return new STypeRecursiveSpliterator(deque.removeFirst(), true);
    }
    @Override
    public long estimateSize() {
        return getExactSizeIfKnown();
    }
    @Override
    public int characteristics() {
        return Spliterator.NONNULL | Spliterator.DISTINCT;
    }
}