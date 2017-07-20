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

package org.opensingular.flow.core;

import org.opensingular.flow.core.entity.IEntityTaskVersion;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;



public class SFlowUtil {

    private static final int PESO_TASK_JAVA = 100;
    private static final int PESO_TASK_WAIT = 300;
    private static final int PESO_TASK_PESSOA = 1000;
    private static final int PESO_TASK_FIM = 100000;

    private SFlowUtil() {}

    public static void sortInstancesByDistanceFromBeginning(List<? extends FlowInstance> instancias, FlowDefinition<?> definicao) {
        instancias.sort((s1, s2) -> compareByDistanceFromBeginning(s1.getLastTaskOrException().getEntityTaskInstance().getTaskVersion(),
                s2.getLastTaskOrException().getEntityTaskInstance().getTaskVersion(), definicao));
    }

    private static int compareByDistanceFromBeginning(IEntityTaskVersion s1, IEntityTaskVersion s2, FlowDefinition<?> definicao) {
        int ordem1 = calculateTaskOrder(s1, definicao);
        int ordem2 = calculateTaskOrder(s2, definicao);
        if (ordem1 != ordem2) {
            return ordem1 - ordem2;
        }
        return s1.getName().compareTo(s2.getName());
    }

    public static <T> void sortByDistanceFromBeginning(List<? extends T> lista, Function<T, IEntityTaskVersion> conversor,
                                                       FlowDefinition<?> definicao) {
        lista.sort(getDistanceFromBeginningComparator(conversor, definicao));
    }

    private static <T> Comparator<T> getDistanceFromBeginningComparator(Function<T, IEntityTaskVersion> conversor,
                                                                        FlowDefinition<?> definicao) {
        return (o1, o2) -> compareByDistanceFromBeginning(conversor.apply(o1), conversor.apply(o2), definicao);
    }

    public static <X extends IEntityTaskVersion> List<X> getSortedByDistanceFromBeginning(List<X> situacoes,
            FlowDefinition<?> definicao) {
        List<X> novo = new ArrayList<>(situacoes);
        novo.sort((s1, s2) -> compareByDistanceFromBeginning(s1, s2, definicao));
        return novo;
    }

    public static List<STask<?>> getSortedTasksByDistanceFromBeginning(FlowDefinition<?> definicao) {
        FlowMap flowMap = definicao.getFlowMap();
        calculateTaskOrder(flowMap);
        List<STask<?>> novo = new ArrayList<>(flowMap.getTasks());
        novo.sort((t1, t2) -> {
            int order1 = t1.getOrder();
            int order2 = t2.getOrder();
            if (order1 != order2) {
                return order1 - order2;
            }
            return t1.getName().compareTo(t2.getName());
        });
        return novo;
    }

    static void calculateTaskOrder(FlowMap flowMap) {
        for (STask<?> task : flowMap.getTasks()) {
            task.setOrder(0);
        }
        Deque<STask<?>> deque = new ArrayDeque<>();
        orderedVisit(0, flowMap.getStart().getTask(), deque);
        flowMap.getTasks().stream().filter(task -> task.getOrder() == 0)
                .forEach(task -> task.setOrder(calculateWeight(task) + 1000000));
    }

    private static void orderedVisit(int previousValue, STask<?> task, Deque<STask<?>> deque) {
        int valor = previousValue + calculateWeight(task);
        int order = task.getOrder();
        if (order == 0 || (order < valor && !deque.contains(task))) {
            task.setOrder(valor);
            deque.add(task);
            for (STransition transicao : task.getTransitions()) {
                if (task.getDefaultTransition() == transicao) {
                    orderedVisit(valor, transicao.getDestination(), deque);
                } else {
                    orderedVisit(valor + 1, transicao.getDestination(), deque);
                }
            }
            deque.removeLast();
        }
    }

    private static int calculateTaskOrder(IEntityTaskVersion entityTaskDefinition, FlowDefinition<?> flowDefinition) {
        if (!flowDefinition.getEntityProcessDefinition()
                .equals(entityTaskDefinition.getProcessVersion().getProcessDefinition())) {
            throw new SingularFlowException("Mistura de situações de definições diferrentes");
        }
        Optional<STask<?>> task = flowDefinition.getFlowMap().getTaskByAbbreviation(entityTaskDefinition.getAbbreviation());
        if (task.isPresent()) {
            return task.get().getOrder();
        }
        if (entityTaskDefinition.isPeople()) {
            return 10000000 + PESO_TASK_PESSOA;
        } else if (entityTaskDefinition.isWait()) {
            return 10000000 + PESO_TASK_WAIT;
        } else if (entityTaskDefinition.isEnd()) {
            return 10000000 + PESO_TASK_FIM;
        } else {
            return 10000000 + PESO_TASK_JAVA;
        }
    }

    private static int calculateWeight(STask<?> task) {
        IEntityTaskType tt = task.getTaskType();
        if (tt.isPeople()) {
            return PESO_TASK_PESSOA;
        } else if (tt.isJava()) {
            return PESO_TASK_JAVA;
        } else if (tt.isWait()) {
            return PESO_TASK_WAIT;
        } else if (tt.isEnd()) {
            return PESO_TASK_FIM;
        }
        throw new SingularFlowException(task.getTaskType() + " não tratado", task);
    }

    /** Faz a injeção de beans no objeto informado, se o mesmo necessitar. */
    @Nonnull
    public static <V> V inject(@Nonnull STask<?> task, @Nonnull V target) {
        return task.inject(target);
    }

}
