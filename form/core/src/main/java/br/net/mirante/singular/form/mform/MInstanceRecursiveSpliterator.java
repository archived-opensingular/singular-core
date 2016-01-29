package br.net.mirante.singular.form.mform;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Spliterator;
import java.util.function.Consumer;

public class MInstanceRecursiveSpliterator implements Spliterator<SInstance2> {
    private final Deque<SInstance2> deque = new ArrayDeque<>();
    public MInstanceRecursiveSpliterator(SInstance2 root, boolean includeRoot) {
        if (includeRoot)
            this.deque.add(root);
        else
            this.deque.addAll(MInstances.children(root));
    }
    @Override
    public boolean tryAdvance(Consumer<? super SInstance2> action) {
        if (deque.isEmpty())
            return false;

        final SInstance2 node = deque.removeFirst();
        deque.addAll(MInstances.children(node));
        action.accept(node);
        return true;
    }
    @Override
    public Spliterator<SInstance2> trySplit() {
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