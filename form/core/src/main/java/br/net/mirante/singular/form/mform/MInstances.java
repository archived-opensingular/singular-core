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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    public static void visitAllChildren(MInstancia parent, Consumer<MInstancia> consumer) {
        visitAllChildren(parent, false, consumer);
    }

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incluindo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     * @param childrenFirst se true o percorrimento é bottom-up
     */
    public static void visitAllChildren(MInstancia parent, boolean childrenFirst, Consumer<MInstancia> consumer) {
        if (parent instanceof ICompositeInstance) {
            for (MInstancia child : ((ICompositeInstance) parent).getChildren()) {
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
    public static void visitAllChildrenIncludingEmpty(MInstancia instance, Consumer<MInstancia> consumer) {
        visitAllChildrenIncludingEmpty(instance, false, consumer);
    }

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incundo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     * @param instance
     * @param childrenFirst se true o percorrimento é bottom-up
     */
    public static void visitAllChildrenIncludingEmpty(MInstancia instance, boolean childrenFirst, Consumer<MInstancia> consumer) {
        if (instance instanceof ICompositeInstance) {
            for (MInstancia child : ((ICompositeInstance) instance).getAllChildren()) {
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
    public static void visitAll(MInstancia instance, Consumer<MInstancia> consumer) {
        visitAll(instance, false, consumer);
    }

    /**
     * Percorre a instância informada e todos as instâncias filha da instancia
     * informada chamando o consumidor, incundo os filhos dos filhos. Ou seja,
     * faz um pecorrimento em profundidade.
     * @param childrenFirst se true o percorrimento é bottom-up
     */
    public static void visitAll(MInstancia instance, boolean childrenFirst, Consumer<MInstancia> consumer) {
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
    public static <P extends MInstancia & ICompositeInstance> P getAncestor(MInstancia node, MTipo<P> ancestorType) throws NoSuchElementException {
        return findAncestor(node, ancestorType).get();
    }

    /**
     * Busca por um ancestral de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @param ancestorType tipo do ancestral
     * @return Optional da instância do ancestral do tipo especificado
     */
    @SuppressWarnings("unchecked")
    public static <A extends MInstancia & ICompositeInstance> Optional<A> findAncestor(MInstancia node, MTipo<A> ancestorType) {
        for (MInstancia parent = node.getPai(); parent != null; parent = parent.getPai()) {
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
    public static <CA extends MInstancia & ICompositeInstance> Optional<CA> findCommonAncestor(MInstancia node, MTipo<?> targetType) {
        for (MEscopo type = targetType; type != null; type = type.getEscopoPai()) {
            for (MInstancia ancestor = node.getPai(); ancestor != null; ancestor = ancestor.getPai()) {
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
    public static <A extends MInstancia> Optional<A> findNearest(MInstancia node, MTipo<A> targetType) {
        return MInstances.findCommonAncestor(node, targetType)
            .flatMap(ancestor -> ancestor.findDescendant(targetType))
            .map(targetNode -> (A) targetNode);
    }

    /**
     * Lista os ancestrais de <code>node</code>.
     * @param node instância inicial da busca
     * @return Lista das instâncias de ancestrais do tipo especificado
     */
    public static List<MInstancia> listAscendants(MInstancia instance, MTipo<?> limitInclusive) {
        List<MInstancia> list = new ArrayList<>();
        MInstancia node = instance.getPai();
        while (node != null && node.getMTipo() != limitInclusive) {
            list.add(node);
            node = node.getPai();
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
    public static <D extends MInstancia> D getDescendant(MInstancia node, MTipo<D> descendantType) {
        return findDescendant(node, descendantType).get();
    }

    /**
     * Busca pelo primeiro descendente de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @param descendantType tipo do descendente
     * @return Optional da instância do primeiro descendente do tipo especificado
     */
    @SuppressWarnings("unchecked")
    public static <D extends MInstancia> Optional<D> findDescendant(MInstancia instancia, MTipo<D> descendantType) {
        final Deque<MInstancia> deque = new ArrayDeque<>();
        deque.add(instancia);
        while (!deque.isEmpty()) {
            final MInstancia node = deque.removeFirst();
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
    public static <D extends MInstancia> List<D> listDescendants(MInstancia instance, MTipo<D> descendantType) {
        return listDescendants(instance, descendantType, Function.identity());
    }

    /**
     * Lista os descendentes de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @param descendantType tipo do descendente
     * @return Lista das instâncias de descendentes do tipo especificado
     */
    @SuppressWarnings("unchecked")
    public static <D extends MInstancia, V> List<V> listDescendants(MInstancia instance, MTipo<?> descendantType, Function<D, V> function) {
        List<V> result = new ArrayList<>();
        final Deque<MInstancia> deque = new ArrayDeque<>();
        deque.add(instance);
        while (!deque.isEmpty()) {
            final MInstancia node = deque.removeFirst();
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
    public static <D extends MInstancia> Stream<D> streamDescendants(MInstancia root, boolean includeRoot, MTipo<D> descendantType) {
        return streamDescendants(root, includeRoot)
            .filter(it -> it.getMTipo() == descendantType)
            .map(it -> (D) it);
    }

    /**
     * Retorna uma Stream que percorre os descendentes de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @return Stream das instâncias de descendentes
     */
    public static Stream<MInstancia> streamDescendants(MInstancia root, boolean includeRoot) {
        return StreamSupport.stream(new MInstanceRecursiveSpliterator(root, includeRoot), false);
    }

    /*
     * Lista os filhos diretos da instância <code>node</code>, criando-os se necessário.
     */
    static Collection<MInstancia> children(MInstancia node) {
        List<MInstancia> result = new ArrayList<>();
        if (node instanceof ICompositeInstance) {
            result.addAll(((ICompositeInstance) node).getAllChildren());
        }
        return result;
    }
}
