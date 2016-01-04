package br.net.mirante.singular.form.mform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Métodos utilitários para manipulação de MTipo.
 *
 * @author Daniel C. Bordin
 */
public abstract class MTypes {

    private MTypes() {}

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incluindo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     */
    public static void visitAllChildren(MTipo<?> parent, Consumer<MTipo<?>> consumer) {
        visitAllContainedTypes(parent, false, consumer);
    }

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incluindo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     * @param containedTypesFirst se true o percorrimento é bottom-up
     */
    public static void visitAllContainedTypes(MTipo<?> parent, boolean containedTypesFirst, Consumer<MTipo<?>> consumer) {
        if (parent instanceof ICompositeType) {
            for (MTipo<?> child : ((ICompositeType) parent).getContainedTypes()) {
                if (containedTypesFirst) {
                    visitAllContainedTypes(child, containedTypesFirst, consumer);
                    consumer.accept(child);
                } else {
                    consumer.accept(child);
                    visitAllContainedTypes(child, containedTypesFirst, consumer);
                }
            }
        }
    }

    /**
     * Percorre a instância informada e todos as instâncias filha da instancia
     * informada chamando o consumidor, incundo os filhos dos filhos. Ou seja,
     * faz um pecorrimento em profundidade.
     */
    public static void visitAll(MTipo<?> type, Consumer<MTipo<?>> consumer) {
        visitAll(type, false, consumer);
    }

    /**
     * Percorre a instância informada e todos as instâncias filha da instancia
     * informada chamando o consumidor, incundo os filhos dos filhos. Ou seja,
     * faz um pecorrimento em profundidade.
     * @param containedTypesFirst se true o percorrimento é bottom-up
     */
    public static void visitAll(MTipo<?> type, boolean containedTypesFirst, Consumer<MTipo<?>> consumer) {
        if (containedTypesFirst) {
            visitAllContainedTypes(type, containedTypesFirst, consumer);
            consumer.accept(type);
        } else {
            consumer.accept(type);
            visitAllContainedTypes(type, containedTypesFirst, consumer);
        }
    }

    /**
     * Retorna uma Stream que percorre os descendentes de <code>node</code> do tipo especificado.
     * @param node instância inicial da busca
     * @return Stream das instâncias de descendentes
     */
    public static Stream<MTipo<?>> streamDescendants(MTipo<?> root, boolean includeRoot) {
        return StreamSupport.stream(new MTypeRecursiveSpliterator(root, includeRoot), false);
    }

    public static Collection<? extends MTipo<?>> containedTypes(MTipo<?> node) {
        List<MTipo<?>> result = new ArrayList<>();
        if (node instanceof ICompositeType) {
            result.addAll(((ICompositeType) node).getContainedTypes());
        }
        result.removeIf(it -> it == null);
        return result;
    }

    public static List<MTipo<?>> listAscendants(MTipo<?> root, boolean includeRoot) {

        final List<MTipo<?>> list = new ArrayList<>();

        if (includeRoot)
            list.add(root);

        MEscopo tipo = root.getEscopoPai();
        while (tipo != null) {
            if (tipo instanceof MTipo<?>)
                list.add((MTipo<?>) tipo);
            tipo = tipo.getEscopoPai();
        }
        return list;
    }
}
