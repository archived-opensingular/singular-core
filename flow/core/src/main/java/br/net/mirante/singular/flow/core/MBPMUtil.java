package br.net.mirante.singular.flow.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;

public class MBPMUtil {

    private static final int PESO_TASK_JAVA = 100;
    private static final int PESO_TASK_WAIT = 300;
    private static final int PESO_TASK_PESSOA = 1000;
    private static final int PESO_TASK_FIM = 100000;

    public static void sortInstancesByDistanceFromBeginning(List<? extends ProcessInstance> instancias,
            final ProcessDefinition<?> definicao) {
        instancias.sort((s1, s2) -> compareByDistanceFromBeginning(s1.getEntity().getSituacao(), s2.getEntity().getSituacao(), definicao));
    }

    private static int compareByDistanceFromBeginning(IEntityTaskDefinition s1, IEntityTaskDefinition s2, ProcessDefinition<?> definicao) {
        int ordem1 = calculateTaskOrder(s1, definicao);
        int ordem2 = calculateTaskOrder(s2, definicao);
        if (ordem1 != ordem2) {
            return ordem1 - ordem2;
        }
        return s1.getNome().compareTo(s2.getNome());
    }

    public static <T> void sortByDistanceFromBeginning(List<? extends T> lista, Function<T, IEntityTaskDefinition> conversor,
            ProcessDefinition<?> definicao) {
        lista.sort(getDistanceFromBeginningComparator(conversor, definicao));
    }

    private static <T> Comparator<T> getDistanceFromBeginningComparator(Function<T, IEntityTaskDefinition> conversor,
            ProcessDefinition<?> definicao) {
        return (o1, o2) -> compareByDistanceFromBeginning(conversor.apply(o1), conversor.apply(o2), definicao);
    }

    public static <X extends IEntityTaskDefinition> List<X> getSortedByDistanceFromBeginning(List<X> situacoes, ProcessDefinition<?> definicao) {
        List<X> novo = new ArrayList<>(situacoes);
        novo.sort((s1, s2) -> compareByDistanceFromBeginning(s1, s2, definicao));
        return novo;
    }

    public static List<MTask<?>> getSortedTasksByDistanceFromBeginning(ProcessDefinition<?> definicao) {
        calculateTaskOrder(definicao.getFlowMap());
        List<MTask<?>> novo = new ArrayList<>(definicao.getFlowMap().getTasks());
        Collections.sort(novo, (t1, t2) -> {
            if (t1.getOrder() != t2.getOrder()) {
                return t1.getOrder() - t2.getOrder();
            }
            return t1.getName().compareTo(t2.getName());
        });
        return novo;
    }

    static void calculateTaskOrder(FlowMap flowMap) {
        for (MTask<?> task : flowMap.getTasks()) {
            task.setOrder(0);
        }
        Deque<MTask<?>> deque = new ArrayDeque<>();
        orderedVisit(0, flowMap.getStartTask(), deque);
        for (MTask<?> task : flowMap.getTasks()) {
            if (task.getOrder() == 0) {
                task.setOrder(calculateWeight(task) + 1000000);
            }
        }
    }

    private static void orderedVisit(int previousValue, MTask<?> task, Deque<MTask<?>> deque) {
        int valor = previousValue + calculateWeight(task);
        if (task.getOrder() == 0 || (task.getOrder() < valor && !deque.contains(task))) {
            task.setOrder(valor);
            deque.add(task);
            for (MTransition transicao : task.getTransitions()) {
                if (task.getDefaultTransition() == transicao) {
                    orderedVisit(valor, transicao.getDestination(), deque);
                } else {
                    orderedVisit(valor + 1, transicao.getDestination(), deque);
                }
            }
            deque.removeLast();
        }
    }

    private static int calculateTaskOrder(IEntityTaskDefinition entityTaskDefinition, ProcessDefinition<?> processDefinition) {
        if (!processDefinition.getEntity().equals(entityTaskDefinition.getDefinicao())) {
            throw new RuntimeException("Mistura de situações de definições diferrentes");
        }
        MTask<?> task = processDefinition.getFlowMap().getTaskWithAbbreviation(entityTaskDefinition.getSigla());
        if (task != null) {
            return task.getOrder();
        }
        if (entityTaskDefinition.isPessoa()) {
            return 10000000 + PESO_TASK_PESSOA;
        } else if (entityTaskDefinition.isWait()) {
            return 10000000 + PESO_TASK_WAIT;
        } else if (entityTaskDefinition.isFim()) {
            return 10000000 + PESO_TASK_FIM;
        } else {
            return 10000000 + PESO_TASK_JAVA;
        }
    }

    private static int calculateWeight(MTask<?> task) {
        switch (task.getTaskType()) {
            case People:
                return PESO_TASK_PESSOA;
            case Java:
                return PESO_TASK_JAVA;
            case Wait:
                return PESO_TASK_WAIT;
            case End:
                return PESO_TASK_FIM;
            default:
                throw new RuntimeException(task.getTaskType() + " não tratado");
        }
    }

    public static final RuntimeException generateError(ProcessInstance instance, String message) {
        return new RuntimeException(instance.getClass().getName() + " - " + instance.getFullId() + " : " + message);
    }

    public static String convertToJavaIdentity(String original) {
        return convertToJavaIdentity(original, false);
    }

    public static String convertToJavaIdentity(String original, boolean firstCharacterUpperCase) {
        StringBuilder sb = new StringBuilder(original.length());
        boolean nextUpper = false;
        for (char c : original.toCharArray()) {
            if (sb.length() == 0) {
                if (Character.isJavaIdentifierStart(c)) {
                    if (firstCharacterUpperCase) {
                        sb.append(Character.toUpperCase(c));
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
                }
            } else if (Character.isJavaIdentifierPart(c)) {
                if (nextUpper) {
                    c = Character.toUpperCase(c);
                    nextUpper = false;
                }
                sb.append(c);
            } else if (Character.isWhitespace(c)) {
                nextUpper = true;
            }
        }
        return sb.toString();
    }
}
