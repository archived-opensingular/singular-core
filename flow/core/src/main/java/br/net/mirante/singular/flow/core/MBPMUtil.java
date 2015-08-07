package br.net.mirante.singular.flow.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;

public class MBPMUtil {

    private static final int PESO_TASK_JAVA = 100;
    private static final int PESO_TASK_WAIT = 300;
    private static final int PESO_TASK_PESSOA = 1000;
    private static final int PESO_TASK_FIM = 100000;

    public static void ordenarTarefasPorDistanciaInicio(List<? extends ProcessInstance> instancias,
            final ProcessDefinition<?> definicao) {
        Collections.sort(instancias,
                (s1, s2) -> comparePorDistanciaInicio(s1.getDemanda().getSituacao(), s2.getDemanda().getSituacao(), definicao));
    }

    private static int comparePorDistanciaInicio(IEntityTaskDefinition s1, IEntityTaskDefinition s2, ProcessDefinition<?> definicao) {
        int ordem1 = calcularOrdem(s1, definicao);
        int ordem2 = calcularOrdem(s2, definicao);
        if (ordem1 != ordem2) {
            return ordem1 - ordem2;
        }
        return s1.getNome().compareTo(s2.getNome());
    }

    public static <T> void ordenarPorDistanciaInicio(List<? extends T> lista, Function<T, IEntityTaskDefinition> conversor,
            ProcessDefinition<?> definicao) {
        Collections.sort(lista, getComparatorPorDistanciaInicio(conversor, definicao));
    }

    private static <T> Comparator<T> getComparatorPorDistanciaInicio(Function<T, IEntityTaskDefinition> conversor,
            ProcessDefinition<?> definicao) {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return comparePorDistanciaInicio(conversor.apply(o1), conversor.apply(o2), definicao);
            }
        };
    }

    public static <X extends IEntityTaskDefinition> List<X> orderPorDistanciaInicio(List<X> situacoes, ProcessDefinition<?> definicao) {
        List<X> novo = new ArrayList<>(situacoes);
        Collections.sort(novo, (s1, s2) -> comparePorDistanciaInicio(s1, s2, definicao));
        return novo;
    }

    public static List<MTask<?>> ordernarTaskPorDistanciaInicio(ProcessDefinition<?> definicao) {
        adicionarIndeceDeOrdem(definicao.getFluxo());
        List<MTask<?>> novo = new ArrayList<>(definicao.getFluxo().getTasks());
        Collections.sort(novo, (t1, t2) -> {
            if (t1.getOrder() != t2.getOrder()) {
                return t1.getOrder() - t2.getOrder();
            }
            return t1.getName().compareTo(t2.getName());
        });
        return novo;
    }

    static void adicionarIndeceDeOrdem(FlowMap mapa) {
        // if (mapa.getTaskInicial().getOrdem() != 0) {
        // return;
        // }
        for (MTask<?> task : mapa.getTasks()) {
            task.setOrder(0);
        }
        Deque<MTask<?>> pilha = new ArrayDeque<>();
        percorrerOrdenando(0, mapa.getTaskInicial(), pilha);
        for (MTask<?> task : mapa.getTasks()) {
            if (task.getOrder() == 0) {
                task.setOrder(calcularPeso(task) + 1000000);
            }
        }
    }

    private static void percorrerOrdenando(int valorAnterior, MTask<?> task, Deque<MTask<?>> pilha) {
        int valor = valorAnterior + calcularPeso(task);
        if (task.getOrder() == 0 || (task.getOrder() < valor && !pilha.contains(task))) {
            task.setOrder(valor);
            pilha.add(task);
            for (MTransition transicao : task.getTransicoes()) {
                if (task.getDefaultTransition() == transicao) {
                    percorrerOrdenando(valor, transicao.getDestination(), pilha);
                } else {
                    percorrerOrdenando(valor + 1, transicao.getDestination(), pilha);
                }
            }
            pilha.removeLast();
        }
    }

    private static int calcularOrdem(IEntityTaskDefinition s, ProcessDefinition<?> definicao) {
        if (!definicao.getDadosDefinicao().equals(s.getDefinicao())) {
            throw new RuntimeException("Mistura de situações de definições diferrentes");
        }
        MTask<?> task = definicao.getFluxo().getTaskWithSigla(s.getSigla());
        if (task != null) {
            return task.getOrder();
        }
        if (s.isPessoa()) {
            return 10000000 + PESO_TASK_PESSOA;
        } else if (s.isWait()) {
            return 10000000 + PESO_TASK_WAIT;
        } else if (s.isFim()) {
            return 10000000 + PESO_TASK_FIM;
        } else {
            return 10000000 + PESO_TASK_JAVA;
        }
    }

    private static int calcularPeso(MTask<?> task) {
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
