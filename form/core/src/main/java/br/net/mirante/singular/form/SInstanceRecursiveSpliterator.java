/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

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