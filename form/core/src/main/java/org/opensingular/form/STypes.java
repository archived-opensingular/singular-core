/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form;

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
