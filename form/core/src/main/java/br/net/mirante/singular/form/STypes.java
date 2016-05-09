/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

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
public abstract class STypes {

    private STypes() {}

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incluindo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     */
    public static void visitAllChildren(SType<?> parent, Consumer<SType<?>> consumer) {
        visitAllContainedTypes(parent, false, consumer);
    }

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incluindo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     * @param containedTypesFirst se true o percorrimento é bottom-up
     */
    public static void visitAllContainedTypes(SType<?> parent, boolean containedTypesFirst, Consumer<SType<?>> consumer) {
        if (parent instanceof ICompositeType) {
            for (SType<?> child : ((ICompositeType) parent).getContainedTypes()) {
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
    public static void visitAll(SType<?> type, Consumer<SType<?>> consumer) {
        visitAll(type, false, consumer);
    }

    /**
     * Percorre a instância informada e todos as instâncias filha da instancia
     * informada chamando o consumidor, incundo os filhos dos filhos. Ou seja,
     * faz um pecorrimento em profundidade.
     * @param containedTypesFirst se true o percorrimento é bottom-up
     */
    public static void visitAll(SType<?> type, boolean containedTypesFirst, Consumer<SType<?>> consumer) {
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
    public static Stream<SType<?>> streamDescendants(SType<?> root, boolean includeRoot) {
        return StreamSupport.stream(new STypeRecursiveSpliterator(root, includeRoot), false);
    }

    public static Collection<? extends SType<?>> containedTypes(SType<?> node) {
        List<SType<?>> result = new ArrayList<>();
        if (node instanceof ICompositeType) {
            result.addAll(((ICompositeType) node).getContainedTypes());
        }
        result.removeIf(it -> it == null);
        return result;
    }

    public static List<SType<?>> listAscendants(SType<?> root, boolean includeRoot) {

        final List<SType<?>> list = new ArrayList<>();

        if (includeRoot)
            list.add(root);

        SScope tipo = root.getParentScope();
        while (tipo != null) {
            if (tipo instanceof SType<?>)
                list.add((SType<?>) tipo);
            tipo = tipo.getParentScope();
        }
        return list;
    }
}
