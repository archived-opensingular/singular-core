/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import br.net.mirante.singular.form.type.core.SIBoolean;
import br.net.mirante.singular.form.type.core.STypeBoolean;

/**
 * Métodos utilitários para manipulação de MInstance.
 *
 * @author Daniel C. Bordin
 */
public abstract class SInstances {

    public static SIComposite getRootInstance(SInstance instance) {
        if (instance.getParent() == null){
            return (SIComposite) instance;
        }
        return getRootInstance(instance.getParent());
    }

    public static interface IVisit<R> extends Serializable {
        void stop();
        void stop(R result);
        void dontGoDeeper();
        void setPartial(R result);
        R getPartial();
    }

    public interface IVisitor<I extends SInstance, R> {
        public void onInstance(I object, IVisit<R> visit);
    }

    public interface IVisitFilter extends Serializable {
        boolean visitObject(Object object);
        default boolean visitChildren(Object object) {
            return true;
        }
        public static IVisitFilter ANY = o -> true;
    }

    private SInstances() {}

    /**
     * Faz um pecorrimento em profundidade de parent e seus filhos.
     */
    public static <I extends SInstance, R> Optional<R> visit(SInstance instance, IVisitor<I, R> visitor) {
        return visit(instance, IVisitFilter.ANY, visitor);
    }

    /**
     * Faz um pecorrimento em profundidade dos filhos de parent.
     */
    public static <R> Optional<R> visitChildren(SInstance parent, IVisitor<SInstance, R> visitor) {
        return visitChildren(parent, IVisitFilter.ANY, visitor);
    }

    /**
     * Faz um pecorrimento em profundidade da instância e seus filhos, em ordem pós-fixada (primeiro filhos, depois pais).
     */
    public static <R> Optional<R> visitPostOrder(SInstance parent, IVisitor<SInstance, R> visitor) {
        return visitPostOrder(parent, IVisitFilter.ANY, visitor);
    }

    /**
     * Faz um pecorrimento em profundidade de parent e seus filhos.
     */
    @SuppressWarnings("unchecked")
    public static <I extends SInstance, R> Optional<R> visit(SInstance rootInstance, IVisitFilter filter, IVisitor<I, R> visitor) {
        final Visit<R> visit = new Visit<>(null);
        visitor.onInstance((I) rootInstance, visit);
        if (visit.dontGoDeeper || visit.stopped)
            return Optional.ofNullable(visit.result);

        internalVisitChildren(rootInstance, visitor, filter, visit);
        return Optional.ofNullable(visit.result);
    }

    /**
     * Faz um pecorrimento em profundidade dos filhos de parent.
     */
    public static <R> Optional<R> visitChildren(SInstance rootInstance, IVisitFilter filter, IVisitor<SInstance, R> visitor) {
        Visit<R> visit = new Visit<>(null);
        internalVisitChildren(rootInstance, visitor, filter, visit);
        return Optional.ofNullable(visit.result);
    }

    /**
     * Faz um pecorrimento em profundidade da instância e seus filhos, em ordem pós-fixada (primeiro filhos, depois pais).
     */
    public static <R> Optional<R> visitPostOrder(SInstance rootInstance, IVisitFilter filter, IVisitor<SInstance, R> visitor) {
        Visit<R> visit = new Visit<>(null);
        internalVisitPostOrder(rootInstance, visitor, filter, visit);
        return Optional.ofNullable(visit.result);
    }

    /**
     * Implements the prefixed traversal logic.
     * @param rootInstance
     * @param visitor
     * @param filter
     * @param visit
     */
    @SuppressWarnings("unchecked")
    private static <I extends SInstance, R> void internalVisitChildren(SInstance rootInstance, IVisitor<I, R> visitor, IVisitFilter filter, Visit<R> visit) {
        if (rootInstance instanceof ICompositeInstance) {
            for (SInstance object : ((ICompositeInstance) rootInstance).getAllChildren()) {
                if (filter.visitObject(object)) {
                    I child = (I) object;
                    final Visit<R> childVisit = new Visit<>(visit.getPartial());
                    visitor.onInstance(child, childVisit);
                    visit.setPartial(childVisit.getPartial());

                    if (childVisit.stopped) {
                        visit.stop(childVisit.result);
                        return;
                    }
                    if (childVisit.dontGoDeeper)
                        continue;
                }

                if (!visit.dontGoDeeper && (object instanceof ICompositeInstance) && filter.visitChildren(object)) {
                    internalVisitChildren(object, visitor, filter, visit);
                    if (visit.stopped)
                        return;
                }
            }
        }
    }

