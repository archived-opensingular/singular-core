package br.net.mirante.singular.form.mform;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import br.net.mirante.singular.form.mform.core.SIBoolean;
import br.net.mirante.singular.form.mform.core.STypeBoolean;

/**
 * Métodos utilitários para manipulação de MInstance.
 *
 * @author Daniel C. Bordin
 */
public abstract class MInstances {

    private MInstances() {}

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incluindo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     */
    public static void visitAllChildren(SInstance parent, Consumer<SInstance> consumer) {
        visitAllChildren(parent, false, consumer);
    }

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incluindo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     * @param childrenFirst se true o percorrimento é bottom-up
     */
    public static void visitAllChildren(SInstance parent, boolean childrenFirst, Consumer<SInstance> consumer) {
        if (parent instanceof ICompositeInstance) {
            for (SInstance child : ((ICompositeInstance) parent).getChildren()) {
                if (childrenFirst) {
                    visitAllChildren(child, childrenFirst, consumer);
                    consumer.accept(child);
                } else {
                    consumer.accept(child);
                    visitAllChildren(child, childrenFirst, consumer);
                }
            }
        }
    }

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incundo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     * @param instance
     */
    public static void visitAllChildrenIncludingEmpty(SInstance instance, Consumer<SInstance> consumer) {
        visitAllChildrenIncludingEmpty(instance, false, consumer);
    }

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incundo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     * @param instance
     * @param childrenFirst se true o percorrimento é bottom-up
     */
    public static void visitAllChildrenIncludingEmpty(SInstance instance, boolean childrenFirst, Consumer<SInstance> consumer) {
        if (instance instanceof ICompositeInstance) {
            for (SInstance child : ((ICompositeInstance) instance).getAllChildren()) {
                if (childrenFirst) {
                    visitAllChildrenIncludingEmpty(child, childrenFirst, consumer);
                    consumer.accept(child);
                } else {
                    consumer.accept(child);
                    visitAllChildrenIncludingEmpty(child, childrenFirst, consumer);
                }
            }
        }
    }

    /**
     * Percorre a instância informada e todos as instâncias filha da instancia
     * informada chamando o consumidor, incundo os filhos dos filhos. Ou seja,
     * faz um pecorrimento em profundidade.
     */
    public static void visitAll(SInstance instance, Consumer<SInstance> consumer) {
        visitAll(instance, false, consumer);
    }

    /**
     * Percorre a instância informada e todos as instâncias filha da instancia
     * informada chamando o consumidor, incundo os filhos dos filhos. Ou seja,
     * faz um pecorrimento em profundidade.
     * @param childrenFirst se true o percorrimento é bottom-up
     */
    public static void visitAll(SInstance instance, boolean childrenFirst, Consumer<SInstance> consumer) {
        if (childrenFirst) {
            visitAllChildren(instance, childrenFirst, consumer);
            consumer.accept(instance);
        } else {
            consumer.accept(instance);
            visitAllChildren(instance, childrenFirst, consumer);
        }
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

    /**
     * Busca por um ancestral de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @param ancestorType tipo do ancestral
     * @return Optional da instância do ancestral do tipo especificado
     */
    @SuppressWarnings("unchecked")
    public static <A extends SInstance & ICompositeInstance> Optional<A> findAncestor(SInstance node, SType<A> ancestorType) {
        for (SInstance parent = node.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.getMTipo() == ancestorType) {
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
        for (MEscopo type = targetType; type != null; type = type.getEscopoPai()) {
            for (SInstance ancestor = node; ancestor != null; ancestor = ancestor.getParent()) {
                if (ancestor.getMTipo() == type) {
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
        return MInstances.findCommonAncestor(node, targetType)
            .flatMap(ancestor -> ancestor.findDescendant(targetType))
            .map(targetNode -> (A) targetNode);
    }

    /**
     * Lista os ancestrais de <code>node</code>.
     * @param node instância inicial da busca
     * @return Lista das instâncias de ancestrais do tipo especificado
     */
    public static List<SInstance> listAscendants(SInstance instance) {
        return listAscendants(instance, null);
    }

    /**
     * Lista os ancestrais de <code>node</code>.
     * @param node instância inicial da busca
     * @return Lista das instâncias de ancestrais do tipo especificado
     */
    public static List<SInstance> listAscendants(SInstance instance, SType<?> limitInclusive) {
        List<SInstance> list = new ArrayList<>();
        SInstance node = instance.getParent();
        while (node != null && node.getMTipo() != limitInclusive) {
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
     * Busca pelo primeiro descendente de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @param descendantType tipo do descendente
     * @return Optional da instância do primeiro descendente do tipo especificado
     */
    @SuppressWarnings("unchecked")
    public static <D extends SInstance> Optional<D> findDescendant(SInstance instancia, SType<D> descendantType) {
        final Deque<SInstance> deque = new ArrayDeque<>();
        deque.add(instancia);
        while (!deque.isEmpty()) {
            final SInstance node = deque.removeFirst();
            if (node.getMTipo() == descendantType) {
                return Optional.of((D) node);
            } else {
                deque.addAll(children(node));
            }
        }
        return Optional.empty();
    }

    /**
     * Lista os descendentes de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @param descendantType tipo do descendente
     * @return Lista das instâncias de descendentes do tipo especificado
     */
    public static <D extends SInstance> List<D> listDescendants(SInstance instance, SType<D> descendantType) {
        return listDescendants(instance, descendantType, Function.identity());
    }

    /**
     * Lista os descendentes de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
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
            if (node.getMTipo() == descendantType) {
                result.add(function.apply((D) node));
            } else {
                deque.addAll(children(node));
            }
        }
        return result;
    }

    /**
     * Retorna uma Stream que percorre os descendentes de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @param descendantType tipo do descendente
     * @return Stream das instâncias de descendentes do tipo especificado
     */
    @SuppressWarnings("unchecked")
    public static <D extends SInstance> Stream<D> streamDescendants(SInstance root, boolean includeRoot, SType<D> descendantType) {
        return streamDescendants(root, includeRoot)
            .filter(it -> it.getMTipo() == descendantType)
            .map(it -> (D) it);
    }

    /**
     * Retorna uma Stream que percorre os descendentes de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @return Stream das instâncias de descendentes
     */
    public static Stream<SInstance> streamDescendants(SInstance root, boolean includeRoot) {
        return StreamSupport.stream(new MInstanceRecursiveSpliterator(root, includeRoot), false);
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

        Predicate<SInstance> pred = instance.getValorAtributo(predicateAttribute);
        if (pred != null)
            instance.setValorAtributo(valueAttribute, pred.test(instance));
    }

    public static <V> V attributeValue(SInstance instance, AtrRef<?, ?, V> attribute, V defaultValue) {
        V value = instance.getValorAtributo(attribute);
        return (value != null) ? value : defaultValue;
    }
    public static <V> boolean hasAttributeValue(SInstance instance, AtrRef<?, ?, V> attribute) {
        V value = instance.getValorAtributo(attribute);
        return (value != null);
    }
}
