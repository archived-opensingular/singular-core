package br.net.mirante.singular.form.mform;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Spliterator;
import java.util.function.Consumer;

public class MTypeRecursiveSpliterator implements Spliterator<MTipo<?>> {
    private final Deque<MTipo<?>> deque = new ArrayDeque<>();
    public MTypeRecursiveSpliterator(MTipo<?> root, boolean includeRoot) {
        if (includeRoot)
            this.deque.add(root);
        else
            this.deque.addAll(MTypes.containedTypes(root));
    }
    @Override
    public boolean tryAdvance(Consumer<? super MTipo<?>> action) {
        if (deque.isEmpty())
            return false;

        final MTipo<?> node = deque.removeFirst();
        if ((node != null) && (MTypes.containedTypes(node) != null))
            deque.addAll(MTypes.containedTypes(node));
        action.accept(node);
        return true;
    }
    @Override
    public Spliterator<MTipo<?>> trySplit() {
        return new MTypeRecursiveSpliterator(deque.removeFirst(), true);
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