    /**
     * Implements the postfixed traversal logic.
     * @param rootInstance
     * @param visitor
     * @param filter
     * @param visit
     */
    @SuppressWarnings("unchecked")
    private static <I extends SInstance, R> void internalVisitPostOrder(SInstance rootInstance, IVisitor<I, R> visitor, IVisitFilter filter, Visit<R> visit) {
        boolean dontGoAbove = false;
        if (rootInstance instanceof ICompositeInstance) {
            final ICompositeInstance parent = (ICompositeInstance) rootInstance;
            if (filter.visitChildren(rootInstance)) {
                final Visit<R> childVisit = new Visit<>(visit.getPartial());
                for (SInstance child : parent.getAllChildren()) {
                    if (filter.visitObject(child)) {
                        internalVisitPostOrder(child, visitor, filter, childVisit);
                        visit.setPartial(childVisit.getPartial());
                        if (childVisit.dontGoDeeper)
                            dontGoAbove = true;
                        if (childVisit.stopped) {
                            visit.stop(childVisit.result);
                            return;
                        }
                    }
                }
            }
        }

        if (!dontGoAbove && filter.visitObject(rootInstance))
            visitor.onInstance((I) rootInstance, visit);
    }

    /**
     * Busca por um ancestral de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @param ancestorType tipo do ancestral
     * @return instância do ancestral do tipo especificado
     * @throws NoSuchElementException se não encontrar nenhum ancestral deste tipo
     */
    public static <P extends SInstance & ICompositeInstance> P getAncestor(SInstance node, SType<P> ancestorType) throws NoSuchElementException {
        return findAncestor(node, ancestorType).get();
    }

