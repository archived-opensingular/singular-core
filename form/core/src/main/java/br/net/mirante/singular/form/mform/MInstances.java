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

/**
 * Métodos utilitários para manipulação de MInstance.
 *
 * @author Daniel C. Bordin
 */
public abstract class MInstances {

    private MInstances() {}

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incundo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     */
    public static void visitAllChildren(MInstancia parent, Consumer<MInstancia> consumer) {
        if (parent instanceof ICompositeInstance) {
            for (MInstancia child : ((ICompositeInstance) parent).getChildren()) {
                consumer.accept(child);
                visitAllChildren(child, consumer);
            }
        }
    }

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incundo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     * @param instance
     * @param includeEmpty se deve visitar tambem campos vazios
     */
    public static void visitAllChildren(MInstancia instance, boolean includeEmpty, Consumer<MInstancia> consumer) {
        if (instance instanceof ICompositeInstance) {
            Collection<? extends MInstancia> children = (includeEmpty)
                ? ((ICompositeInstance) instance).getAllChildren()
                : ((ICompositeInstance) instance).getChildren();
            for (MInstancia child : children) {
                consumer.accept(child);
                visitAllChildren(child, includeEmpty, consumer);
            }
        }
    }

    /**
     * Percorre a instância informada e todos as instâncias filha da instancia
     * informada chamando o consumidor, incundo os filhos dos filhos. Ou seja,
     * faz um pecorrimento em profundidade.
     */
    public static void visitAll(MInstancia instance, Consumer<MInstancia> consumer) {
        consumer.accept(instance);
        if (instance instanceof ICompositeInstance) {
            for (MInstancia child : ((ICompositeInstance) instance).getChildren()) {
                visitAllChildren(child, consumer);
            }
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

    /*
     * Lista os filhos diretos da instância <code>node</code>, criando-os se necessário.
     */
    private static Collection<MInstancia> children(MInstancia node) {
        List<MInstancia> result = new ArrayList<>();
        if (node instanceof MIComposto) {
            result.addAll(((MIComposto) node).getAllFields());
        } else if (node instanceof MILista<?>) {
            result.addAll(((MILista<?>) node).getChildren());
        }
        return result;
    }
}
