package br.net.mirante.singular.form.mform;

import java.util.*;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;

public class MTypeRecursiveSpliterator implements Spliterator<SType<?>> {
    private final Deque<SType<?>> deque = new ArrayDeque<>();
    public MTypeRecursiveSpliterator(SType<?> root, boolean includeRoot) {
        if (includeRoot)
            this.deque.add(root);
//        else
//            this.deque.addAll(MTypes.containedTypes(root));

        Collection possibleTypes = MTypes.containedTypes(root);
        Collection<SType> newTypes = newArrayList();
        do {
            for(SType  t: (Collection<SType>)possibleTypes){
                if(!this.deque.contains(t)){
                    this.deque.add(t);
                    newTypes.add(t);
                }
            }
            Collection<SType> childTypes = newArrayList();
            for(SType t: newTypes){
                childTypes.addAll(MTypes.containedTypes(t));
            }
            newTypes = newArrayList();
            possibleTypes = childTypes;
        }while(!possibleTypes.isEmpty());
    }
    @Override
    public boolean tryAdvance(Consumer<? super SType<?>> action) {
        if (deque.isEmpty())
            return false;

        final SType<?> node = deque.removeFirst();
//        deque.addAll(MTypes.containedTypes(node));
        action.accept(node);
        return true;
    }
    @Override
    public Spliterator<SType<?>> trySplit() {
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