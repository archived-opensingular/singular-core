package br.net.mirante.singular.form.mform;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Spliterator;
import java.util.function.Consumer;

public class MInstanceRecursiveSpliterator implements Spliterator<SInstance> {
    private final Deque<SInstance> deque = new ArrayDeque<>();
    public MInstanceRecursiveSpliterator(SInstance root, boolean includeRoot) {
        if (includeRoot)
            this.deque.add(root);
        else
            this.deque.addAll(MInstances.children(root));
    }
    @Override
    public boolean tryAdvance(Consumer<? super SInstance> action) {
        if (deque.isEmpty())
            return false;

        final SInstance node = deque.removeFirst();
        deque.addAll(MInstances.children(node));
        action.accept(node);
        return true;
    }
    @Override
    public Spliterator<SInstance> trySplit() {
        return new MInstanceRecursiveSpliterator(deque.removeFirst(), true);
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