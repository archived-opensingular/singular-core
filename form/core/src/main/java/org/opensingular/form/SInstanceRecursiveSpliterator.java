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
import java.util.Deque;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SInstanceRecursiveSpliterator implements Spliterator<SInstance> {
    private final Deque<SInstance> deque = new ArrayDeque<>();
    public SInstanceRecursiveSpliterator(SInstance root, boolean includeRoot) {
        if (includeRoot)
            this.deque.add(root);
        else
            this.deque.addAll(SInstances.children(root));
    }
    @Override
    public boolean tryAdvance(Consumer<? super SInstance> action) {
        if (deque.isEmpty())
            return false;

        final SInstance node = deque.removeFirst();
        deque.addAll(SInstances.children(node));
        action.accept(node);
        return true;
    }
    @Override
    public Spliterator<SInstance> trySplit() {
        return new SInstanceRecursiveSpliterator(deque.removeFirst(), true);
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