    public static <A extends SType<?>> Optional<SInstance> findAncestor(SInstance node, Class<A> ancestorType) {
        for (SInstance parent = node.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.getType().getClass().equals(ancestorType)) {
                return Optional.of(parent);
            }
        }
        return Optional.empty();
    }

    /**
     * Busca por um ancestral de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @param ancestorType tipo do ancestral
     * @return Optional da instância do ancestral do tipo especificado
     */
    @SuppressWarnings("unchecked")
    public static <A extends SInstance & ICompositeInstance> Optional<A> findAncestor(SInstance node, SType<A> ancestorType) {
        for (SInstance parent = node.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.getType().isTypeOf(ancestorType)) {
                return Optional.of((A) parent);
            }
        }
        return Optional.empty();
    }

    /**
     * Busca por um ancestral de <code>node</code> cujo tipo é um ancestral comum do tipo de <code>node</code>
     * e <code>targetType</code>.
     * @param node instância inicial da busca
     * @param targetType tipo de outro campo
     * @return Optional da instância do ancestral comum
     */
    @SuppressWarnings("unchecked")
    public static <CA extends SInstance & ICompositeInstance> Optional<CA> findCommonAncestor(SInstance node, SType<?> targetType) {
        for (SScope type = targetType; type != null; type = type.getParentScope()) {
            for (SInstance ancestor = node; ancestor != null; ancestor = ancestor.getParent()) {
                if (SType.class.isAssignableFrom(type.getClass()) && ancestor.getType().isTypeOf((SType<?>) type) && ancestor instanceof ICompositeInstance) {
                    return Optional.of((CA) ancestor);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Busca por o no mais próximo de <code>node</code> na hierarquia de instâncias, cujo tipo é igual a <code>targetType</code>.
     * @param node instância inicial da busca
     * @param targetType tipo do campo a ser procurado
     * @return Optional da instância do targetType encontrado
     */
    public static <A extends SInstance> Optional<A> findNearest(SInstance node, SType<A> targetType) {
        Optional<A> desc = SInstances.findDescendant(node, targetType);
        if (desc.isPresent())
            return desc;
        else
            return SInstances.findCommonAncestor(node, targetType)
                .flatMap(ancestor -> ancestor.findDescendant(targetType))
                .map(targetNode -> targetNode);
    }

    /**
     * Lista os ancestrais de <code>node</code>.
     * @param instance instância inicial da busca
     * @return Lista das instâncias de ancestrais do tipo especificado
     */
    public static List<SInstance> listAscendants(SInstance instance) {
        return listAscendants(instance, null);
    }

    public static List<SInstance> listAscendants(SInstance instance, boolean selfIncluded) {
        return listAscendants(instance, null, selfIncluded);
    }
    /**
     * Lista os ancestrais de <code>node</code>.
     * @param instance instância inicial da busca
     * @return Lista das instâncias de ancestrais do tipo especificado
     */
    public static List<SInstance> listAscendants(SInstance instance, SType<?> limitInclusive) {
        return listAscendants(instance, limitInclusive, false);
    }

    /**
     * Lista os ancestrais de <code>node</code>.
     * @param instance instância inicial da busca
     * @return Lista das instâncias de ancestrais do tipo especificado
     */
    public static List<SInstance> listAscendants(SInstance instance, SType<?> limitInclusive, boolean selfIncluded) {
        List<SInstance> list = new ArrayList<>();
        if (selfIncluded) {
            list.add(instance);
        }
        SInstance node = instance.getParent();
        while (node != null && !node.getType().isTypeOf(limitInclusive)) {
            list.add(node);
            node = node.getParent();
        }
        return list;
    }

    /**
     * Busca pelo primeiro descendente de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @param descendantType tipo do descendente
     * @return instância do primeiro descendente do tipo especificado
     * @throws NoSuchElementException se não encontrar nenhum descendente deste tipo
     */
    public static <D extends SInstance> D getDescendant(SInstance node, SType<D> descendantType) {
        return findDescendant(node, descendantType).get();
    }

    /**
     * Busca descendente de <code>node</code> do tipo especificado, por id.
     * @param node instância inicial da busca
     * @param descendantId id do descendente
     * @param descendantType tipo do descendente
     * @return instância especificada
     * @throws NoSuchElementException se não encontrar a instancia especificada
     */
    public static <D extends SInstance> D getDescendantById(SInstance node, Integer descendantId, SType<D> descendantType) {
        return findDescendantById(node, descendantId, descendantType).get();
    }

    /**
     * Busca descendente de <code>node</code> do tipo especificado, por id.
     * @param node instância inicial da busca
     * @param descendantId id do descendente
     * @param descendantType tipo do descendente
     * @return Optional da instância especificada
     */
    public static <D extends SInstance> Optional<D> findDescendantById(SInstance node, Integer descendantId, SType<D> descendantType) {
        return streamDescendants(node, true, descendantType)
            .filter(it -> descendantId.equals(it.getId()))
            .findAny();
    }

    /**
     * Busca descendente de <code>node</code> do tipo especificado, por id.
     * @param node instância inicial da busca
     * @param descendantId id do descendente
     * @return Optional da instância especificada
     */
    public static Optional<SInstance> findDescendantById(SInstance node, Integer descendantId) {
        return streamDescendants(node, true)
            .filter(it -> descendantId.equals(it.getId()))
            .findAny();
    }

    /**
     * Busca pelo primeiro descendente de <code>node</code> do tipo especificado.
     * @param instance instância inicial da busca
     * @param descendantType tipo do descendente
     * @return Optional da instância do primeiro descendente do tipo especificado
     */
    @SuppressWarnings("unchecked")
    public static <D extends SInstance> Optional<D> findDescendant(SInstance instance, SType<D> descendantType) {
        final Deque<SInstance> deque = new ArrayDeque<>();
        deque.add(instance);
        while (!deque.isEmpty()) {
            final SInstance node = deque.removeFirst();
            if (node.getType().isTypeOf(descendantType)) {
                return Optional.of((D) node);
            } else {
                deque.addAll(children(node));
            }
        }
        return Optional.empty();
    }

    /**
     * Lista os descendentes de <code>node</code> do tipo especificado.
     * @param instance instância inicial da busca
     * @param descendantType tipo do descendente
     * @return Lista das instâncias de descendentes do tipo especificado
     */
    public static <D extends SInstance> List<D> listDescendants(SInstance instance, SType<D> descendantType) {
        return listDescendants(instance, descendantType, Function.identity());
    }

    /**
     * Lista os descendentes de <code>node</code> do tipo especificado.
     * @param instance instância inicial da busca
     * @param descendantType tipo do descendente
     * @return Lista das instâncias de descendentes do tipo especificado
     */
    @SuppressWarnings("unchecked")
    public static <D extends SInstance, V> List<V> listDescendants(SInstance instance, SType<?> descendantType, Function<D, V> function) {
        List<V> result = new ArrayList<>();
        final Deque<SInstance> deque = new ArrayDeque<>();
        deque.add(instance);
        while (!deque.isEmpty()) {
            final SInstance node = deque.removeFirst();
            if (node.getType().isTypeOf(descendantType)) {
                result.add(function.apply((D) node));
            } else {
                deque.addAll(children(node));
            }
        }
        return result;
    }

    /**
     * Retorna uma Stream que percorre os descendentes de <code>node</code> do tipo especificado.
     * @param root instância inicial da busca
     * @param descendantType tipo do descendente
     * @return Stream das instâncias de descendentes do tipo especificado
     */
    @SuppressWarnings("unchecked")
    public static <D extends SInstance> Stream<D> streamDescendants(SInstance root, boolean includeRoot, SType<D> descendantType) {
        return streamDescendants(root, includeRoot)
            .filter(it -> it.getType().isTypeOf(descendantType))
            .map(it -> (D) it);
    }

    /**
     * Retorna uma Stream que percorre os descendentes de <code>node</code> do tipo especificado.
     * @param root instância inicial da busca
     * @return Stream das instâncias de descendentes
     */
    public static Stream<SInstance> streamDescendants(SInstance root, boolean includeRoot) {
        return StreamSupport.stream(new SInstanceRecursiveSpliterator(root, includeRoot), false);
    }

    /*
     * Lista os filhos diretos da instância <code>node</code>, criando-os se necessário.
     */
    static Collection<SInstance> children(SInstance node) {
        List<SInstance> result = new ArrayList<>();
        if (node instanceof ICompositeInstance) {
            result.addAll(((ICompositeInstance) node).getAllChildren());
        }
        return result;
    }

    public static void updateBooleanAttribute(
                                              SInstance instance,
                                              AtrRef<STypeBoolean, SIBoolean, Boolean> valueAttribute,
                                              AtrRef<STypePredicate, SIPredicate, Predicate<SInstance>> predicateAttribute) {

        Predicate<SInstance> pred = instance.getAttributeValue(predicateAttribute);
        if (pred != null)
            instance.setAttributeValue(valueAttribute, pred.test(instance));
    }

    public static <V> V attributeValue(SInstance instance, AtrRef<?, ?, V> attribute, V defaultValue) {
        V value = instance.getAttributeValue(attribute);
        return (value != null) ? value : defaultValue;
    }
    public static <V> boolean hasAttributeValue(SInstance instance, AtrRef<?, ?, V> attribute) {
        V value = instance.getAttributeValue(attribute);
        return (value != null);
    }

    private static class Visit<R> implements IVisit<R> {
        boolean dontGoDeeper;
        boolean stopped;
        R       result;
        R       partial;
        public Visit(R partial) {
            this.partial = partial;
        }
        @Override
        public void dontGoDeeper() {
            this.dontGoDeeper = true;
        }
        @Override
        public void stop() {
            this.stopped = true;
        }
        @Override
        public void stop(R result) {
            this.result = result;
            stop();
        }
        @Override
        public void setPartial(R result) {
            partial = result;
        }
        @Override
        public R getPartial() {
            return partial;
        }
    }
}
